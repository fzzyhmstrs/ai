package me.fzzyhmstrs.amethyst_imbuement.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class ShieldingStatusEffect(statusEffectCategory:StatusEffectCategory, i: Int):
    StatusEffect(statusEffectCategory,i) {
    override fun onRemoved(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        entity.absorptionAmount = entity.absorptionAmount - (1 * (amplifier + 1)).toFloat()
        super.onRemoved(entity, attributes, amplifier)
    }

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        entity.absorptionAmount = entity.absorptionAmount + (1 * (amplifier + 1)).toFloat()
        super.onApplied(entity, attributes, amplifier)
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return false
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
    }
}