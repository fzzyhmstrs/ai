package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity_util.Scalable
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.block.BlockState
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName", "LeakingThis")
open class PlayerCreatedConstructEntity(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World): GolemEntity(entityType,world),
    Angerable, PlayerCreatable, ModifiableEffectEntity, Tameable, Scalable {

    constructor(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World, ageLimit: Int = -1, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : this(entityType, world){
        maxAge = ageLimit

        this.createdBy = createdBy?.uuid
        this.owner = createdBy
        if (createdBy != null) {
            startTrackingOwner(createdBy)
        }

        if (augmentEffect != null){
            this.passEffects(augmentEffect,level)
        }

        if (world is ServerWorld) {
            initialize(world,world.getLocalDifficulty(this.blockPos),SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    companion object{
        internal val SCALE = DataTracker.registerData(PlayerCreatedConstructEntity::class.java,TrackedDataHandlerRegistry.FLOAT)
    }

    protected var attackTicksLeft = 0
    protected var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    override var maxAge = -1
    override var createdBy: UUID? = null
    override var owner: LivingEntity? = null
    private var angryAt: UUID? = null
    override var entityEffects: AugmentEffect = AugmentEffect()
    private var level = 1
    open var entityGroup: EntityGroup = EntityGroup.DEFAULT
    internal var entityScale = 1f
    protected var trackingOwner = false

    override fun passEffects(ae: AugmentEffect, level: Int) {
        this.entityEffects = ae.copy()
        this.level = level
    }

    init{
        stepHeight = 1.0f
    }

    private fun startTrackingOwner(currentOwner: LivingEntity){
        goalSelector.add(2, FollowSummonerGoal(this,currentOwner, 1.0, 10.0f, 2.0f, false))
        targetSelector.add(1, TrackSummonerAttackerGoal(this,currentOwner))
        trackingOwner = true
    }

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(3, WanderNearTargetGoal(this, 0.9, 16.0f))
        goalSelector.add(3, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(4, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, true))
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

    override fun getGroup(): EntityGroup {
        return entityGroup
    }

    open fun setGroup(entityGroup: EntityGroup){
        this.entityGroup = entityGroup
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeAngerToNbt(nbt)
        nbt.putInt("maximum_age", maxAge)
        nbt.putInt("effect_level", level)
        nbt.putFloat("scale_factor",getScale())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        if (owner != null) {
            try {
                goalSelector.add(2, FollowSummonerGoal(this, owner!!, 1.0, 10.0f, 2.0f, false))
                targetSelector.add(1, TrackSummonerAttackerGoal(this, owner!!))
            } catch (e: Exception){
                //
            }
        }
        readAngerFromNbt(world, nbt)
        this.maxAge = nbt.getInt("maximum_age").takeIf { it > 0 } ?: -1
        this.level = nbt.getInt("effect_level").takeIf { it > 0 } ?: 1
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
        if (getOwner() != null) {
            if (target.isTeammate(getOwner())) return false
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

    open fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(getStepSound(), 0.15f, 1.0f)
    }

    override fun tryAttack(target: Entity): Boolean {
        val summoner = getOwner()
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

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    fun setConstructOwner(owner: LivingEntity?){
        createdBy = owner?.uuid
        this.owner = owner
    }

    override fun getOwnerUuid(): UUID? {
        return createdBy
    }

    override fun getOwner(): Entity? {
        return if (owner != null) {
            owner
        } else if (world is ServerWorld && createdBy != null) {
            val o = (world as ServerWorld).getEntity(createdBy)
            if (o != null && o is LivingEntity) {
                owner = o
                if (!trackingOwner){
                    startTrackingOwner(o)
                }
            }
            o
        }else {
            null
        }
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

    //need to also do the bounding boxxxxxxx
    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
    }

}