package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity

object ShineEffect {

    fun shine(entity: Entity, owner: Entity?, processContext: ProcessContext){
        if (entity is MobEntity) {
            val target = entity.target
            if (entity.world.random.nextFloat() < 0.15){
                target?.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING))
            }
        }
    }

}