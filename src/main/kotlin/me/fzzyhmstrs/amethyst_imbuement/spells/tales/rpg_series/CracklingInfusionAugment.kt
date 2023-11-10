package me.fzzyhmstrs.amethyst_imbuement.spells.tales.rpg_series

import me.fzzyhmstrs.amethyst_core.compat.spell_power.SpChecker
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class CracklingInfusionAugment: MiscAugment(ScepterTier.THREE,12){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withAmplifier(-1,1)
                                                .withDuration(4000)
                                                .withRange(10.0,0.5,0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,12000,500,
            22,imbueLevel,60,BookOfTalesItem.TALES_TIER, Items.GOLDEN_APPLE)
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0

        val mod = SpChecker.getModFromTags(user, RegisterTag.LIGHTNING_AUGMENTS, RegisterTag.HEALER_AUGMENTS)
        val duration = effect.duration(level) * (1 + mod.toInt() / 100)
        val amplifier = (effect.amplifier(level) / 4) * (1 + mod.toInt() / 60)

        if (entityList.isEmpty()){
            successes++
            user.addStatusEffect(StatusEffectInstance(RegisterStatus.LIGHTNING_AURA,duration, amplifier))
            user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,duration, 0))
            user.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION,duration, 0))
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        } else {
            entityList.add(user)
            for (entity3 in entityList) {
                if (entity3 !is Monster && entity3 !is PassiveEntity && entity3 is LivingEntity) {
                    if (  !AiConfig.entities.isEntityPvpTeammate(user,entity3,this)) continue
                    successes++
                    entity3.addStatusEffect(StatusEffectInstance(RegisterStatus.LIGHTNING_AURA,duration, amplifier))
                    entity3.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,duration, 0))
                    entity3.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION,duration, 0))
                    effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
                }
            }
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }
}
