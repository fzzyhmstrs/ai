package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

interface Reactant{
    fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity? = null): Boolean
    fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity? = null)
}
