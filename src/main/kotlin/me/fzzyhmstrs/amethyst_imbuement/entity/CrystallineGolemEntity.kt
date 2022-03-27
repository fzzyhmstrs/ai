package me.fzzyhmstrs.amethyst_imbuement.entity

import com.google.common.collect.ImmutableList
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TimeHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.SpawnHelper
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.event.GameEvent
import java.util.*
import java.util.stream.Stream
import kotlin.experimental.and
import kotlin.experimental.or

@Suppress("PrivatePropertyName")
class CrystallineGolemEntity(entityType: EntityType<CrystallineGolemEntity>, world: World): GolemEntity(entityType,world), Angerable {

    constructor(entityType: EntityType<CrystallineGolemEntity>, world: World, ageLimit: Int) : this(entityType, world){
        maxAge = ageLimit
    }

    companion object {
        private val CRYSTAL_GOLEM_FLAGS =
            DataTracker.registerData(CrystallineGolemEntity::class.java, TrackedDataHandlerRegistry.BYTE)

        fun createGolemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 180.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0)
        }
    }
    var attackTicksLeft = 0
    private var lookingAtVillagerTicksLeft = 0
    private val ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39)
    private var angerTime = 0
    private var maxAge = -1
    private var angryAt: UUID? = null

    init{
        stepHeight = 1.0f
    }

    override fun initGoals() {
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(2, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(4, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(5, CrystallineGolemLookGoal(this))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(1, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(2, ActiveTargetGoal(
            this,
            MobEntity::class.java, 5, false, false
        ) { entity: LivingEntity? -> entity is Monster})
        targetSelector.add(3, UniversalAngerGoal(this, false))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        if (maxAge == -1) {
            dataTracker.startTracking(CRYSTAL_GOLEM_FLAGS, 0.toByte())
        } else {
            dataTracker.startTracking(CRYSTAL_GOLEM_FLAGS, 1.toByte())
        }
    }

    override fun getNextAirUnderwater(air: Int): Int {
        return air
    }

    override fun pushAway(entity: Entity) {
        if (entity is Monster && getRandom().nextInt(20) == 0) {
            target = entity as LivingEntity
        }
        super.pushAway(entity)
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
        val blockState: BlockState =
            world.getBlockState(BlockPos(MathHelper.floor(this.x),MathHelper.floor(this.y),MathHelper.floor(this.z)))
        if (velocity.horizontalLengthSquared() > 2.500000277905201E-7 && random.nextInt(5) == 0 && !blockState.isAir) {
            world.addParticle(
                BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                this.x + (random.nextFloat().toDouble() - 0.5) * this.width.toDouble(),
                this.y + 0.1,
                this.z + (random.nextFloat().toDouble() - 0.5) * this.width.toDouble(),
                4.0 * (random.nextFloat().toDouble() - 0.5),
                0.5,
                (random.nextFloat().toDouble() - 0.5) * 4.0
            )
        }
        if (!world.isClient) {
            tickAngerLogic(world as ServerWorld, true)
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("PlayerCreated", this.isPlayerCreated())
        writeAngerToNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        this.setPlayerCreated(nbt.getBoolean("PlayerCreated"))
        readAngerFromNbt(world, nbt)
    }

    private fun isPlayerCreated(): Boolean {
        return (dataTracker.get(CRYSTAL_GOLEM_FLAGS) and 1) != 0.toByte()
    }

    fun setPlayerCreated(playerCreated: Boolean) {
        val b = dataTracker.get(CRYSTAL_GOLEM_FLAGS)
        if (playerCreated) {
            dataTracker.set(CRYSTAL_GOLEM_FLAGS, (b or 1))
        } else {
            dataTracker.set(CRYSTAL_GOLEM_FLAGS, (b and -0x2))
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

    private fun getAttackDamage(): Float {
        return getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        val f = getAttackDamage()
        val g = if (f.toInt() > 0) f / 2.0f + random.nextInt(f.toInt()).toFloat() else f
        val bl = target.damage(DamageSource.mob(this), g)
        if (bl) {
            target.velocity = target.velocity.add(0.0, 0.5, 0.0)
            applyDamageEffects(this, target)
        }
        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return bl
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
            playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        } else if (status.toInt() == 11) {
            lookingAtVillagerTicksLeft = 400
        } else if (status.toInt() == 34) {
            lookingAtVillagerTicksLeft = 0
        } else {
            super.handleStatus(status)
        }
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

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult? {
        val itemStack = player.getStackInHand(hand)
        if (!itemStack.isOf(Items.AMETHYST_SHARD)) {
            return ActionResult.PASS
        }
        val f = this.health
        heal(20.0f)
        if (this.health == f) {
            return ActionResult.PASS
        }
        val g = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f
        playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g)
        this.emitGameEvent(GameEvent.MOB_INTERACT, this.cameraBlockPos)
        if (!player.abilities.creativeMode) {
            itemStack.decrement(1)
        }
        return ActionResult.success(world.isClient)
    }

    override fun damage(source: DamageSource?, amount: Float): Boolean {
        val crack = getCrack()
        val bl = super.damage(source, amount)
        if (bl && getCrack() != crack) {
            playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0f, 1.0f)
        }
        return bl
    }

    fun getCrack(): Crack {
        return Crack.from(this.health / this.maxHealth)
    }

    override fun canSpawn(world: WorldView): Boolean {
        val blockPos = blockPos
        val blockPos2 = blockPos.down()
        val blockState = world.getBlockState(blockPos2)
        if (blockState.hasSolidTopSurface(world, blockPos2, this)) {
            for (i in 1..2) {
                var blockState2: BlockState
                val blockPos3 = blockPos.up(i)
                if (SpawnHelper.isClearForSpawn(
                        world,
                        blockPos3,
                        world.getBlockState(blockPos3).also { blockState2 = it },
                        blockState2.fluidState,
                        RegisterEntity.CRYSTAL_GOLEM_ENTITY
                    )
                ) continue
                return false
            }
            return SpawnHelper.isClearForSpawn(
                world,
                blockPos,
                world.getBlockState(blockPos),
                Fluids.EMPTY.defaultState,
                RegisterEntity.CRYSTAL_GOLEM_ENTITY
            ) && world.doesNotIntersectEntities(this)
        }
        return false
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState?) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f)
    }

    fun getLookingAtVillagerTicks(): Int {
        return lookingAtVillagerTicksLeft
    }

    override fun onDeath(source: DamageSource) {
        super.onDeath(source)
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    enum class Crack(private val maxHealthFraction: Float) {
        NONE(1.0f), LOW(0.75f), MEDIUM(0.5f), HIGH(0.25f);

        companion object {
            private var VALUES: List<Crack> = Stream.of(*values()).sorted(
                Comparator.comparingDouble { crack: Crack -> crack.maxHealthFraction.toDouble() }
            ).collect(ImmutableList.toImmutableList())

            fun from(healthFraction: Float): Crack {
                for (crack in VALUES) {
                    if (healthFraction >= crack.maxHealthFraction) continue
                    return crack
                }
                return NONE
            }

        }
    }
}