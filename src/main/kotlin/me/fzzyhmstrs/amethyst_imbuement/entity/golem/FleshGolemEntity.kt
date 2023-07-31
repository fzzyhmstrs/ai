package me.fzzyhmstrs.amethyst_imbuement.entity.golem

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.zombie.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

open class FleshGolemEntity: CrystallineGolemEntity {

    constructor(entityType: EntityType<FleshGolemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<FleshGolemEntity>, world: World, ageLimit: Int, createdBy: LivingEntity?) : super(entityType, world, ageLimit, createdBy)

    companion object {
        fun createGolemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.fleshGolem.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.fleshGolem.baseDamage.get().toDouble())
        }
    }

    private val crackIdentifierMap: Map<Crack, Identifier> = ImmutableMap.of(
        CrystallineGolemEntity.Crack.LOW,
        AI.identity("textures/entity/crystal_golem/flesh_golem_crackiness_low.png"),
        CrystallineGolemEntity.Crack.MEDIUM,
        AI.identity("textures/entity/crystal_golem/flesh_golem_crackiness_medium.png"),
        CrystallineGolemEntity.Crack.HIGH,
        AI.identity("textures/entity/crystal_golem/flesh_golem_crackiness_high.png")
    )

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        this.entityGroup = EntityGroup.UNDEAD
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    override fun getBaseDamage(): Float {
        return super.getBaseDamage() - (getCrack().index * 0.5f)
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult? {
        val itemStack = player.getStackInHand(hand)
        if (!itemStack.isOf(Items.ROTTEN_FLESH)) {
            return ActionResult.PASS
        }
        val f = this.health
        heal(4.0f)
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

    override fun remove(reason: RemovalReason) {
        if (reason.shouldDestroy()){
            val unhallowed = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world,AiConfig.entities.fleshGolem.zombieLifespan.get(), owner)
            unhallowed.refreshPositionAndAngles(pos.x,pos.y,pos.z,this.yaw,this.pitch)
            unhallowed.passContext(processContext.copy())
            world.spawnEntity(unhallowed)
            world.playSound(null,this.blockPos,SoundEvents.ENTITY_ZOMBIE_AMBIENT,SoundCategory.PLAYERS,1.0f,1.0f)
        }
        super.remove(reason)
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f)
    }

    override fun getCrackTextureMap(): Map<Crack, Identifier> {
        return crackIdentifierMap
    }
}
