package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfMythosItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.Display
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class ImbuingTableDisplay(val recipe: ImbuingRecipe): Display {

    override fun getInputEntries(): MutableList<EntryIngredient> {
        val list: MutableList<EntryIngredient> = mutableListOf()
        val inputs = recipe.getInputs()
        for (i in inputs.indices){
            val input = inputs[i]
            if (recipe.getAugment() != "" && i == 6){
                val identifier = Identifier(recipe.getAugment())
                val enchant = Registry.ENCHANTMENT.get(identifier)
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
                        else -> {
                            list.add(EntryIngredient.empty())
                        }
                    }
                } else {
                    list.add(EntryIngredient.empty())
                }
                continue
            }
            val builder = EntryIngredient.builder()
            input.matchingStacks.forEach { builder.add(EntryStacks.of(it)) }
            list.add(builder.build())
        }
        return list
    }

    override fun getOutputEntries(): MutableList<EntryIngredient> {
        val list: MutableList<EntryIngredient> = mutableListOf()
        if (recipe.getAugment() == "") {
            val builder = EntryIngredient.builder()
            builder.add(EntryStacks.of(recipe.output))
            list.add(builder.build())
        } else {
            val identifier = Identifier(recipe.getAugment())
            val enchant = Registry.ENCHANTMENT.get(identifier)
            var stack = ItemStack(Items.ENCHANTED_BOOK,1)
            if (enchant != null){
                stack.addEnchantment(enchant,1)
            } else {
                stack = ItemStack(Items.BOOK, 1)
            }
            val builder = EntryIngredient.builder()
            builder.add(EntryStacks.of(stack))
            list.add(builder.build())
        }
        return list
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return CategoryIdentifier.of<ImbuingTableDisplay>(ImbuingRecipe.Type.ID)
    }

    /*override fun getDisplayLocation(): Optional<Identifier> {
        return Optional.of(recipe.id)
    }*/
}