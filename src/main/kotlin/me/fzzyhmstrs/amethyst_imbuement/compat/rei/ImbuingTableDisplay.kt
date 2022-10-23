package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfMythosItem
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class ImbuingTableDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>, tag: NbtCompound):
    BasicDisplay(inputs, outputs, location) {

    constructor(recipe: ImbuingRecipe): this(
        getRecipeInputEntries(recipe),
        getRecipeOutputEntries(recipe),
        Optional.ofNullable(recipe.id),
        createCostTag(recipe))

    private val cost = Nbt.readIntNbt("display_cost", tag)

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
        return CategoryIdentifier.of<ImbuingTableDisplay>(AI.MOD_ID, ImbuingRecipe.Type.ID)
    }

    companion object{

        private fun createCostTag(recipe: ImbuingRecipe): NbtCompound{
            val nbt = NbtCompound()
            val cost = recipe.getCost()
            Nbt.writeIntNbt("display_cost",cost,nbt)
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
                    val ingredient = ModCompatHelper.centerSlotGenerator(recipe)
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
            if (recipe.getAugment() == "") {
                val builder = EntryIngredient.builder()
                builder.add(EntryStacks.of(recipe.output))
                list.add(builder.build())
            } else {
                val identifier = Identifier(recipe.getAugment())
                val enchant = Registry.ENCHANTMENT.get(identifier)
                val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
                val stack: ItemStack
                val builder = EntryIngredient.builder()
                if (enchant != null){
                    stack = ItemStack(Items.ENCHANTED_BOOK,1)
                    stack.addEnchantment(enchant,1)
                    builder.add(EntryStacks.of(stack))
                    list.add(builder.build())
                } else if (modifier != null){
                    val builder2 = EntryIngredient.builder()
                    modifier.acceptableItemStacks().forEach {
                        val moddedStack = it.copy()
                        ModifierHelper.addModifierForREI(modifier.modifierId, moddedStack)
                        builder2.add(EntryStacks.of(moddedStack))
                    }
                    list.add(builder2.build())
                } else {
                    stack = ItemStack(Items.BOOK, 1)
                    builder.add(EntryStacks.of(stack))
                    list.add(builder.build())
                }
            }
            return list
        }
    }
}