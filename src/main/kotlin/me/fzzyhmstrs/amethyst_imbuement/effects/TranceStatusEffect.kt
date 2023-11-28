package me.fzzyhmstrs.amethyst_imbuement.effects

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects

class TranceStatusEffect(statusEffectCategory: StatusEffectCategory, i: Int): StatusEffect(statusEffectCategory, i) {

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        super.onApplied(entity, attributes, amplifier)
        entity.addStatusEffect(StatusEffectInstance(RegisterStatus.ARCANE_AURA,200,2))
        entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,200,2))
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return false
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
    }
}