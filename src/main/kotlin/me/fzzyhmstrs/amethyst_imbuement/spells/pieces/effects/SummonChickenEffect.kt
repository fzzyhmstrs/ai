package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object SummonChickenEffect {

    fun gust(entity: Entity, owner: Entity?, processContext: ProcessContext){
        if (entity.world.isClient) return
        if (entity.world.time % 20L != 0L) return
        val entityList = RaycasterUtil.raycastEntityArea(6.0, entity)
        for (target in entityList){
            if (target is LivingEntity)
                target.takeKnockback(1.0,entity.x - target.x, entity.z - target.z)
        }
    }
}
