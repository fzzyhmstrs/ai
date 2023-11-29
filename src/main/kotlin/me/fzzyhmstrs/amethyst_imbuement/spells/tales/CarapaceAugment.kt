package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class CarapaceAugment: MinorSupportAugment(ScepterTier.THREE,7){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(7,1)
                                                .withDuration(2200,200)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, 6000, 450,
            30,imbueLevel,75, BookOfTalesItem.TALES_TIER, Items.SHULKER_SHELL)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if ((target is LivingEntity && AiConfig.entities.shouldItHitFriend(user,target,this))) {
            target.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR, effects.duration(level), effects.amplifier(level)/2))
            effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
            world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
            return true
        }
        user.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR, effects.duration(level), effects.amplifier(level)/2))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }
}
