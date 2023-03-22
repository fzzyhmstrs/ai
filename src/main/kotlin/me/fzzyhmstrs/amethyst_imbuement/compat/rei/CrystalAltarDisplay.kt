package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.util.Identifier
import java.util.*

class CrystalAltarDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>):
    BasicDisplay(inputs, outputs, location) {

    constructor(recipe: AltarRecipe): this(
        getRecipeInputEntries(recipe),
        getRecipeOutputEntries(recipe),
        Optional.ofNullable(recipe.id))



    override fun getInputEntries(): MutableList<EntryIngredient> {
        return inputs
    }

    override fun getOutputEntries(): MutableList<EntryIngredient> {
        return outputs
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return CategoryIdentifier.of<CrystalAltarDisplay>(AI.MOD_ID, AltarRecipe.Type.ID)
    }

    companion object{

        fun serializer(): Serializer<CrystalAltarDisplay>{
            return Serializer.ofSimple { inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier> ->
                CrystalAltarDisplay(
                    inputs,
                    outputs,
                    location
                )
            }
        }

        private fun getRecipeInputEntries(recipe: AltarRecipe): MutableList<EntryIngredient>{
            val list: MutableList<EntryIngredient> = mutableListOf()
            val builder0 = EntryIngredient.builder()
            recipe.dust.matchingStacks.forEach { builder0.add(EntryStacks.of(it)) }
            list.add(builder0.build())
            val builder1 = EntryIngredient.builder()
            recipe.base.matchingStacks.forEach { builder1.add(EntryStacks.of(it)) }
            list.add(builder1.build())
            val builder2 = EntryIngredient.builder()
            recipe.addition.matchingStacks.forEach { builder2.add(EntryStacks.of(it)) }
            list.add(builder2.build())
            return list
        }

        private fun getRecipeOutputEntries(recipe: AltarRecipe): MutableList<EntryIngredient>{
            val list: MutableList<EntryIngredient> = mutableListOf()
            val builder = EntryIngredient.builder()
            builder.add(EntryStacks.of(recipe.output))
            list.add(builder.build())
            return list
        }


    }
}
