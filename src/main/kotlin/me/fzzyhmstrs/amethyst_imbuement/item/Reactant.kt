package me.fzzyhmstrs.amethyst_imbuement.item

interface Reactant{
    fun react(stack: ItemStack, reagants: List<ItemStack>)
}
