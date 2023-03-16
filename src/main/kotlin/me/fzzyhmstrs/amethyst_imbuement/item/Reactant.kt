package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.item.ItemStack

interface Reactant{
    fun canReact(stack: ItemStack, reagents: List<ItemStack>): Boolean
    fun react(stack: ItemStack, reagents: List<ItemStack>)
}
