package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity_util.Scalable
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.block.BlockState
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EntityView
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName", "LeakingThis")
open class ChorseEntity(entityType: EntityType<out ChorseEntity>, world: World): AbstractHorseEntity(entityType,world),
    PlayerCreatable, ModifiableEffectEntity, Scalable {

    constructor(entityType: EntityType<out ChorseEntity>, world: World, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : this(entityType, world){
        this.createdBy = createdBy?.uuid
        this.entityOwner = createdBy
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
        protected val HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295")
        internal val SCALE = DataTracker.registerData(ChorseEntity::class.java,TrackedDataHandlerRegistry.FLOAT)
        fun createChorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, AiConfig.entities.chorse.baseJumpStrength.get())
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.chorse.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.chorse.baseMoveSpeed.get())
        }
    }

    private val followSummonerGoal = FollowSummonerGoal(this, null, 1.0, 10.0f, 2.0f, false)
    private val callForConstructHelpGoal = CallForConstructHelpGoal(this)

    override var maxAge = -1
    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
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
        if (!world.isClient)
            initOwnerGoals()
    }

    override fun initGoals() {
        goalSelector.add(0, SwimGoal(this))
        goalSelector.add(1, EscapeDangerGoal(this, 1.2))
        goalSelector.add(6, WanderAroundFarGoal(this, 0.7))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
    }

    private fun initOwnerGoals(){
        goalSelector.add(2,followSummonerGoal)
        goalSelector.add(3,callForConstructHelpGoal)
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

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        if (source.isIn(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) {
            this.callForConstructHelpGoal.onHurt()
        }
        return super.damage(source, amount)
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
        nbt.putInt("effect_level", level)
        nbt.putFloat("scale_factor",getScale())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        if (owner != null) {
            try {
                startTrackingOwner(owner!!)
            } catch (e: Exception){
                //
            }
        }
        this.level = nbt.getInt("effect_level").takeIf { it > 0 } ?: 1
        setScale(nbt.getFloat("scale_factor").takeIf { it > 0f } ?: 1f)
    }

    override fun canTarget(target: LivingEntity): Boolean {
        if (!isPlayerCreated()) return super.canTarget(target)
        val uuid = target.uuid
        if (getOwner() != null) {
            if (target.isTeammate(getOwner())) return false
        }
        return uuid != createdBy
    }

    open fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_STEP
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(getStepSound(), 0.15f, 1.0f)
    }

    override fun tryAttack(target: Entity): Boolean {
        val bl = super.tryAttack(target)
        if (bl) {
            val summoner = getOwner()
            if (summoner != null && summoner is PlayerEntity && target is LivingEntity){
                (target as PlayerHitTimerAccessor).setPlayerHitTimer(100)
            }
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

    //need to also do the bounding boxxxxxxx
    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
    }

}
