package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfMythosItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class ImbuingTableDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>):
    BasicDisplay(inputs, outputs, location) {

    constructor(recipe: ImbuingRecipe): this(
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
        return CategoryIdentifier.of<ImbuingTableDisplay>(AI.MOD_ID, ImbuingRecipe.Type.ID)
    }

    companion object{

        fun serializer(): Serializer<ImbuingTableDisplay>{
            return Serializer.ofSimple { inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier> ->
                ImbuingTableDisplay(
                    inputs,
                    outputs,
                    location
                )
            }
        }

        private fun getRecipeInputEntries(recipe: ImbuingRecipe): MutableList<EntryIngredient>{
            val list: MutableList<EntryIngredient> = mutableListOf()
            val inputs = recipe.getInputs()
            for (i in inputs.indices){
                val input = inputs[i]
                if (recipe.getAugment() != "" && i == 6){
                    val identifier = Identifier(recipe.getAugment())
                    val enchant = Registry.ENCHANTMENT.get(identifier)
                    val modifier = RegisterModifier.ENTRIES.get(identifier)
                    if (enchant != null){
                        when (enchant) {
                            is BaseAugment -> {
                                val builder = EntryIngredient.builder()
                                enchant.acceptableItemStacks().forEach { builder.add(EntryStacks.of(it)) }
                                list.add(builder.build())
                            }
                            is ScepterAugment -> {
                                val builder = EntryIngredient.builder()
                                enchant.acceptableItemStacks().forEach {
                                    builder.add(EntryStacks.of(it))
                                }
                                list.add(builder.build())
                            }
                            else -> {
                                val builder = EntryIngredient.builder()
                                for (item in Registry.ITEM.iterator()){
                                    if (enchant.isAcceptableItem(ItemStack(item))){
                                        builder.add(EntryStacks.of(ItemStack(item)))
                                    }
                                }
                                list.add(builder.build())
                            }
                        }
                    } else if(modifier != null) {
                        val builder = EntryIngredient.builder()
                        modifier.acceptableItemStacks().forEach {
                            builder.add(EntryStacks.of(it))
                        }
                        list.add(builder.build())
                    } else {
                        list.add(EntryIngredient.empty())
                    }
                    continue
                }
                val builder = EntryIngredient.builder()
                input.matchingStacks.forEach {
                    val item = it.item
                    val stack = it.copy()
                    if (item is BookOfLoreItem){
                        item.addLoreKeyForREI(stack,recipe.getAugment())
                    } else if (item is BookOfMythosItem){
                        item.addLoreKeyForREI(stack,recipe.getAugment())
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
                val modifier = RegisterModifier.ENTRIES.get(identifier)
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
                        ScepterObject.addModifierForREI(modifier.modifierId, moddedStack)
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