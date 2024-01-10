package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalAttackGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalPriorityTargetGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterNetworking
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.block.BlockState
import net.minecraft.client.render.entity.feature.SkinOverlayOwner
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import java.util.*
import kotlin.math.log
import kotlin.math.max

class SardonyxElementalEntity(entityType: EntityType<out HostileEntity>?, world: World?) : HostileEntity(entityType, world), SkinOverlayOwner {

    companion object{

        protected val CHARGING: TrackedData<Int> = DataTracker.registerData(SardonyxElementalEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        protected val FIRING_PROJECTILES: TrackedData<Boolean> = DataTracker.registerData(SardonyxElementalEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        fun createElementalAttributes(): DefaultAttributeContainer.Builder {
            return createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.sardonyxElemental.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
                .add(EntityAttributes.GENERIC_ARMOR, AiConfig.entities.sardonyxElemental.baseArmor.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.sardonyxElemental.baseDamage.get())
        }
    }

    override fun initGoals() {
        goalSelector.add(8, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(8, LookAroundGoal(this))
        initCustomGoals()
    }

    protected fun initCustomGoals() {
        //goalSelector.add(1, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(2, SardonyxElementalAttackGoal(this, { damageMultiplier }, {t -> spellOnAttack(t)}, { this }, {bl -> setFiringProjectiles(bl) }))
        goalSelector.add(7, WanderAroundGoal(this, 1.0))
        targetSelector.add(1, SardonyxElementalPriorityTargetGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(2, ActiveTargetGoal(this as MobEntity, PlayerCreatedConstructEntity::class.java, true))
        targetSelector.add(3, ActiveTargetGoal(this as MobEntity, PlayerEntity::class.java, true))
        targetSelector.add(5, ActiveTargetGoal(this as MobEntity, IronGolemEntity::class.java, true))

    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CHARGING,0)
        dataTracker.startTracking(FIRING_PROJECTILES,false)
    }

    override fun getNextAirUnderwater(air: Int): Int {
        return air
    }

    protected var attackTicksLeft = 0
    protected var quartersReached = 0
    internal var lastDamageTracker = LastDamageTracker()
    internal var spellCooldown = 60
    private var lastSpellWasBuff = true
    private var highDamageCalcify = -1
    private var damageMultiplier = 1f
    private val bossBar = ServerBossBar(displayName,BossBar.Color.RED,BossBar.Style.NOTCHED_12)

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        quartersReached = nbt.getInt("quartersReached")
        setFiringProjectiles(nbt.getBoolean("firingProjectiles"))
        if (hasCustomName()) {
            bossBar.name = this.displayName
        }
        lastDamageTracker = LastDamageTracker.fromNbt(nbt.getCompound("lastDamageTracker"))
        damageMultiplier = nbt.getFloat("damageMultiplier").takeIf { it > 0f } ?: 1f
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("firingProjectiles", getFiringProjectiles())
        nbt.putInt("quartersReached", quartersReached)
        nbt.put("lastDamageTracker", lastDamageTracker.toNbt())
        nbt.putFloat("damageMultiplier",damageMultiplier)
    }

    override fun setCustomName(name: Text?) {
        super.setCustomName(name)
        bossBar.name = this.displayName
    }

    override fun tickMovement() {
        val charge = dataTracker.get(CHARGING)
        if (charge > 0) {
            setCharging(charge - 1)
        }
        if (!getCharging())
            super.tickMovement()
        if (highDamageCalcify > 0)
            highDamageCalcify--
        if (spellCooldown > 0) {
            spellCooldown--
        } else if (!lastSpellWasBuff) {
            if (lastDamageTracker.getAverageDamage() > 10f){
                if (lastDamageTracker.isMagic()){
                    Calcify.INSTANCE.magicCalcify(this)
                } else {
                    Calcify.INSTANCE.physicalCalcify(this)
                }
            } else {
                DeathCloud.INSTANCE.cloud(this)
            }
            lastSpellWasBuff = true
            spellCooldown = AiConfig.entities.sardonyxElemental.spellActivationCooldown.get()
        }
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
    }

    override fun mobTick() {
        super.mobTick()
        if (EventRegistry.ticker_20.isReady()){
            this.heal(AiConfig.entities.sardonyxElemental.amountHealedPerSecond.get())
        }
        bossBar.percent = this.health / this.maxHealth
    }

    override fun checkDespawn() {
        if (world.difficulty == Difficulty.PEACEFUL && this.isDisallowedInPeaceful) {
            discard()
            return
        }
        despawnCounter = 0
    }

    override fun isAffectedBySplashPotions(): Boolean {
        return false
    }

    override fun onStartedTrackingBy(player: ServerPlayerEntity?) {
        super.onStartedTrackingBy(player)
        bossBar.addPlayer(player)
    }

    override fun onStoppedTrackingBy(player: ServerPlayerEntity?) {
        super.onStoppedTrackingBy(player)
        bossBar.removePlayer(player)
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
        } else {
            super.handleStatus(status)
        }
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }

    override fun getAmbientSound(): SoundEvent {
        return RegisterSound.ELEMENTAL_RUMBLE
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent {
        return RegisterSound.ELEMENTAL_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return RegisterSound.ELEMENTAL_DEATH
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState?) {
        playSound(RegisterSound.STOMP, 1.0f, 1.0f)
    }

    fun getCharging(): Boolean{
        return dataTracker.get(CHARGING) > 0
    }

    fun setCharging(bl: Boolean) {
        dataTracker.set(CHARGING,if (bl) 100 else 0)
    }

    fun setCharging(amount: Int) {
        dataTracker.set(CHARGING,amount)
    }

    fun getFiringProjectiles(): Boolean{
        return dataTracker.get(FIRING_PROJECTILES)
    }

    fun setFiringProjectiles(bl: Boolean){
        dataTracker.set(FIRING_PROJECTILES,bl)
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        var realAmount = amount
        if (getCharging() && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false
        if (realAmount >= 50f && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            if (highDamageCalcify <= 0) {
                Calcify.INSTANCE.emergencyCalcify(this)
                highDamageCalcify = 600
            }
            realAmount = 50f
        }else if (realAmount >= 8f){
            highDamageCalcify -= 5
            spellCooldown -= 5
        }
        val lastHealth = this.health
        val bl = super.damage(source, realAmount)
        if (bl && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            if (atOrBelowQuarter()){
                if (world is ServerWorld){
                    if (quartersReached <= 2) {
                        val target = this.target
                        if (target != null) {
                            val rot = Vec3d(target.x - this.x,target.getBodyY(0.5) - this.getBodyY(0.5),target.z - this.z).normalize()
                            DevastationBeam.INSTANCE.fire(world as ServerWorld,this,this.pos.add(0.0,this.height/2.0,0.0),rot)
                        }
                    } else {
                        val targetList = lastDamageTracker.getPrioritiesForDevastation(world as ServerWorld)
                        for (target in targetList){
                            val rot = Vec3d(target.x - this.x,target.getBodyY(0.5) - target.getBodyY(0.5),target.z - target.z).normalize()
                            DevastationBeam.INSTANCE.fire(world as ServerWorld,this,this.pos.add(0.0,this.height/2.0,0.0),rot)
                        }
                    }
                }
                val nearbyPossibleAttackers = (world.getOtherEntities(this,this.boundingBox.expand(7.0)) {it is PlayerEntity || it is GolemEntity || it is LivingEntity && it is Tameable})
                if (nearbyPossibleAttackers.size > 8) {
                    FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth <= 0.75f)
                    FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                } else if (nearbyPossibleAttackers.size > 4){
                    FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                }
                setCharging(100)
            }
            source.attacker?.damage(FzzyDamage.thorns(this), max(5f,5f*damageMultiplier))
            lastDamageTracker.addDamage(source, lastHealth - this.health)
        }
        return bl
    }

    private fun spellOnAttack(target: Entity){
        if (spellCooldown <= 0 && !world.isClient && lastSpellWasBuff){
            val nearbyPossibleAttackers = (world.getOtherEntities(this,this.boundingBox.expand(7.0)) {it is PlayerEntity || it is GolemEntity || it is LivingEntity && it is Tameable}) .map { it as LivingEntity }
            if (lastDamageTracker.getNumberOfAttackers() < 3 && nearbyPossibleAttackers.size < 4){
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
            } else {
                for (entity in nearbyPossibleAttackers){
                    if (entity is ServerPlayerEntity)
                        RegisterNetworking.sendPlayerKnockback(entity,this.pos.subtract(entity.pos).normalize(),2.5)
                    else
                        entity.takeKnockback(2.5,this.x - entity.x, this.z - entity.z)
                }
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)

            }
            lastSpellWasBuff = false
            spellCooldown = AiConfig.entities.sardonyxElemental.spellActivationCooldown.get()
        }
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        spellOnAttack(target)
        if (!AiConfig.entities.shouldItHit(this,target,AiConfig.Entities.Options.NONE)) return false
        val i: Int = EnchantmentHelper.getFireAspect(this)
        var f = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
        var g = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).toFloat()
        if (target is LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.mainHandStack, target.group)
            g += EnchantmentHelper.getKnockback(this).toFloat()
        }
        if (i > 0) {
            target.setOnFireFor(i * 4)
        }
        if (target.damage(FzzyDamage.mobAttack(this), f)) {
            if (g > 0.0f && target is LivingEntity) {
                target.takeKnockback(
                    (g * 0.5f).toDouble(), MathHelper.sin(yaw * (Math.PI.toFloat() / 180)).toDouble(), -MathHelper.cos(
                        yaw * (Math.PI.toFloat() / 180)
                    ).toDouble()
                )
                velocity = velocity.multiply(0.6, 1.0, 0.6)
            }
            if (target is PlayerEntity) {
                disablePlayerShield(target, this.mainHandStack, if (target.isUsingItem) target.activeItem else ItemStack.EMPTY)
            }
            val list = world.getNonSpectatingEntities(LivingEntity::class.java, target.boundingBox.expand(2.0, 0.25, 2.0))
            for (splashTarget in list){
                if (AiConfig.entities.shouldItHit(this,splashTarget,AiConfig.Entities.Options.NONE)){
                    if (splashTarget.damage(FzzyDamage.mobAttack(this), f/2f)){
                        if (g > 0.0f && splashTarget is LivingEntity) {
                            splashTarget.takeKnockback(
                                (g * 0.25f).toDouble(), MathHelper.sin(yaw * (Math.PI.toFloat() / 180)).toDouble(), -MathHelper.cos(
                                    yaw * (Math.PI.toFloat() / 180)
                                ).toDouble()
                            )
                            //velocity = velocity.multiply(0.6, 1.0, 0.6)
                        }
                        applyDamageEffects(this, splashTarget)
                    }
                }
            }
            applyDamageEffects(this, target)
            onAttacking(target)
            return true
        }
        return false
    }

    private fun disablePlayerShield(player: PlayerEntity, mobStack: ItemStack, playerStack: ItemStack) {
        if (!mobStack.isEmpty && !playerStack.isEmpty && mobStack.item is AxeItem && playerStack.isOf(Items.SHIELD)) {
            val f = 0.25f + EnchantmentHelper.getEfficiency(this).toFloat() * 0.05f
            if (random.nextFloat() < f) {
                player.itemCooldownManager[Items.SHIELD] = 100
                world.sendEntityStatus(player, EntityStatuses.BREAK_SHIELD)
            }
        }
    }

    private fun atOrBelowQuarter(): Boolean{
        return when (quartersReached){
            0-> {
                if (health/maxHealth <= 0.75f){
                    health = maxHealth * 0.75f
                    quartersReached = 1
                    true
                } else {
                    false
                }
            }
            1-> {
                if (health/maxHealth <= 0.5f){
                    health = maxHealth * 0.5f
                    quartersReached = 2
                    true
                } else {
                    false
                }
            }
            2-> {
                if (health/maxHealth <= 0.25f){
                    health = maxHealth * 0.25f
                    quartersReached = 3
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }

    private val healthUUID = UUID.fromString("6b75d6f8-8d86-11ee-b9d1-0242ac120002")
    private val armorUUID = UUID.fromString("6b75da4a-8d86-11ee-b9d1-0242ac120002")
    private val damageUUID = UUID.fromString("6b75db94-8d86-11ee-b9d1-0242ac120002")
    private val knockbackUUID = UUID.fromString("95c16616-8d86-11ee-b9d1-0242ac120002")

    fun analyzeBreakingPlayer(player: PlayerEntity){
        var damage = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat() + EnchantmentHelper.getAttackDamage(player.mainHandStack, this.group) * player.getAttackCooldownProgress(0.5f)
        for (i in 0 until 9) {
            val stack = player.inventory.getStack(i)
            if (stack.isEmpty) continue
            if (stack.item !is ToolItem) continue
            val attributes = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
            val container = EntityAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE){}
            container.baseValue = player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            for (attribute in attributes.get(EntityAttributes.GENERIC_ATTACK_DAMAGE)){
                container.addTemporaryModifier(attribute)
            }
            val f = container.value.toFloat()
            val g = EnchantmentHelper.getAttackDamage(stack, this.group) * player.getAttackCooldownProgress(0.5f)
            damage = max(f + g, damage)

        }


        val othersNearby = player.world.getNonSpectatingEntities(MobEntity::class.java,player.boundingBox.expand(7.0)).filter { it !is Monster && if (it !is Tameable) it !is PassiveEntity else true }
        var friendHealth = 0f
        var friendTotalDamage = 0f
        var friendCount = 0
        for (friend in othersNearby){
            friendHealth += friend.maxHealth
            if (friend.attributes.hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                friendTotalDamage += friend.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
            friendCount++
        }

        val averageFriendDamage = if (friendCount > 0) friendTotalDamage/friendCount else 0f

        damage += averageFriendDamage
        val tankMultiplier = ((damage/40f) * (damage/40f)).toDouble()

        var damageMultiplier = 0.0
        damageMultiplier += player.getStatusEffect(StatusEffects.RESISTANCE)?.amplifier?.plus(1)?.times(0.15) ?: 0.0
        val armor = player.armor
        val toughness = player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
        val test = DamageUtil.getInflictedDamage(DamageUtil.getDamageLeft(20f,armor.toFloat(),toughness.toFloat()),EnchantmentHelper.getProtectionAmount(player.armorItems,FzzyDamage.mobAttack(this)).toFloat())

        damageMultiplier += log(20f/test,40f)/2.0
        damageMultiplier += max(log((friendHealth + player.maxHealth) / 80f,30f).toDouble(),0.0)/2.0

        val knockbackMultiplier = max(0.0,(((friendCount + 1)/10.0) - 1.0))

        this.damageMultiplier = max((damageMultiplier/2f).toFloat() + 1f,1f)

        if (tankMultiplier > 0.0) {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(
                EntityAttributeModifier(
                    healthUUID,
                    "Modified health bonus",
                    tankMultiplier * 2.0,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.addPersistentModifier(
                EntityAttributeModifier(
                    armorUUID,
                    "Modified armor bonus",
                    tankMultiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
        }
        if (damageMultiplier > 0.0)
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(
                EntityAttributeModifier(
                    damageUUID,
                    "Modified damage bonus",
                    damageMultiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
        if (knockbackMultiplier > 0.0)
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.addPersistentModifier(
                EntityAttributeModifier(
                    knockbackUUID,
                    "Modified knockback bonus",
                    knockbackMultiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
        this.health = this.maxHealth
    }


    internal class LastDamageTracker(private val lastAttacks: LinkedList<Pair<DamageSource,Float>> = LinkedList(),
            private val attackers: MutableMap<UUID, Float> = mutableMapOf(),
            private var averageDamage: Float = 0f,
            private var numberOfAttackers: Int = 0,
            private var standoutAttacker: UUID? = null,
            private var damageIsMagic: Boolean = false){

        fun toNbt(): NbtCompound{
            val nbt = NbtCompound()
            if (attackers.isNotEmpty()) {
                val attackersList = NbtList()
                for (attacker in attackers) {
                    val attackerNbt = NbtCompound()
                    attackerNbt.putUuid("attackerUuid", attacker.key)
                    attackerNbt.putFloat("attackerDamage", attacker.value)
                    attackersList.add(attackerNbt)
                }
                nbt.put("attackersList", attackersList)
            }
            if (averageDamage != 0f)
                nbt.putFloat("averageDamage",averageDamage)
            if (numberOfAttackers != 0)
                nbt.putInt("numberOfAttackers",numberOfAttackers)
            if (standoutAttacker != null)
                nbt.putUuid("standoutAttacker",standoutAttacker)
            nbt.putBoolean("damageIsMagic",damageIsMagic)

            return nbt
        }

        companion object{
            fun fromNbt(nbt: NbtCompound): LastDamageTracker{
                val attackers: MutableMap<UUID, Float> = mutableMapOf()
                if (nbt.contains("attackersList")) {
                    val attackersList = nbt.getList("attackersList", NbtElement.COMPOUND_TYPE.toInt())
                    for (element in attackersList){
                        val compound = element as? NbtCompound ?: continue
                        val uuid = compound.getUuid("attackerUuid")
                        val damage = compound.getFloat("attackerDamage")
                        attackers[uuid] = damage
                    }
                }
                val averageDamage = nbt.getFloat("averageDamage")
                val numberOfAttackers = nbt.getInt("numberOfAttackers")
                val standoutAttacker = if(nbt.containsUuid("standoutAttacker")) nbt.getUuid("standoutAttacker") else null
                val damageIsMagic = nbt.getBoolean("damageIsMagic")
                return LastDamageTracker(LinkedList(), attackers, averageDamage, numberOfAttackers, standoutAttacker, damageIsMagic)
            }
        }

        fun addDamage(source: DamageSource, amount: Float){
            lastAttacks.addLast(Pair(source,amount))
            if (lastAttacks.size > 15)
                lastAttacks.pop()
            attackers.clear()
            var totalDmg = 0f
            var damageMagic = 0
            for (s in lastAttacks){
                if (s.first.isIn(DamageTypeTags.WITCH_RESISTANT_TO))
                    damageMagic++
                val le = (s.first.attacker as? LivingEntity)?: continue
                val currentDmg = attackers.computeIfAbsent(le.uuid){0f}
                totalDmg += s.second
                attackers[le.uuid] = currentDmg + s.second
            }
            if (damageMagic > lastAttacks.size/2)
                damageIsMagic = true
            averageDamage = totalDmg / lastAttacks.size
            numberOfAttackers = attackers.size
            var currentStandout = 0f
            standoutAttacker = null
            for (attacker in attackers){
                if (attacker.value > (totalDmg * 0.4f)) {
                    if (attacker.value > currentStandout){
                        standoutAttacker = attacker.key
                        currentStandout = attacker.value
                    }
                }
            }
        }

        fun hasPriority(): Boolean{
            return standoutAttacker != null
        }

        fun getPriority(world: World): LivingEntity?{
            if (world !is ServerWorld) return null
            return world.getEntity(standoutAttacker) as? LivingEntity
        }

        fun getPrioritiesForDevastation(world: ServerWorld): List<LivingEntity>{
            if (attackers.isEmpty()) return listOf()
            if (attackers.size <= 3){
                return attackers.keys.mapNotNull { world.getEntity(it) as? LivingEntity }
            }
            val targetList: MutableList<LivingEntity> = mutableListOf()
            val sortedMap: SortedMap<Float,UUID> = sortedMapOf(kotlin.Comparator.reverseOrder())
            for (pair in attackers){
                sortedMap[pair.value] = pair.key
            }
            for (pair in sortedMap){
                val entity = world.getEntity(pair.value) as? LivingEntity ?: continue
                targetList.add(entity)
                if (targetList.size >= 3) return targetList
            }
            return targetList
        }

        fun getAverageDamage(): Float{
            return averageDamage
        }

        fun getNumberOfAttackers(): Int{
            return numberOfAttackers
        }

        fun isMagic(): Boolean{
            return damageIsMagic
        }

    }

    override fun shouldRenderOverlay(): Boolean {
        return getCharging()
    }


}
