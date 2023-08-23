package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlayerFangsEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.min

class FangsAugment: MiscAugment(ScepterTier.TWO,6) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withRange(11.0,1.0,0.0)
            .withDamage(5.8F,0.2F)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,34,12,
            9,imbueLevel,1, LoreTier.LOW_TIER, Items.EMERALD)
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
        for (i in 0..effect.range(level).toInt()) {
            val g = 1.25 * (i + 1).toDouble()
            val success = PlayerFangsEntity.conjureFangs(
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
        return SoundEvents.ENTITY_EVOKER_FANGS_ATTACK
    }
}
