package me.fzzyhmstrs.amethyst_imbuement.entity.living

import fzzyhmstrs.should_i_hit_that.api.TemporarySummon
import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity_util.Scalable
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.CallForConstructHelpGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.FleeCreeperGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.FollowSummonerGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.TrackSummonerAttackerGoal
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.fzzy_core.trinket_util.TrinketUtil
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
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EntityView
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName", "LeakingThis")
open class PlayerCreatedConstructEntity(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World): GolemEntity(entityType,world),
    Angerable, PlayerCreatable, ModifiableEffectEntity, Tameable, Scalable, TemporarySummon {

    constructor(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World, ageLimit: Int = -1, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : this(entityType, world){
        maxAge = ageLimit

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
        internal val SCALE = DataTracker.registerData(PlayerCreatedConstructEntity::class.java,TrackedDataHandlerRegistry.FLOAT)
    }

    private val followSummonerGoal = FollowSummonerGoal(this, null, 1.0, 20.0f, 2.0f, false)
    private val callForConstructHelpGoal = CallForConstructHelpGoal(this)
    private val trackSummonerAttackerGoal = TrackSummonerAttackerGoal(this,null)


    protected var attackTicksLeft = 0
    protected var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    override var maxAge = -1
    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    private var angryAt: UUID? = null
    override var entityEffects: AugmentEffect = AugmentEffect()
    protected var level = 1
    open var entityGroup: EntityGroup = EntityGroup.DEFAULT
    internal var entityScale = 1f
    protected var trackingOwner = false
    protected var spell: Identifier? = null

    override fun passEffects(ae: AugmentEffect, level: Int) {
        this.entityEffects = ae.copy()
        this.level = level
    }

    open fun setSpell(spell: ScepterAugment){
        this.spell = FzzyPort.ENCHANTMENT.getId(spell)
    }

    open fun getSpell(): ScepterAugment? {
        return FzzyPort.ENCHANTMENT.get(spell) as? ScepterAugment
    }

    init{
        stepHeight = 1.0f
        if (!world.isClient)
            initOwnerGoals()
    }

    override fun initGoals() {
        goalSelector.add(0, FleeCreeperGoal(this))
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(4, WanderNearTargetGoal(this, 0.9, 16.0f))
        goalSelector.add(4, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(5, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))

        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity -> shouldAngerAt(entity) || AiConfig.entities.shouldItHit(owner,entity,AiConfig.Entities.Options.NON_FRIENDLY_NON_GOLEM,getSpell()) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity -> AiConfig.entities.shouldItHit(owner,entity,AiConfig.Entities.Options.NON_FRIENDLY_NON_GOLEM,getSpell()) && ((entity as? CreeperEntity)?.isIgnited != true) })
        targetSelector.add(4, UniversalAngerGoal(this, true))
    }

    private fun initOwnerGoals(){
        goalSelector.add(2, followSummonerGoal)
        goalSelector.add(3,callForConstructHelpGoal)

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
        if (maxAge > 0){
            if (age >= maxAge){
                kill()
            }
        }
        if (this.target?.isAlive == false)
            this.target = null
        val constructOwner = owner
        if (constructOwner != null){
            val attacker = constructOwner.recentDamageSource?.attacker
            if (attacker != null && attacker is LivingEntity){
                this.target = attacker
                setAngryAt(attacker.uuid)
                chooseRandomAngerTime()
            }
            if (this.age % 5 == 0 && constructOwner is PlayerEntity) {
                val trinkets = TrinketUtil.getTrinketStacks(constructOwner)
                for (stack in trinkets) {
                    if (EnchantmentHelper.getLevel(RegisterEnchantment.BEAST_MAGNET, stack) > 0){
                        val box = this.boundingBox.expand(1.0)
                        for (entity in world.getOtherEntities(this,box)){
                            if (entity is ItemEntity){
                                entity.onPlayerCollision(constructOwner)
                            } else if (entity is ExperienceOrbEntity){
                                entity.onPlayerCollision(constructOwner)
                            }
                        }
                        break
                    }
                }
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

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        val attacker = source.attacker
        if (attacker is LivingEntity){
            val spell = if (source is SpellDamageSource) source.getSpell() else null
            if(!AiConfig.entities.shouldItHitBase(attacker, this,AiConfig.Entities.Options.NONE, spell)) return false
        }
        if (source.isIn(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) {
            this.callForConstructHelpGoal.onHurt()
        }
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
                startTrackingOwner(owner!!)
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
        val owner = owner
        return AiConfig.entities.shouldItHitBase(owner,target,getSpell())
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
        if (summoner != null){
            if (!AiConfig.entities.shouldItHitBase(summoner,target,getSpell())) return false
        } else {
            if (!AiConfig.entities.shouldItHitBase(this,target,getSpell())) return false
        }
        var f = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
        var g = this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).toFloat()
        if (target is LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.mainHandStack, target.group)
            g += EnchantmentHelper.getKnockback(this).toFloat()
        }
        val fireAspect: Int = EnchantmentHelper.getFireAspect(this)
        if (fireAspect > 0) {
            target.setOnFireFor(fireAspect * 4)
        }
        val spell = getSpell()
        val source = if (spell == null) FzzyDamage.mobAttack(this) else SpellDamageSource(FzzyDamage.mobAttack(this), spell)
        val bl = target.damage(source, f)
        if (bl) {
            if (g > 0.0f && target is LivingEntity) {
                target.takeKnockback(
                    (g * 0.5f).toDouble(), MathHelper.sin(yaw * (Math.PI.toFloat() / 180)).toDouble(), -MathHelper.cos(
                        yaw * (Math.PI.toFloat() / 180)
                    ).toDouble()
                )
                velocity = velocity.multiply(0.6, 1.0, 0.6)
            }
            if (target is PlayerEntity) {
                disablePlayerShield(
                    target,
                    this.mainHandStack,
                    if (target.isUsingItem) target.activeItem else ItemStack.EMPTY
                )
            }
            applyDamageEffects(this, target)
            onAttacking(target)
            if (summoner != null && summoner is PlayerEntity && target is LivingEntity){
                (target as PlayerHitTimerAccessor).setPlayerHitTimer(100)
            }
            val h = world.getLocalDifficulty(blockPos).localDifficulty
            if (this.mainHandStack.isEmpty && this.isOnFire && random.nextFloat() < h * 0.3f) {
                target.setOnFireFor(2 * h.toInt())
            }
        }
        return bl
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

    override fun onKilledOther(world: ServerWorld?, other: LivingEntity?): Boolean {
        val ownerChk = owner
        if (ownerChk is PlayerEntity)
            ownerChk.onKilledOther(world, other)
        return super.onKilledOther(world, other)
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

    //need to also do the bounding boxxxxxxx
    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
    }

}
