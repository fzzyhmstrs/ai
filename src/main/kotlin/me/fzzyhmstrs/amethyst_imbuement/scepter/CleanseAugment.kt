package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.HealerAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class CleanseAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MinorSupportAugment(tier,maxLvl, *slot),
    HealerAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(190,10)

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is PlayerEntity) {
                val statuses: MutableList<StatusEffectInstance> = mutableListOf()
                for (effect in (target as LivingEntity).statusEffects){
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
        return if (user.isPlayer) {
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
                true
            } else {
                false
            }

    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,600,12,1,imbueLevel, LoreTier.NO_TIER, Items.MILK_BUCKET)
    }
}