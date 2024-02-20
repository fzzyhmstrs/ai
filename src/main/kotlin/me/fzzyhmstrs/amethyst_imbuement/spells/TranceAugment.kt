package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class TranceAugment: MinorSupportAugment(ScepterTier.TWO,13){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(21,3)
                                                .withDuration(400,0)
        

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(2440,-40),350,
            19,imbueLevel,30,LoreTier.LOW_TIER, RegisterItem.MANA_REGENERATION_POTION)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        EffectQueue.addStatusToQueue(user, RegisterStatus.TRANCE, effects.duration(level), effects.amplifier(level))
        EffectQueue.addStatusToQueue(user, StatusEffects.BLINDNESS, effects.duration(level), effects.amplifier(level))
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }
}
