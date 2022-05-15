package me.fzzyhmstrs.amethyst_imbuement.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class DraconicVisionStatusEffect(statusEffectCategory:StatusEffectCategory, i: Int):
    StatusEffect(statusEffectCategory,i) {

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return false
    }
}