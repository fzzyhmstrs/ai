package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TemptGoal
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.recipe.Ingredient

class CustomTemptGoal(entity: PathAwareEntity):
    TemptGoal(entity, 1.5, Ingredient.EMPTY, false)
{

    private val predicate = TargetPredicate.createNonAttackable().ignoreVisibility().setBaseMaxDistance(10.0)
    override fun canStart(): Boolean {
        closestPlayer = mob.world.getClosestPlayer(predicate, mob)
        return closestPlayer != null
    }
}