package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.IceSpikeEntity
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.joml.Vector2d
import kotlin.math.min

class IceBurstAugment: MiscAugment(ScepterTier.THREE,9){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(12.75F,0.25F)
            .withDuration(175,25)
            .withRange(7.0,1.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(
            SpellType.FURY, 80, 40,
            22, imbueLevel, 2, BookOfTalesItem.TALES_TIER, Items.BLUE_ICE
        )
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
        val horPos = Vector2d(user.x,user.z)
        val f = (user.yaw + 90) * MathHelper.PI / 180
        val range = effect.range(level)
        var i = user.x - range
        while (i < user.x + range){
            if (target != null){
                d = min(target.y, user.y)
                e = d + 2.0
            } else {
                d = user.y
                e = d + 2.0
            }
            var j = user.z - range
            while(j < user.z + range){
                if (horPos.distance(i,j) > range || horPos.distance(i,j) < 0.8) {
                    j += 0.75
                    continue
                }
                val success = IceSpikeEntity.conjureIceSpikes(
                    world,
                    user,
                    i,
                    j,
                    d,
                    e,
                    f,
                    (horPos.distance(i,j).toInt() * 2) + 1,
                    effect,
                    level,
                    this
                )
                if (success != Double.NEGATIVE_INFINITY) {
                    successes++
                    d = success
                    e = d + 2.0
                }
                j += 0.75
            }
            i += 0.75
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