package me.fzzyhmstrs.amethyst_imbuement.scepter.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance

class StunningExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {

    override fun affectEntity(entity: Entity) {
        if (AI.aiRandom().nextFloat() < 0.2f && entity is LivingEntity){
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,80))
        }
    }

}