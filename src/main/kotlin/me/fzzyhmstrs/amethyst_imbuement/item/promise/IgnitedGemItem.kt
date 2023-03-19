package me.fzzyhmstrs.amethyst_imbuement.item.promise

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text

abstract class IgnitedGemItem(settings: Settings): Item(settings) {

    abstract fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>)

}
