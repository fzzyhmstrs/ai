package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class RevivifyAugment: MinorSupportAugment(ScepterTier.THREE,7){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(2200,200).withAmplifier(7,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, 280, 175,
            30, imbueLevel,13, BookOfTalesItem.TALES_TIER, RegisterItem.GOLDEN_HEART)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target !is Monster && target is LivingEntity) {
                if (!(target is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,target,this))) {
                  target.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, (effects.duration(level) * 0.7).toInt(), effects.amplifier(1)))
                  target.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, (effects.duration(level + 3) * 0.7).toInt(), effects.amplifier(level - 1)))
                  effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                  val passedEffect = AugmentEffect()
                  passedEffect.plus(effects)
                  passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.BENEFICIAL)
                  passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.HARMFUL)
                  RegisterEnchantment.MASS_CLEANSE.effect(world, user, mutableListOf(target), level, passedEffect)
                  RegisterEnchantment.MASS_HEAL.effect(world, user, mutableListOf(target), level, passedEffect)
                  world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                  return true
                }
            }
        }
        user.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, effects.duration(level), effects.amplifier(1)))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, effects.duration(level + 3), effects.amplifier(level)))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        val passedEffect = AugmentEffect()
        passedEffect.plus(effects)
        passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.BENEFICIAL)
        passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.HARMFUL)
        RegisterEnchantment.MASS_CLEANSE.effect(world, user, mutableListOf(user), level, passedEffect)
        RegisterEnchantment.MASS_HEAL.effect(world, user, mutableListOf(user), level, passedEffect)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS
    }
}
