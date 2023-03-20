package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class CleanseAugment: MinorSupportAugment(ScepterTier.ONE,11){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(190,10)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(670,-20),45,
            1, imbueLevel,15, LoreTier.NO_TIER, Items.MILK_BUCKET)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if ((target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) && target is LivingEntity) {
                val statuses: MutableList<StatusEffectInstance> = mutableListOf()
                for (effect in target.statusEffects){
                    if (effect.effectType.isBeneficial) continue
                    statuses.add(effect)
                }
                for (effect in statuses) {
                    target.removeStatusEffect(effect.effectType)
                }
                target.fireTicks = 0
                EffectQueue.addStatusToQueue(target,RegisterStatus.IMMUNITY,effects.duration(level),effects.amplifier(level))
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                return true
            }
        }
        val statuses: MutableList<StatusEffectInstance> = mutableListOf()
        for (effect in user.statusEffects){
            if (effect.effectType.isBeneficial) continue
            statuses.add(effect)
        }
        for (effect in statuses) {
            user.removeStatusEffect(effect.effectType)
        }
        user.fireTicks = 0
        EffectQueue.addStatusToQueue(user,RegisterStatus.IMMUNITY,effects.duration(level),effects.amplifier(level))
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON
    }
}
