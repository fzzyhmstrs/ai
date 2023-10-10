package me.fzzyhmstrs.amethyst_imbuement.spells.special

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.IceSpikeEntity.Companion.conjureIceSpikes
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.min

class IceSpikesAugment: MiscAugment(ScepterTier.TWO,5){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(11,1,0)
                                                .withDamage(5.25F,0.25F)
                                                .withDuration(225,25)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,32,14,
            8, imbueLevel,2, LoreTier.NO_TIER, Items.BLUE_ICE)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        var d: Double
        var e: Double
        if (target != null){
            d = min(target.y, user.y)
            e = d + 2.0
        } else {
            d = user.y
            e = d + 2.0
        }
        val f = (user.yaw + 90) * MathHelper.PI / 180
        for (i in 0..effect.amplifier(level)) {
            val g = 1.25 * (i + 1).toDouble()
            val success = conjureIceSpikes(
                world,
                user,
                user.x + MathHelper.cos(f).toDouble() * g,
                user.z + MathHelper.sin(f).toDouble() * g,
                d,
                e,
                f,
                i,
                effect,
                level,
                this
            )
            if (success != Double.NEGATIVE_INFINITY) {
                successes++
                d = success
                e = d + 2.0
            }

        }
        val bl = successes > 0
        if (bl){
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return RegisterSound.ICE_SPIKES
    }
}
