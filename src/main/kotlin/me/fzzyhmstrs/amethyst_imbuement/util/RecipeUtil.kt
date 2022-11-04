package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.JsonObject
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RecipeUtil {

    internal fun buildOutputProvider(recipe: ImbuingRecipe): StackProvider{
        val augment = recipe.getAugment()
        if (augment != ""){
            val stack = ItemStack(Items.ENCHANTED_BOOK)
            val enchant = Registry.ENCHANTMENT.get(Identifier(augment))?:return EmptyStackProvider()
            EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(enchant,1))
            return SingleStackProvider(stack)
        }
        val output = recipe.output
        return SingleStackProvider(output)
    }

    internal fun buildInputProviders(recipe: ImbuingRecipe): List<StackProvider>{
        val list: MutableList<StackProvider> = mutableListOf()
        recipe.getInputs().forEachIndexed { index, it ->
            if (index == 6 && recipe.getAugment() != ""){
                val ingredient = recipe.getCenterIngredient()
                list.add(StackProvider.getProvider(ingredient))
            } else {
                list.add(StackProvider.getProvider(it))
            }
        }
        return list
    }

    internal class MultiStackProvider(private val stacks: Array<ItemStack>): StackProvider{
        var timer = -1L
        var currentIndex = 0

        override fun getStack(): ItemStack {
            val time = System.currentTimeMillis()
            if (timer == -1L){
                timer = time
            } else if (time - timer >= 1000L){
                currentIndex++
                if (currentIndex >= stacks.size){
                    currentIndex = 0
                }
                timer = time
            }
            return stacks[currentIndex]
        }
    }
    internal class SingleStackProvider(private val stack: ItemStack): StackProvider{
        override fun getStack(): ItemStack {
            return stack
        }

    }
    internal class EmptyStackProvider(): StackProvider{
        override fun getStack(): ItemStack {
            return ItemStack.EMPTY
        }
    }
    internal interface StackProvider{
        fun getStack(): ItemStack
        companion object{
            fun getProvider(ingredient: Ingredient): StackProvider{
                if (ingredient.isEmpty){
                    return EmptyStackProvider()
                }
                val stacks = ingredient.matchingStacks
                if (stacks.size == 1){
                    return SingleStackProvider(stacks[0])
                }
                return MultiStackProvider(stacks)
            }
        }
    }

    fun ingredientFromJson(json: JsonObject?): Ingredient{
        return if (json == null) Ingredient.EMPTY else Ingredient.fromJson(json)
    }

}