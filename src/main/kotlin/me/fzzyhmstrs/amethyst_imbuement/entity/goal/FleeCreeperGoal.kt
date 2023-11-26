package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import net.minecraft.entity.ai.NoPenaltyTargeting
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.pathing.Path
import net.minecraft.entity.mob.CreeperEntity

class FleeCreeperGoal(private val construct: PlayerCreatedConstructEntity): Goal(){

    private var fleePath: Path? = null
    
    override fun canStart(): Boolean {
        val target = construct.target
        /*if (target != null) {
            println(target)
            if (target is CreeperEntity)
                println(target.fuseSpeed)
            println(target.squaredDistanceTo(construct))
        }*/
        if (target is CreeperEntity && target.fuseSpeed > 0 && target.squaredDistanceTo(construct) < 49.0){
            val vec3d = NoPenaltyTargeting.findFrom(construct, 16, 7, target.pos) ?: return false
            if (target.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < target.squaredDistanceTo(construct)) {
                return false
            }
            fleePath = construct.navigation.findPathTo(vec3d.x,vec3d.y,vec3d.z,0)
            return fleePath != null
        }
        return false
    }

    override fun shouldRunEveryTick(): Boolean {
        return true
    }

    override fun start() {
        this.construct.navigation.startMovingAlong(fleePath,1.25)
    }
    
}