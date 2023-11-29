package me.fzzyhmstrs.amethyst_imbuement.spells.special

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class BarrierAugment: MinorSupportAugment(ScepterTier.TWO,10){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(0,1)
                                                .withDuration(540,80)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,600,50,
            10,imbueLevel,15, LoreTier.NO_TIER, Items.SHIELD)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is LivingEntity && AiConfig.entities.shouldItHitFriend(user,target,this)) {
                EffectQueue.addStatusToQueue(target, StatusEffects.ABSORPTION, effects.duration(level), effects.amplifier(level)/5)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                return true
            }
        }
        EffectQueue.addStatusToQueue(user, StatusEffects.ABSORPTION, effects.duration(level), effects.amplifier(level)/5)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_ARMOR_EQUIP_CHAIN
    }
}
