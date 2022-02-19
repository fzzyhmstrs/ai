package me.fzzyhmstrs.amethyst_imbuement.effects

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class ReachStatusEffect(statusEffectCategory:StatusEffectCategory, i: Int):
    StatusEffect(statusEffectCategory,i) {
    /*override fun onRemoved(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        val client = MinecraftClient.getInstance()
        client.interactionManager.reachDistance
        super.onRemoved(entity, attributes, amplifier)
    }

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        entity.absorptionAmount = entity.absorptionAmount + 0.5f
        super.onApplied(entity, attributes, amplifier)
    }*/
}