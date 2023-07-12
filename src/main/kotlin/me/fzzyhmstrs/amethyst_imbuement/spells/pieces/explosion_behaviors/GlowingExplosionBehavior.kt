package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects

class GlowingExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun affectEntity(entity: Entity) {
        if (entity is LivingEntity && entity.isAlive){
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING,300))
        }
    }
}