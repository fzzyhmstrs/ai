package me.fzzyhmstrs.amethyst_imbuement.item.promise

import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

abstract class IgnitedGemItem(settings: Settings): Item(settings) {

    abstract fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack):List<Text>

}
