package me.fzzyhmstrs.amethyst_imbuement.item

interface Reactant{
    fun canReact(stack: ItemStack, reagants: List<ItemStack>): Boolean
    fun react(stack: ItemStack, reagants: List<ItemStack>)
}
