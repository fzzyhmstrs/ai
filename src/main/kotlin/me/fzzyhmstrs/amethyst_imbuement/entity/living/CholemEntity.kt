package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.Items
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.SpawnHelper
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.event.GameEvent

open class CholemEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<CholemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<CholemEntity>, world: World, ageLimit: Int = AiConfig.entities.cholem.baseLifespan.get(), createdBy: LivingEntity?) : super(entityType, world, ageLimit, createdBy)

    companion object {

        protected val ENRAGED = DataTracker.registerData(CholemEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        fun createGolemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.cholem.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.cholem.baseDamage.get().toDouble())
        }
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(ENRAGED,false)
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

    override fun tickMovement() {
        super.tickMovement()
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
    }

    private fun getAttackDamage(): Float {
        return getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        val bl = super.tryAttack(target)
        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return bl
    }

    override fun applyKnockback(g: Float, target: Entity){
        target.velocity = target.velocity.add(0.0, 0.5, 0.0)
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        }
        super.handleStatus(status)
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
        this.emitGameEvent(GameEvent.ENTITY_INTERACT, this)
        if (!player.abilities.creativeMode) {
            itemStack.decrement(1)
        }
        return ActionResult.success(world.isClient)
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

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f)
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }

    fun getLookingAtVillagerTicks(): Int {
        return lookingAtVillagerTicksLeft
    }

    override fun remove(reason: RemovalReason?) {
        processContext.beforeRemoval()
        runEffect(ModifiableEffectEntity.ON_REMOVED,this,owner,processContext)
        super.remove(reason)
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }
}
