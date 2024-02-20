package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.SparkboltEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class SparkboltAugment: SummonProjectileAugment(ScepterTier.ONE,15){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(4.25F,0.15f)
                                                .withAmplifier(45,2)
                                                .withDuration(40)
                                                .withRange(2.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, 24,6,
            5, imbueLevel,1, LoreTier.LOW_TIER, Items.CUT_COPPER)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val sbe = SparkboltEntity(world,user,level)
        sbe.passEffects(effects, level)
        sbe.setAugment(this)
        sbe.setVelocity(user,user.pitch,user.yaw,0.0f,
            0.9f,
            0.5f)
        return sbe
    }

    override fun entityTask(
        world: World,
        target: Entity,
        user: LivingEntity,
        level: Double,
        hit: HitResult?,
        effects: AugmentEffect
    ) {
        if (world.random.nextDouble() < effects.amplifier(level.toInt())/2500.0 && target is LivingEntity)
            RegisterStatus.stun(target,effects.duration(0))
        target.damage(SpellDamageSource(FzzyDamage.lightning(user),this),effects.damage(0)/2.5f)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_SHOOT
    }

}
