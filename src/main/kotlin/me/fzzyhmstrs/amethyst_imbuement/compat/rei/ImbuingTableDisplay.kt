package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_core.item.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.recipe.ImbuingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import java.util.*

class ImbuingTableDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>, tag: NbtCompound):
    BasicDisplay(inputs, outputs, location) {

    constructor(recipe: ImbuingRecipe): this(
        getRecipeInputEntries(recipe),
        getRecipeOutputEntries(recipe),
        Optional.ofNullable(recipe.id),
        createCostTag(recipe))

    private val cost = tag.getInt("display_cost")

    override fun getInputEntries(): MutableList<EntryIngredient> {
        return inputs
    }

    override fun getOutputEntries(): MutableList<EntryIngredient> {
        return outputs
    }

    fun getCost(): Int{
        return cost
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return imbuingCategoryId
    }

    companion object{

        private val imbuingCategoryId = CategoryIdentifier.of<ImbuingTableDisplay>(AI.MOD_ID, ImbuingRecipe.Type.ID)

        private fun createCostTag(recipe: ImbuingRecipe): NbtCompound{
            val nbt = NbtCompound()
            val cost = recipe.getCost()
            nbt.putInt("display_cost",cost)
            return nbt
        }

        fun serializer(): Serializer<ImbuingTableDisplay>{
            return Serializer.of { inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>, tag:NbtCompound ->
                ImbuingTableDisplay(
                    inputs,
                    outputs,
                    location,
                    tag
                )
            }
        }

        private fun getRecipeInputEntries(recipe: ImbuingRecipe): MutableList<EntryIngredient>{
            val list: MutableList<EntryIngredient> = mutableListOf()
            val inputs = recipe.getInputs()
            for (i in inputs.indices){
                val input = inputs[i]
                val builder = EntryIngredient.builder()
                if (recipe.getAugment() != "" && i == 6){
                    val ingredient = recipe.getCenterIngredient()
                    ingredient.matchingStacks.forEach { builder.add(EntryStacks.of(it)) }
                    list.add(builder.build())
                    continue
                }
                input.matchingStacks.forEach {
                    val item = it.item
                    val stack = it.copy()
                    if (item is AbstractAugmentBookItem){
                        AbstractAugmentBookItem.addLoreKeyForREI(stack,recipe.getAugment())
                    }
                    builder.add(EntryStacks.of(stack))
                }
                list.add(builder.build())
            }
            return list
        }

        private fun getRecipeOutputEntries(recipe: ImbuingRecipe): MutableList<EntryIngredient>{
            val list: MutableList<EntryIngredient> = mutableListOf()
            val builder = EntryIngredient.builder()
            builder.add(EntryStacks.of(recipe.getOutput()))
            list.add(builder.build())
            return list
        }
    }
}