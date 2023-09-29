package me.fzzyhmstrs.amethyst_imbuement.item.interfaces

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType

interface Reactant{
    fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity? = null, type: RecipeType<*>? = null): Boolean
    fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity? = null, type: RecipeType<*>? = null)
}
