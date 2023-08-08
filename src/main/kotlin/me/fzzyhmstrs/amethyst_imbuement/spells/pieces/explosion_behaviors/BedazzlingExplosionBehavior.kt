package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance

class BedazzlingExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun affectEntity(entity: Entity) {
        if (entity is LivingEntity && entity.world.random.nextFloat() < 0.25){
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,80))
        }
    }
}