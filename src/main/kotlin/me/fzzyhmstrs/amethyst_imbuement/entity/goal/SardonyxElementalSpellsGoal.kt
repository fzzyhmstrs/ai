package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import net.minecraft.entity.ai.goal.Goal

class SardonyxElementalSpellsGoal(private val sardonyxElementalEntity: SardonyxElementalEntity): Goal() {
    override fun canStart(): Boolean {
        return sardonyxElementalEntity.spellCooldown <= 0
    }

    override fun shouldRunEveryTick(): Boolean {
        return true
    }
}