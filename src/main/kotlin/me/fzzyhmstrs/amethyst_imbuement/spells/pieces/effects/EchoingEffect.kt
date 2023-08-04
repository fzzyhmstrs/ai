package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance

object EchoingEffect {

    //owner is the target in this case
    fun resonate(entity: Entity, owner: Entity?, processContext: ProcessContext){
        if (owner is LivingEntity){
            val resonance = owner.getStatusEffect(RegisterStatus.RESONATING)
            if (resonance == null) {
                owner.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING, 80, 0))
            } else {
                val resonanceLevel = resonance.amplifier + owner.world.random.nextInt(2)
                owner.removeStatusEffect(RegisterStatus.RESONATING)
                owner.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING, 80, resonanceLevel))
            }
        }
    }

}