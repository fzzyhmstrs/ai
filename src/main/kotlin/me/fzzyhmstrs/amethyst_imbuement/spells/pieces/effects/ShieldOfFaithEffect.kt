package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance

object ShieldOfFaithEffect {
    //owner is the attacker in this case
    fun shield(entity: Entity, attackerOrOwner: Entity?, processContext: ProcessContext){
        if (attackerOrOwner !is LivingEntity) return
        if (attackerOrOwner.group != EntityGroup.UNDEAD) return
        if (entity is LivingEntity && entity.world.random.nextFloat() < 0.2){
            val amp = if (processContext.get(ContextData.CRIT)) 5 else 2
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.BLESSED,200,amp))
        }
    }

}