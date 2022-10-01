package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.amethyst_imbuement.scepter.SummonFamiliarAugment
import net.minecraft.block.Blocks
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.CatEntity
import net.minecraft.entity.passive.CatVariant
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.InventoryChangedListener
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.util.*

@Suppress("PrivatePropertyName")
class ImbuedFamiliarEntity(entityType: EntityType<ImbuedFamiliarEntity>, world: World):
    GolemEntity(entityType,world),
    Angerable, PlayerCreatable,
    RideableInventory,
    JumpingMount, InventoryChangedListener{

    constructor(entityType: EntityType<ImbuedFamiliarEntity>, world: World, createdBy: LivingEntity? = null,
                modDamage: Double = 0.0, modHealth: Double = 0.0, invSlots: Int = 3, level: Int = 1) : this(entityType, world){
        modifiedDamage = modDamage
        modifiedHealth = modHealth
        inventorySlots = invSlots
        this.level = level
        this.createdBy = createdBy?.uuid
        this.owner = createdBy
        if (createdBy != null) {
            goalSelector.add(2, FollowSummonerGoal(this,createdBy, 1.0, 10.0f, 2.0f, false))
            targetSelector.add(1, TrackSummonerAttackerGoal(this,createdBy))
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

        fun createImbuedFamiliarAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, baseAttackSpeed.toDouble())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
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
    private var level = 1
    private var items = SimpleInventory(3 + inventorySlots)
    private val CAT_VARIANT = DataTracker.registerData(CatEntity::class.java, TrackedDataHandlerRegistry.CAT_VARIANT)

    init{
        stepHeight = 1.0f
        items.addListener(this)
        updateArmor()
    }

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(3, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(3, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(4, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(CAT_VARIANT, CatVariant.BLACK)
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        this.initEquipment(world.random, difficulty)
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
        if (world.isClient) {
            return
        }
        setArmorTypeFromStack(items.getStack(0))
    }
    private fun setArmorTypeFromStack(stack: ItemStack) {
        this.equipArmor(stack)
        if (!world.isClient) {
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)!!.removeModifier(HORSE_ARMOR_BONUS_ID)
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
    fun getArmorType(): ItemStack? {
        return getEquippedStack(EquipmentSlot.CHEST)
    }
    private fun equipArmor(stack: ItemStack) {
        equipStack(EquipmentSlot.CHEST, stack)
        setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f)
    }
    private fun isHorseArmor(item: ItemStack): Boolean {
        return item.item is HorseArmorItem
    }

    override fun tick() {
        super.tick()
        if (maxAge > 0){
            if (age >= maxAge){
                kill()
            }
        }
    }

    override fun tickMovement() {
        super.tickMovement()
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
        if (lookingAtVillagerTicksLeft > 0) {
            --lookingAtVillagerTicksLeft
        }
        if (!world.isClient) {
            tickAngerLogic(world as ServerWorld, true)
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
        return dataTracker.get(CAT_VARIANT)
    }

    fun setVariant(variant: CatVariant) {
        dataTracker.set(CAT_VARIANT, variant)
    }

    fun getTexture(): Identifier {
        return getVariant().texture()
    }

    fun setLevel(level: Int){
        this.level = level
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putDouble("ModifiedHealth", modifiedHealth)
        nbt.putDouble("ModifiedDamage", modifiedDamage)
        nbt.putInt("InventorySlots",inventorySlots)
        nbt.putInt("Level",level)
        nbt.put("Items", (items.toNbtList()))
        nbt.putString("variant", Registry.CAT_VARIANT.getId(this.getVariant()).toString())
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
        readPlayerCreatedNbt(world, nbt)
        readAngerFromNbt(world, nbt)
        updateArmor()
    }

    override fun canTarget(target: LivingEntity): Boolean {
        if (!isPlayerCreated()) return super.canTarget(target)
        val uuid = target.uuid
        if (owner != null) {
            if (target.isTeammate(owner)) return false
        }
        return uuid != createdBy
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
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
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

    override fun openInventory(player: PlayerEntity?) {
        TODO("Not yet implemented")
    }

    override fun setJumpStrength(strength: Int) {
        TODO("Not yet implemented")
    }

    override fun canJump(): Boolean {
        return true
    }

    override fun startJumping(height: Int) {
        TODO("Not yet implemented")
    }

    override fun stopJumping() {
    }

    override fun isClimbing(): Boolean {
        return false
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        val itemStack = player.getStackInHand(hand)
        if (isHorseArmor(itemStack)) {
            openInventory(player)
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
        if (item.isOf(Items.WHEAT)) {
            f = 2.0f
        } else if (item.isOf(Items.SUGAR)) {
            f = 1.0f
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
        TODO("Not yet implemented")
    }

}