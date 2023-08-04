package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance

class ResonanceExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun affectEntity(entity: Entity) {
        if (entity is LivingEntity){
            val resonance = entity.getStatusEffect(RegisterStatus.RESONATING)
            if (resonance == null) {
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING, 80, 0))
            } else {
                val resonanceLevel = resonance.amplifier + 1
                entity.removeStatusEffect(RegisterStatus.RESONATING)
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING, 80, resonanceLevel))
            }
        }
    }
}