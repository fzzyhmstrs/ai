package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.block.BlockState
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
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName")
class UnhallowedEntity(entityType: EntityType<UnhallowedEntity>, world: World): GolemEntity(entityType,world),
    Angerable, PlayerCreatable {

    constructor(entityType: EntityType<UnhallowedEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null, bonusEquips: Int = 0, modDamage: Double = 0.0, modHealth: Double = 0.0) : this(entityType, world){
        modifiedDamage = modDamage
        modifiedHealth = modHealth
        maxAge = ageLimit
        this.createdBy = createdBy?.uuid
        this.owner = createdBy
        if (createdBy != null) {
            goalSelector.add(2, FollowSummonerGoal(this,createdBy, 1.0, 10.0f, 2.0f, false))
            targetSelector.add(1, TrackSummonerAttackerGoal(this,createdBy))
        }
        bonusEquipment = bonusEquips
        if (world is ServerWorld) {
            initialize(world,world.getLocalDifficulty(this.blockPos),SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    companion object {
        private const val baseMaxHealth = 20.0
        private const val baseMoveSpeed = 0.4
        private const val baseAttackDamage = 3.0

        fun createUnhallowedAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage)
        }
    }
    private var attackTicksLeft = 0
    private var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    override var maxAge = -1
    private var bonusEquipment = 0
    override var createdBy: UUID? = null
    override var owner: LivingEntity? = null
    private var angryAt: UUID? = null
    private var modifiedDamage = 0.0
    private var modifiedHealth = 0.0

    init{
        stepHeight = 1.0f
    }

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(3, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(3, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(4, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(5, UnhallowedLookGoal(this))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        this.initEquipment(difficulty)
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

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putDouble("ModifiedHealth", modifiedHealth)
        nbt.putDouble("ModifiedDamage", modifiedDamage)
        writePlayerCreatedNbt(nbt)
        writeAngerToNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        modifiedHealth = nbt.getDouble("ModifiedHealth")
        modifiedDamage = nbt.getDouble("ModifiedDamage")
        readPlayerCreatedNbt(world, nbt)
        readAngerFromNbt(world, nbt)
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

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_DEATH
    }

    private fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(getStepSound(), 0.15f, 1.0f)
    }

    override fun getGroup(): EntityGroup? {
        return EntityGroup.UNDEAD
    }

    override fun initEquipment(difficulty: LocalDifficulty) {
        when (bonusEquipment) {
            1 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.LEATHER_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.LEATHER_CHESTPLATE))
            }
            2 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.CHAINMAIL_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.CHAINMAIL_CHESTPLATE))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.WOODEN_SWORD))
            }
            3 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.IRON_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.IRON_CHESTPLATE))
                this.equipStack(EquipmentSlot.FEET, ItemStack(Items.IRON_BOOTS))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.STONE_SWORD))
            }
            4 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(RegisterArmor.STEEL_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(RegisterArmor.STEEL_CHESTPLATE))
                this.equipStack(EquipmentSlot.LEGS, ItemStack(RegisterArmor.STEEL_LEGGINGS))
                this.equipStack(EquipmentSlot.FEET, ItemStack(RegisterArmor.STEEL_BOOTS))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(RegisterItem.GLOWING_BLADE))
            }
        }
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
        super.onDeath(source)
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

}