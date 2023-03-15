package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.item.ItemStack

interface Reagent{
    companion object{
        fun getReagents(inventory: Inventory): List<ItemStack>{
            val list: MutableList<ItemStack> = mutableListOf()
            for (i in 0 until inventory.size){
                if (inventory.getStack(i).item is Reagent){
                    list.add(inventory.getStack(i))
                }
            }
            return list
        }
    }
}
