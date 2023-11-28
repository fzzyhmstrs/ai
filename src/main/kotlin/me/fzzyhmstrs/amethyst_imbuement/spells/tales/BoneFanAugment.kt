package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.BoneShardEntity
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class BoneFanAugment: SummonProjectileAugment(ScepterTier.THREE,17) {

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(9.875f,0.125f)
                                                .withAmplifier(2)
                                                .withDuration(80)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,40,36,
            21,imbueLevel,3,BookOfTalesItem.TALES_TIER,Items.BONE_BLOCK)
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        var successes = 0
        val speed = 2.0F
        val div = 0.75F
        val increment = 12.5f * 2 / effects.amplifier(level)
        for (i in -effects.amplifier(level) ..effects.amplifier(level)){
            val fbe = BoneShardEntity(
                world, user, speed, div, (user.yaw + 12.5f * i),
                user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin((user.yaw + increment * i) * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
                user.eyeY - 0.6 - 0.8 * MathHelper.sin(user.pitch * (Math.PI.toFloat() / 180)),
                user.z + (user.width + 0.5f) * 0.5 * MathHelper.cos((user.yaw + increment * i) * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
            )
            fbe.passEffects(effects, level)
            fbe.setAugment(this)
            val bl = world.spawnEntity(fbe)
            if(bl) {
                successes++
                world.playSound(
                    null,
                    user.blockPos,
                    soundEvent(),
                    SoundCategory.PLAYERS,
                    1.0f,
                    world.getRandom().nextFloat() * 0.4f + 0.8f
                )
            }
        }
        return successes > 0
    }


    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }
}
