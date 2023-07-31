package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.CallForConstructHelpGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.FollowSummonerGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.TrackSummonerAttackerGoal
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TimeHelper
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EntityView
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName", "LeakingThis")
open class PlayerCreatedConstructEntity(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World, settings: Settings = Settings()): AttackTicksGolemEntity(entityType,world),
    Angerable, PlayerCreatable, ModifiableEffectEntity, Tameable, Scalable {

    constructor(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World, ageLimit: Int = -1, createdBy: LivingEntity? = null, settings: Settings = Settings()) : this(entityType, world, settings){
        maxAge = ageLimit

        this.createdBy = createdBy?.uuid
        this.entityOwner = createdBy
        if (createdBy != null) {
            startTrackingOwner(createdBy)
        }
    }

    companion object{
        protected val SCALE = DataTracker.registerData(PlayerCreatedConstructEntity::class.java,TrackedDataHandlerRegistry.FLOAT)
    }

    private val followSummonerGoal = FollowSummonerGoal(this, null, 1.0, 10.0f, 2.0f, false)
    private val callForConstructHelpGoal = CallForConstructHelpGoal(this)
    private val trackSummonerAttackerGoal = TrackSummonerAttackerGoal(this,null)


    protected var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    override var maxAge = -1
    
    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    private var angryAt: UUID? = null
    protected var trackingOwner = false
        
    override var entityEffects: AugmentEffect = AugmentEffect()
    override var level = 1
    override var spells: PairedAugments = PairedAugments()
    override var modifiableEffects = ModifiableEffectContainer()
    override var processContext: ProcessContext = ProcessContext.FROM_ENTITY_CONTEXT
    
    open var entityGroup: EntityGroup = EntityGroup.DEFAULT
    internal var entityScale = 1f

    override fun passEffects(spells: PairedAugments, ae: AugmentEffect, level: Int) {
        super.passEffects(spells, ae, level)
        val chk = world
        if (chk is ServerWorld) {
            initialize(chk,chk.getLocalDifficulty(this.blockPos),SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    init{
        stepHeight = 1.0f
        if (!world.isClient)
            initOwnerGoals(settings)
    }

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(4, WanderNearTargetGoal(this, 0.9, 16.0f))
        goalSelector.add(4, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(5, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))

        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, true))
    }

    private fun initOwnerGoals(settings: Settings){
        if (settings.follow)
            goalSelector.add(2, followSummonerGoal)
        if (settings.callHelp)
            goalSelector.add(3,callForConstructHelpGoal)
        if (settings.track)
            targetSelector.add(1, trackSummonerAttackerGoal)
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        this.initEquipment(world.random, difficulty)
        modifyHealth(entityEffects.amplifier(level).toDouble())
        modifyDamage(entityEffects.damage(level).toDouble())
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(SCALE,1f)
    }

    open fun modifyHealth(modHealth: Double){
        val baseHealth = this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
        if (modHealth != baseHealth && modHealth != 0.0){
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(
                EntityAttributeModifier(
                    "Modified health bonus",
                    modHealth - baseHealth,
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
        }
    }

    open fun modifyDamage(modDamage: Double){
        val baseDamage = this.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)
        if (modDamage != baseDamage && modDamage != 0.0){
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(
                EntityAttributeModifier(
                    "Modified damage bonus",
                    modDamage - baseDamage,
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
        }
    }

    override fun tick() {
        super.tick()
        tickTickEffects(this, owner, processContext)
        if (maxAge > 0){
            if (age >= maxAge){
                kill()
            }
        }
        if (owner != null){
            val attacker = owner?.recentDamageSource?.attacker
            if (attacker != null && attacker is LivingEntity){
                this.target = attacker
                setAngryAt(attacker.uuid)
                chooseRandomAngerTime()
            }
        }
    }

    override fun tickMovement() {
        super.tickMovement()
        if (lookingAtVillagerTicksLeft > 0) {
            --lookingAtVillagerTicksLeft
        }
        if (!world.isClient) {
            tickAngerLogic(world as ServerWorld, true)
        }
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        if (source.isIn(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) {
            this.callForConstructHelpGoal.onHurt()
        }
        runEffect(ModifiableEffectEntity.ON_DAMAGED,this,owner,processContext)
        return super.damage(source, amount)
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

    override fun getGroup(): EntityGroup {
        return entityGroup
    }

    open fun setGroup(entityGroup: EntityGroup){
        this.entityGroup = entityGroup
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeModifiableNbt(nbt)
        writeAngerToNbt(nbt)
        nbt.putFloat("scale_factor",getScale())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        readModifiableNbt(nbt)
        if (owner != null) {
            try {
                startTrackingOwner(owner!!)
            } catch (e: Exception){
                //
            }
        }
        readAngerFromNbt(world, nbt)
        setScale(nbt.getFloat("scale_factor").takeIf { it > 0f } ?: 1f)
    }

    fun setLookingAtVillager(lookingAtVillager: Boolean) {
        if (lookingAtVillager) {
            lookingAtVillagerTicksLeft = 400
            world.sendEntityStatus(this, 11.toByte())
        } else {
            lookingAtVillagerTicksLeft = 0
            world.sendEntityStatus(this, 34.toByte())
        }
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
        if (status.toInt() == 11) {
            lookingAtVillagerTicksLeft = 400
        } else if (status.toInt() == 34) {
            lookingAtVillagerTicksLeft = 0
        } else {
            super.handleStatus(status)
        }
    }

    open fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(getStepSound(), 0.15f, 1.0f)
    }

    override fun tryAttack(target: Entity): Boolean {
        val summoner = owner
        if (summoner != null && summoner is PlayerEntity && target is LivingEntity){
            (target as PlayerHitTimerAccessor).setPlayerHitTimer(100)
        }
        var f = getBaseDamage()
        var g = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).toFloat()
        if (target is LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.mainHandStack, target.group)
            g += EnchantmentHelper.getKnockback(this).toFloat()
        }
        val hit = EntityHitResult(target)
        f = if (summoner is SpellCastingEntity) {
            spells.provideDealtDamage(f, processContext, hit, summoner, world, Hand.MAIN_HAND, level, entityEffects)
        } else {
            f
        }
        EnchantmentHelper.getFireAspect(this).also {i -> if (i > 0) target.setOnFireFor(i * 4)}
        val damageSource = if (summoner is SpellCastingEntity) {
            spells.provideDamageSource(processContext,hit,this,summoner,world,Hand.MAIN_HAND, level,entityEffects)
        } else {
            this.damageSources.mobAttack(this)
        }

        val bl = target.damage(damageSource,f)
        if (bl) {
            runEffect(ModifiableEffectEntity.DAMAGE,this,owner,processContext)
            applyDamageEffects(this, target)
            val d = world.getLocalDifficulty(blockPos).localDifficulty
            if (this.mainHandStack.isEmpty && this.isOnFire && random.nextFloat() < f * 0.3f)
                target.setOnFireFor((2 * d).toInt())
            applyKnockback(g,target)
            if (target is PlayerEntity)
                disablePlayerShield(target, this.mainHandStack, if (target.isUsingItem) target.activeItem else ItemStack.EMPTY)
            if (!target.isAlive)
                runEffect(ModifiableEffectEntity.KILL,this,owner,processContext)
        }
        return bl
    }

    protected open fun attackEffects(target: Entity,damageSource: DamageSource, damage: Float){

    }

    protected open fun getBaseDamage(): Float{
        return this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
    }

    protected open fun applyKnockback(g: Float, target: Entity){
        if (g > 0.0f && target is LivingEntity) {
            target.takeKnockback((g * 0.5f).toDouble(), MathHelper.sin(yaw * (Math.PI.toFloat() / 180)).toDouble(), -MathHelper.cos(yaw * (Math.PI.toFloat() / 180)).toDouble())
            velocity = velocity.multiply(0.6, 1.0, 0.6)
        }
    }

    protected fun disablePlayerShield(player: PlayerEntity, mobStack: ItemStack, playerStack: ItemStack) {
        if (!mobStack.isEmpty && !playerStack.isEmpty && mobStack.item is AxeItem && playerStack.item is ShieldItem) {
            val f = 0.25f + EnchantmentHelper.getEfficiency(this).toFloat() * 0.05f
            if (random.nextFloat() < f) {
                player.itemCooldownManager[playerStack.item] = 100
                world.sendEntityStatus(player, EntityStatuses.BREAK_SHIELD)
            }
        }
    }

    override fun remove(reason: RemovalReason) {
        processContext.beforeRemoval()
        runEffect(ModifiableEffectEntity.ON_REMOVED,this,owner,processContext)
        super.remove(reason)
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    fun setConstructOwner(owner: LivingEntity?){
        createdBy = owner?.uuid
        this.entityOwner = owner
        if (!trackingOwner && owner != null){
            startTrackingOwner(owner)
        }
    }

    override fun getOwnerUuid(): UUID? {
        return createdBy
    }

    override fun method_48926(): EntityView {
        return this.world
    }

    override fun getOwner(): LivingEntity? {
        return if (entityOwner != null) {
            entityOwner
        } else if (world is ServerWorld && createdBy != null) {
            val o = (world as ServerWorld).getEntity(createdBy)
            if (o != null && o is LivingEntity) {
                this.entityOwner = o
                if (!trackingOwner){
                    startTrackingOwner(o)
                }
                o
            } else {
                null
            }

        }else {
            null
        }
    }

    private fun startTrackingOwner(currentOwner: LivingEntity){
        followSummonerGoal.setSummoner(currentOwner)
        trackSummonerAttackerGoal.setSummoner(currentOwner)
        trackingOwner = true
    }

    override fun onTrackedDataSet(data: TrackedData<*>?) {
        super.onTrackedDataSet(data)
        if(SCALE.equals(data)){
            entityScale = getScale()
        }
    }

    override fun getScale(): Float {
        return dataTracker.get(SCALE)
    }

    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
        this.calculateDimensions()
    }

    override fun getDimensions(pose: EntityPose): EntityDimensions? {
        return super.getDimensions(pose).scaled(getScale())
    }

    class Settings(val follow: Boolean = true, val callHelp: Boolean = true, val track: Boolean = true)

}
