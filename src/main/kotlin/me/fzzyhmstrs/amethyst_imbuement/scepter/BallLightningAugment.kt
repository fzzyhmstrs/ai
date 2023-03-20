package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.BallLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.FlameboltEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class BallLightningAugment: SummonProjectileAugment(ScepterTier.TWO,8){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(6.4F,0.2F,0.0F)
            .withDuration(19,-1)
            .withRange(3.0,.25)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,80,25,
            14, imbueLevel,3, LoreTier.LOW_TIER, Items.COPPER_BLOCK)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 2.0F
        val div = 0.75F
        return BallLightningEntity.createBallLightning(world, user, speed, div, effects, level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT
    }
}