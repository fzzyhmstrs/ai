package me.fzzyhmstrs.amethyst_imbuement.entity.golem

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

open class LavaGolemEntity: CrystallineGolemEntity {

    constructor(entityType: EntityType<LavaGolemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<LavaGolemEntity>, world: World, ageLimit: Int, createdBy: LivingEntity?) : super(entityType, world, ageLimit, createdBy)

    companion object {
        fun createGolemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.lavaGolem.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.lavaGolem.baseDamage.get().toDouble())
        }
    }

    override fun attackEffects(target: Entity, damageSource: DamageSource, damage: Float) {
        target.setOnFireFor(10)
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult? {
        val itemStack = player.getStackInHand(hand)
        if (!itemStack.isOf(Items.MAGMA_BLOCK)) {
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

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f)
    }
}
