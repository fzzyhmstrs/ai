package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.Display
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks

class CrystalAltarDisplay(val recipe: AltarRecipe): Display {

    override fun getInputEntries(): MutableList<EntryIngredient> {
        val list: MutableList<EntryIngredient> = mutableListOf()
        val builder = EntryIngredient.builder()
        recipe.base.matchingStacks.forEach { builder.add(EntryStacks.of(it)) }
        list.add(builder.build())
        val builder2 = EntryIngredient.builder()
        recipe.addition.matchingStacks.forEach { builder2.add(EntryStacks.of(it)) }
        list.add(builder2.build())
        return list
    }

    override fun getOutputEntries(): MutableList<EntryIngredient> {
        val list: MutableList<EntryIngredient> = mutableListOf()
        val builder = EntryIngredient.builder()
        builder.add(EntryStacks.of(recipe.output))
        list.add(builder.build())
        return list
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return CategoryIdentifier.of<ImbuingTableDisplay>(AltarRecipe.Type.ID)
    }
}