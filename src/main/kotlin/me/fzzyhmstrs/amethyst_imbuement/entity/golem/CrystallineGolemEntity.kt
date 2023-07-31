package me.fzzyhmstrs.amethyst_imbuement.entity.golem

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.config_util.ReadMeText
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedInt
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
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
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.SpawnHelper
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.event.GameEvent
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.math.max

open class CrystallineGolemEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<out CrystallineGolemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<out CrystallineGolemEntity>, world: World, ageLimit: Int, createdBy: LivingEntity?) : super(entityType, world, ageLimit, createdBy)

    class CrystalGolem: ConfigSection(Header.Builder().space().add("readme.entities.golem_1").build()){
        @ReadMeText("readme.entities.crystalGolem.spellBaseLifespan")
        var spellBaseLifespan = ValidatedInt(5500, Int.MAX_VALUE-120000,20)
        @ReadMeText("readme.entities.crystalGolem.spellPerLvlLifespan")
        var spellPerLvlLifespan = ValidatedInt(500,5000,0)
        @ReadMeText("readme.entities.crystalGolem.guardianLifespan")
        var guardianLifespan = ValidatedInt(900, Int.MAX_VALUE,20)
        var baseHealth = ValidatedDouble(180.0,1024.0,1.0)
        var baseMoveSpeed = ValidatedDouble(0.4,1.0,0.01)
        var baseKnockbackResist = ValidatedDouble(1.0,1.0,0.0)
        var baseDamage = ValidatedFloat(20.0f,1000f,0f)
    }

    companion object {
        fun createGolemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.crystalGolem.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.crystalGolem.baseMoveSpeed.get())
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, AiConfig.entities.crystalGolem.baseKnockbackResist.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.crystalGolem.baseDamage.get().toDouble())
        }
    }

    private val crackIdentifierMap: Map<Crack, Identifier> = ImmutableMap.of(
        CrystallineGolemEntity.Crack.LOW,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_low.png"),
        CrystallineGolemEntity.Crack.MEDIUM,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_medium.png"),
        CrystallineGolemEntity.Crack.HIGH,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_high.png")
    )

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
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

    override fun getBaseDamage(): Float {
        val f = super.getBaseDamage()
        val g = if (f.toInt() > 0) f / 2.0f + random.nextInt(f.toInt()).toFloat() else f
        return g
    }

    override fun applyKnockback(g: Float, target: Entity){
        target.velocity = target.velocity.add(0.0, 0.5, 0.0)
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return super.tryAttack(target)
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

    override fun damage(source: DamageSource, amount: Float): Boolean {
        val crack = getCrack()
        val bl = super.damage(source, amount)
        if (bl && getCrack() != crack) {
            playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0f, 1.0f)
        }
        return bl
    }

    open fun getCrackTextureMap(): Map<Crack,Identifier>{
        return crackIdentifierMap
    }

    fun getCrackTexture(): Identifier?{
        return getCrackTextureMap()[getCrack()]
    }

    fun getCrack(): Crack {
        val healthFraction = this.health / this.maxHealth
        return Crack.getCrackFromIndex(max(3,((healthFraction - 0.00001f)/0.25f).toInt()))
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

    fun getLookingAtVillagerTicks(): Int {
        return lookingAtVillagerTicksLeft
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    enum class Crack(maxHealthFraction: Float, val index: Int) {
        NONE(1.0f,0),
        LOW(0.75f,1),
        MEDIUM(0.5f,2),
        HIGH(0.25f,3);

        companion object {
            private val CRACKS = Stream.of(*values()).collect(Collectors.toMap({crack: Crack -> crack.index}, {crack -> crack}))

            fun getCrackFromIndex(index: Int): Crack{
                return CRACKS[index]?:NONE
            }
        }
    }
}
