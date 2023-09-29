package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.item.interfaces.Reagent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class IgnitedGemItem(settings: Settings): Item(settings), Reagent {

    abstract fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>)

    abstract fun getModifier(): Identifier

}
