package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import net.minecraft.entity.ai.goal.TrackTargetGoal

class SardonyxElementalPriorityTargetGoal(private val sardonyxElemental: SardonyxElementalEntity) :
    TrackTargetGoal(sardonyxElemental, true, true) {



    override fun canStart(): Boolean {
        return sardonyxElemental.lastDamageTracker.hasPriority()
    }

    override fun start() {
        sardonyxElemental.target = sardonyxElemental.lastDamageTracker.getPriority(sardonyxElemental.world)
        super.start()
    }

}