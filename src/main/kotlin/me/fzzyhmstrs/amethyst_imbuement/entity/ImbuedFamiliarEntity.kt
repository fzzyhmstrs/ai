package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.amethyst_imbuement.scepter.SummonFamiliarAugment
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuedFamiliarInventoryScreenHandlerFactory
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.CatVariant
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.InventoryChangedListener
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.*
import net.minecraft.util.math.*
import net.minecraft.util.registry.Registry
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.util.*

@Suppress("PrivatePropertyName")
class ImbuedFamiliarEntity(entityType: EntityType<ImbuedFamiliarEntity>, world: World):
    PathAwareEntity(entityType,world),
    Angerable, PlayerCreatable,
    RideableInventory,
    JumpingMount, InventoryChangedListener{

    constructor(entityType: EntityType<ImbuedFamiliarEntity>, world: World, createdBy: LivingEntity? = null,
                modDamage: Double = 0.0, modHealth: Double = 0.0, invSlots: Int = 3, level: Int = 1,
                followMode: Int = 0, attackMode: Int = 0) : this(entityType, world){
        modifiedDamage = modDamage
        modifiedHealth = modHealth
        inventorySlots = invSlots
        this.followMode = this.followMode.fromIndex(followMode)
        this.attackMode = this.attackMode.fromIndex(attackMode)
        this.level = level
        this.createdBy = createdBy?.uuid
        this.owner = createdBy
        if (createdBy != null) {
            goalSelector.add(2, FamiliarFollowSummonerGoal(this,createdBy))
            targetSelector.add(1, FamiliarTrackSummonerAttackerGoal(this,createdBy))
        }
        if (world is ServerWorld) {
            initialize(world,world.getLocalDifficulty(this.blockPos),SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    companion object {
        private  val baseMaxHealth = AiConfig.entities.familiarBaseHealth
        private val baseAttackSpeed = AiConfig.entities.familiarBaseAttackSpeed
        private const val baseMoveSpeed = 0.3
        private  val baseAttackDamage = AiConfig.entities.familiarBaseDamage
        private val HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295")
        private val CAT_ARMOR_BASE_STRING = AI.MOD_ID + ":textures/entity/familiar/armor/cat_armor_"

        fun createImbuedFamiliarAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, baseAttackSpeed.toDouble())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
                .add(EntityAttributes.HORSE_JUMP_STRENGTH,1.2)
        }
    }
    private var attackTicksLeft = 0
    private var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    override var createdBy: UUID? = null
    override var maxAge: Int = -1
    override var owner: LivingEntity? = null
    private var angryAt: UUID? = null
    private var modifiedDamage = 0.0
    private var modifiedHealth = 0.0
    private var inventorySlots = 3
    internal var followMode: FollowMode = FollowMode.FOLLOW
    internal var attackMode: AttackMode = AttackMode.ATTACK
    private var level = 1
    private var variant: CatVariant = CatVariant.ALL_BLACK
    internal var items = FamiliarInventory(1 + inventorySlots)
    internal var catArmorTex = Identifier("empty")
    private var jumpStrength = 0f
    private var inAir = false

    init{
        stepHeight = 1.0f
        items.addListener(this)
        updateArmor()
    }

    override fun initGoals() {
        goalSelector.add(1, SwimGoal(this))
        goalSelector.add(2, FamiliarAttackGoal(this))
        //goalSelector.add(3, WanderAroundGoal(this, 0.7,60,false))
        goalSelector.add(5, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(6, LookAroundGoal(this))
        targetSelector.add(2, FamiliarRevengeGoal(this))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, true) { entity: LivingEntity? -> shouldAngerAt(entity)})
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, true) { entity: LivingEntity? -> canTargetMob(entity) })
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        if (modifiedDamage > baseAttackDamage){
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(
                EntityAttributeModifier(
                    "Modified damage bonus",
                    modifiedDamage - baseAttackDamage,
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
        }
        if (modifiedHealth > baseMaxHealth){
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(
                EntityAttributeModifier(
                    "Modified health bonus",
                    modifiedHealth - baseMaxHealth,
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    private fun updateArmor(){
        val stack = items.getStack(0)
        val item = stack.item
        if (item is HorseArmorItem) {
            val horseArmorIdString = item.entityTexture.toString()
            val substring1 = horseArmorIdString.substring(0,horseArmorIdString.length-4)
            val lastUnderscore = substring1.lastIndexOf('_')
            val subString2 = if (lastUnderscore > 0){
                substring1.substring(lastUnderscore + 1,substring1.length)
            } else {
                "iron"
            }
            catArmorTex = Identifier(CAT_ARMOR_BASE_STRING + subString2)
            println(catArmorTex)
        }
        if (world.isClient) {
            return
        }
        setArmorTypeFromStack(stack)
    }
    private fun setArmorTypeFromStack(stack: ItemStack) {
        this.equipArmor(stack)
        if (!world.isClient) {
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.removeModifier(HORSE_ARMOR_BONUS_ID)
            if (this.isHorseArmor(stack)) {

                val bonus = (stack.item as HorseArmorItem).bonus
                if (bonus != 0) {
                    getAttributeInstance(EntityAttributes.GENERIC_ARMOR)!!.addTemporaryModifier(
                        EntityAttributeModifier(
                            HORSE_ARMOR_BONUS_ID,
                            "Horse armor bonus",
                            bonus.toDouble(),
                            EntityAttributeModifier.Operation.ADDITION
                        )
                    )
                }
            }
        }
    }

    fun getCatArmorTex(stack: ItemStack): Identifier{
        if (catArmorTex == Identifier("empty")){
            val item = stack.item
            if (item is HorseArmorItem) {
                val horseArmorIdString = item.entityTexture.toString()
                val substring1 = horseArmorIdString.substring(0,horseArmorIdString.length-4)
                val lastUnderscore = substring1.lastIndexOf('_')
                val subString2 = if (lastUnderscore > 0){
                    substring1.substring(lastUnderscore + 1,substring1.length)
                } else {
                    "iron"
                }
                val id = Identifier(CAT_ARMOR_BASE_STRING + subString2)
                catArmorTex = id
                return id
            }
        }
        return catArmorTex
    }
    fun getArmorType(): ItemStack {
        return getEquippedStack(EquipmentSlot.CHEST)
    }
    private fun equipArmor(stack: ItemStack) {

        equipStack(EquipmentSlot.CHEST, stack)
        setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f)
    }
    internal fun isHorseArmor(item: ItemStack): Boolean {
        return item.item is HorseArmorItem
    }

    override fun canWalkOnFluid(state: FluidState): Boolean {
        return true
    }

    override fun tickMovement() {
        super.tickMovement()
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
        if (!world.isClient) {
            tickAngerLogic(world as ServerWorld, true)
        }
    }

    override fun travel(movementInput: Vec3d) {
        if (!this.isAlive) {
            return
        }
        val entity: Entity? = this.primaryPassenger
        val livingEntity = if(entity is LivingEntity){ entity} else {null}
        if (!hasPassengers() || livingEntity == null) {
            airStrafingSpeed = 0.02f
            super.travel(movementInput)
            return
        }
        this.yaw = livingEntity.yaw
        prevYaw = yaw
        pitch = livingEntity.pitch * 0.5f
        setRotation(yaw, pitch)
        headYaw = yaw.also { bodyYaw = it }
        val f = livingEntity.sidewaysSpeed * 0.5f
        var g = livingEntity.forwardSpeed
        if (g <= 0.0f) {
            g *= 0.35f
        }
        if (jumpStrength > 0.0f && !this.isInAir() && onGround) {
            val d: Double = getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * jumpStrength.toDouble() * this.jumpVelocityMultiplier.toDouble()
            val e = d + this.jumpBoostVelocityModifier
            val vec3d = velocity
            this.setVelocity(vec3d.x, e, vec3d.z)
            this.setInAir(true)
            velocityDirty = true
            if (g > 0.0f) {
                val h = MathHelper.sin(yaw * (Math.PI.toFloat() / 180))
                val i = MathHelper.cos(yaw * (Math.PI.toFloat() / 180))
                velocity =
                    velocity.add((-0.4f * h * jumpStrength).toDouble(), 0.0, (0.4f * i * jumpStrength).toDouble())
            }
            jumpStrength = 0.0f
        }
        airStrafingSpeed = movementSpeed * 0.1f
        if (this.isLogicalSideForUpdatingMovement) {
            movementSpeed = getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED).toFloat()
            super.travel(Vec3d(f.toDouble(), movementInput.y, g.toDouble()))
        } else if (livingEntity is PlayerEntity) {
            velocity = Vec3d.ZERO
        }
        if (onGround) {
            jumpStrength = 0.0f
            setInAir(false)
        }
        updateLimbs(this, false)
        tryCheckBlockCollision()
    }

    override fun getPrimaryPassenger(): LivingEntity? {
        val entity: Entity? = this.firstPassenger
        return if (entity is LivingEntity) {
            entity
        } else null
    }

    override fun updatePassengerPosition(passenger: Entity) {
        super.updatePassengerPosition(passenger)
        if (passenger is MobEntity) {
            bodyYaw = passenger.bodyYaw
        }
    }

    override fun chooseRandomAngerTime() {
        setAngerTime(ANGER_TIME_RANGE[random])
    }

    override fun setAngerTime(ticks: Int) {
        angerTime = ticks
    }

    override fun getAngerTime(): Int {
        return angerTime
    }

    override fun setAngryAt(uuid: UUID?) {
        angryAt = uuid
    }

    override fun getAngryAt(): UUID? {
        return angryAt
    }

    fun getVariant(): CatVariant {
        return variant
    }

    fun setVariant(variant: CatVariant) {
        println("whoo!")
        println(world.isClient)
        this.variant = variant
    }

    fun getTexture(): Identifier {
        return variant.texture()
    }

    fun setLevel(level: Int){
        this.level = level
    }

    private fun isInAir(): Boolean {
        return this.inAir
    }

    private fun setInAir(inAir: Boolean) {
        this.inAir = inAir
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putDouble("ModifiedHealth", modifiedHealth)
        nbt.putDouble("ModifiedDamage", modifiedDamage)
        nbt.putInt("InventorySlots",inventorySlots)
        nbt.putInt("Level",level)
        nbt.put("Items", (items.toNbtList()))
        nbt.putString("variant", Registry.CAT_VARIANT.getId(this.getVariant()).toString())

        followMode.toNbt(nbt)
        attackMode.toNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeAngerToNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        modifiedHealth = nbt.getDouble("ModifiedHealth")
        modifiedDamage = nbt.getDouble("ModifiedDamage")
        inventorySlots = nbt.getInt("InventorySlots")
        level = nbt.getInt("Level")
        items.readNbtList(nbt.getList("Items",10))
        val catVariant = Registry.CAT_VARIANT[Identifier.tryParse(nbt.getString("variant"))]
        if (catVariant != null) {
            this.setVariant(catVariant)
        }

        followMode = followMode.fromNbt(nbt)
        attackMode = attackMode.fromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        readAngerFromNbt(world, nbt)
        updateArmor()
    }

    override fun canTarget(target: LivingEntity): Boolean {
        if (attackMode == AttackMode.PASSIVE) return false
        if (!isPlayerCreated()) return super.canTarget(target)
        val uuid = target.uuid
        if (owner != null) {
            if (target.isTeammate(owner)) return false
        }
        return if (attackMode == AttackMode.REVENGE) {
            target == recentDamageSource?.source
        }else {
            uuid != createdBy
        }
    }

    private fun canTargetMob(target: LivingEntity?): Boolean{
        if (target !is Monster) return false
        if (attackMode == AttackMode.ATTACK) return true
        if (attackMode == AttackMode.REVENGE) return target == recentDamageSource?.source
        return false
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
        } else if (status.toInt() == 11) {
            lookingAtVillagerTicksLeft = 400
        } else if (status.toInt() == 34) {
            lookingAtVillagerTicksLeft = 0
        } else {
            super.handleStatus(status)
        }
    }

    override fun getMinAmbientSoundDelay(): Int {
        return 240
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_CAT_PURR
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_CAT_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_CAT_DEATH
    }

    override fun getGroup(): EntityGroup? {
        return EntityGroup.DEFAULT
    }

    override fun tryAttack(target: Entity): Boolean {
        val summoner = owner
        if (summoner != null && summoner is PlayerEntity && target is LivingEntity){
            (target as PlayerHitTimerAccessor).setPlayerHitTimer(100)
        }
        val bl = super.tryAttack(target)
        if (bl) {
            val f = world.getLocalDifficulty(blockPos).localDifficulty
            if (this.mainHandStack.isEmpty && this.isOnFire && random.nextFloat() < f * 0.3f) {
                target.setOnFireFor(2 * f.toInt())
            }
        }
        return bl
    }

    override fun onDeath(source: DamageSource) {
        SummonFamiliarAugment.removeActiveFamiliar(this)
        super.onDeath(source)
    }

    override fun onRemoved() {
        SummonFamiliarAugment.removeActiveFamiliar(this)
        super.onRemoved()
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    override fun openInventory(player: PlayerEntity) {
        val screenHandlerFactory = ImbuedFamiliarInventoryScreenHandlerFactory(this,this.modifiedDamage,modifiedHealth,inventorySlots,level)
        player.openHandledScreen(screenHandlerFactory)
    }

    override fun setJumpStrength(strength: Int) {
        var str = strength
        if (str < 0) {
            str = 0
        }
        jumpStrength = if (str >= 90) 1.0f else 0.4f + 0.4f * str.toFloat() / 90.0f
    }

    override fun canJump(): Boolean {
        return true
    }

    override fun startJumping(height: Int) {
        playSound(SoundEvents.ENTITY_RABBIT_JUMP, 1.0f, 1.2f)
    }

    override fun stopJumping() {
    }

    override fun isClimbing(): Boolean {
        return false
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        val itemStack = player.getStackInHand(hand)
        if (isHorseArmor(itemStack) || player.shouldCancelInteraction()) {
            openInventory(player)
            return ActionResult.success(world.isClient)
        } else if (itemStack.isFood){
            receiveFood(itemStack)
            return ActionResult.success(world.isClient)
        }
        putPlayerOnBack(player)
        return ActionResult.success(world.isClient)
    }
    private fun putPlayerOnBack(player: PlayerEntity) {
        if (!world.isClient) {
            player.yaw = yaw
            player.pitch = pitch
            player.startRiding(this)
        }
    }
    private fun receiveFood(item: ItemStack): Boolean {
        var bl = false
        var f = 0.0f
        if (item.isOf(Items.COOKED_CHICKEN)) {
            f = 4.0f
        } else if (item.isOf(Items.COOKED_SALMON) || item.isOf(Items.COOKED_COD)) {
            f = 3.0f
        } else if (item.isOf(Items.COD) || item.isOf(Items.SALMON) || item.isOf(Items.CHICKEN)) {
            f = 1.5f
        }
        if (this.health < this.maxHealth && f > 0.0f) {
            heal(f)
            bl = true
        }
        if (f> 0) {
            world.addParticle(
                ParticleTypes.HAPPY_VILLAGER,
                getParticleX(1.0), this.randomBodyY + 0.5, getParticleZ(1.0), 0.0, 0.0, 0.0
            )
            bl = true
        }
        if (bl) {
            this.emitGameEvent(GameEvent.EAT)
        }
        return bl
    }

    override fun onInventoryChanged(sender: Inventory?) {
        val itemStack = getArmorType()
        updateArmor()
        val itemStack2 = getArmorType()
        if (isHorseArmor(itemStack2) && itemStack != itemStack2) {
            playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5f, 1.0f)
        }
    }

    private fun locateSafeDismountingPos(offset: Vec3d, passenger: LivingEntity): Vec3d? {
        val d = this.x + offset.x
        val e = boundingBox.minY
        val f = this.z + offset.z
        val mutable = BlockPos.Mutable()
        block0@ for (entityPose in passenger.poses) {
            mutable[d, e] = f
            val g = boundingBox.maxY + 0.75
            do {
                val h = world.getDismountHeight(mutable)
                val vec3d = Vec3d(d, mutable.y.toDouble() + h, f)
                val box: Box = passenger.getBoundingBox(entityPose)
                if (mutable.y.toDouble() + h > g) continue@block0
                if (Dismounting.canDismountInBlock(h) && Dismounting.canPlaceEntityAt(
                        world,
                        passenger,
                        box.offset(vec3d)
                    )
                ) {
                    passenger.pose = entityPose
                    return vec3d
                }
                mutable.move(Direction.UP)
            } while (mutable.y.toDouble() < g)
        }
        return null
    }

    override fun updatePassengerForDismount(passenger: LivingEntity): Vec3d? {
        val vec3d = getPassengerDismountOffset(
            this.width.toDouble(),
            passenger.width.toDouble(),
            yaw + if (passenger.mainArm == Arm.RIGHT) 90.0f else -90.0f
        )
        val vec3d2 = locateSafeDismountingPos(vec3d, passenger)
        if (vec3d2 != null) {
            return vec3d2
        }
        val vec3d3 = getPassengerDismountOffset(
            this.width.toDouble(),
            passenger.width.toDouble(),
            yaw + if (passenger.mainArm == Arm.LEFT) 90.0f else -90.0f
        )
        val vec3d4 = locateSafeDismountingPos(vec3d3, passenger)
        return vec3d4 ?: pos
    }

    private class FamiliarFollowSummonerGoal(private val summoned: ImbuedFamiliarEntity, summoner: LivingEntity):
            FollowSummonerGoal(summoned, summoner, 1.0, 10.0f, 2.0f, false){

        override fun canStart(): Boolean {
            if (summoned.followMode != FollowMode.FOLLOW) return false
            return  super.canStart()
        }

        override fun shouldContinue(): Boolean {
            if (summoned.followMode != FollowMode.FOLLOW) return false
            return super.shouldContinue()
        }
    }

    private class FamiliarTrackSummonerAttackerGoal(private val summoned: ImbuedFamiliarEntity, summoner: LivingEntity): TrackSummonerAttackerGoal(summoned, summoner){

        override fun canStart(): Boolean {
            if (summoned.attackMode != AttackMode.ATTACK) return false
            return super.canStart()
        }

        override fun shouldContinue(): Boolean {
            if (summoned.attackMode != AttackMode.ATTACK) return false
            return super.shouldContinue()
        }

    }

    private class FamiliarRevengeGoal(private val familiar: ImbuedFamiliarEntity): RevengeGoal(familiar,*arrayOfNulls(0)){

        override fun canStart(): Boolean {
            if (familiar.attackMode != AttackMode.ATTACK && familiar.attackMode != AttackMode.REVENGE) return false
            return super.canStart()
        }

        override fun shouldContinue(): Boolean {
            if (familiar.attackMode != AttackMode.ATTACK && familiar.attackMode != AttackMode.REVENGE) return false
            return super.shouldContinue()
        }

    }

    internal enum class FollowMode(val index: Int, val key: String){
        STAY(0, "familiar.follow_mode.stay"),
        FOLLOW(1,"familiar.follow_mode.follow");

        fun toNbt(nbt: NbtCompound){
            nbt.putInt("FollowMode",this.index)
        }

        fun fromNbt(nbt: NbtCompound): FollowMode{
            val index = nbt.getInt("FollowMode")
            return fromIndex(index)
        }

        fun fromIndex(index:Int): FollowMode{
            for (mode in values()){
                if (mode.index == index){
                    return mode
                }
            }
            return FOLLOW
        }

        fun cycle(): FollowMode{
            for (mode in values()){
                if (mode.index > index){
                    return mode
                }
            }
            return STAY
        }
    }

    internal enum class AttackMode(val index: Int, val key: String){
        ATTACK(0, "familiar.attack_mode.attack"),
        PASSIVE(1, "familiar.attack_mode.passive"),
        REVENGE(2, "familiar.attack_mode.revenge");

        fun toNbt(nbt: NbtCompound){
            nbt.putInt("FollowMode",this.index)
        }

        fun fromNbt(nbt: NbtCompound): AttackMode{
            val index = nbt.getInt("FollowMode")
            return fromIndex(index)
        }

        fun fromIndex(index:Int): AttackMode{
            for (mode in values()){
                if (mode.index == index){
                    return mode
                }
            }
            return ATTACK
        }

        fun cycle(): AttackMode{
            for (mode in values()){
                if (mode.index > index){
                    return mode
                }
            }
            return ATTACK
        }
    }

    class FamiliarInventory(size: Int): SimpleInventory(size) {
        override fun readNbtList(nbtList: NbtList) {
            for (i in 0 until nbtList.size) {
                val compound = nbtList.getCompound(i)
                val stack = ItemStack.fromNbt(compound)
                if (stack.isEmpty) continue
                val slot = compound.getInt("slot")
                this.setStack(slot, stack)
            }
        }

        override fun toNbtList(): NbtList {
            val list = NbtList()
            for (i in 0 until this.size()) {
                val compound = NbtCompound()
                compound.putInt("slot", i)
                val stack = this.getStack(i)
                stack.writeNbt(compound)
                list.add(compound)
            }
            return list
        }

        private var dirtyChecked: Boolean = false
        override fun markDirty() {
            if (dirtyChecked) {
                dirtyChecked = false
                return
            }
            dirtyChecked = true
            super.markDirty()
        }

        override fun onClose(player: PlayerEntity?) {
            markDirty()
        }
    }


    class FamiliarAttackGoal(private val mob: MobEntity) : Goal()
    {
        var target: LivingEntity? = null
        var cooldown = 0

        init{
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK))
        }

        override fun canStart(): Boolean {
            val livingEntity: LivingEntity = mob.getTarget() ?: return false
            this.target = livingEntity
            return true
        }

        override fun shouldContinue(): Boolean {
            if (!this.target!!.isAlive) {
                return false
            }
            return if (mob.squaredDistanceTo(this.target) > 225.0) {
                false
            } else !mob.getNavigation().isIdle() || canStart()
        }

        override fun stop() {
            this.target = null
            mob.getNavigation().stop()
        }

        override fun shouldRunEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            mob.getLookControl().lookAt(this.target, 30.0f, 30.0f)
            val d: Double = (mob.getWidth() * 2.0f * (mob.getWidth() * 2.0f)).toDouble()
            val e: Double = mob.squaredDistanceTo(this.target!!.x, this.target!!.y, this.target!!.z)
            var f = 0.8
            if (e > d && e < 16.0) {
                f = 1.33
            } else if (e < 225.0) {
                f = 0.6
            }
            println("blop")
            mob.getNavigation().startMovingTo(this.target, f)
            println(System.currentTimeMillis())
            cooldown = Math.max(cooldown - 1, 0)
            if (e > d) {
                return
            }
            if (cooldown > 0) {
                return
            }
            cooldown = 20
            mob.tryAttack(this.target)
        }
    }
}