package me.fzzyhmstrs.amethyst_imbuement.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class StridingStatusEffect(statusEffectCategory:StatusEffectCategory, i: Int):
    StatusEffect(statusEffectCategory,i) {
    /*override fun onRemoved(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        entity.stepHeight = 0.6f
        println(entity.stepHeight)
        super.onRemoved(entity, attributes, amplifier)
    }

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        entity.stepHeight = 1.1f
        println(entity.stepHeight)
        super.onApplied(entity, attributes, amplifier)
    }*/
}