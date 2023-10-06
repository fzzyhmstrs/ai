package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity
import net.minecraft.util.math.Box

class CallForConstructHelpGoal(private val construct: MobEntity): Goal(){
    
    private var hurt: Boolean = false
    
    override fun canStart(): Boolean {
        return hurt
    }

    fun onHurt() {
        hurt = true
    }

    override fun start() {
        hurt = false
        val target = construct.attacker?:construct.target?:return
        val box = Box.from(construct.pos).expand(16.0, 10.0, 16.0)
        val help = construct.world.getOtherEntities(construct,box) { entity -> entity is PlayerCreatedConstructEntity || entity is PlayerCreatable }
        for (construct in help){
            if (construct is MobEntity){
                construct.target = target
            }
        }
        super.start()
    }
    
}