package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class MentalClarityAugment: MinorSupportAugment(ScepterTier.TWO,16){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(840,60).withAmplifier(-1,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(1920,-20),300,
            13,imbueLevel,45,LoreTier.LOW_TIER, RegisterItem.KNOWLEDGE_POWDER)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is PlayerEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) {
                EffectQueue.addStatusToQueue(
                    target,
                    RegisterStatus.INSIGHTFUL,
                    effects.duration(level),
                    (effects.amplifier(level)/3) + 1)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                return true
            }
        }

        EffectQueue.addStatusToQueue(
            user,
            RegisterStatus.INSIGHTFUL,
            effects.duration(level),
            (effects.amplifier(level)/3) + 1)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }
}
