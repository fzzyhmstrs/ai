package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import net.minecraft.entity.Entity
import net.minecraft.entity.Tameable
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.util.math.Box

object BodySwapEffect {

    fun callHostiles(entity: Entity, owner: Entity?, processContext: ProcessContext){
        val world = entity.world
        if (world.time % 22 != 0L || processContext.isBeforeRemoval()) return
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(24.0,3.0,24.0),pos.subtract(24.0,3.0,24.0))
        val entities = world.getOtherEntities(entity, box) {it is HostileEntity}
        for (target in entities){
            target.teleport(pos.x,pos.y,pos.z)
        }
    }

    fun callSummons(entity: Entity, owner: Entity?, processContext: ProcessContext){
        val world = entity.world
        if (world.time % 22 != 0L || processContext.isBeforeRemoval()) return
        val pos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val box = Box(pos.add(24.0,3.0,24.0),pos.subtract(24.0,3.0,24.0))
        val entities = world.getOtherEntities(entity, box) {it is Tameable}
        for (target in entities){
            target.teleport(pos.x,pos.y,pos.z)
        }
    }

}