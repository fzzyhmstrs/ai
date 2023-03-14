package me.fzzyhmstrs.amethyst_imbuement.item.promise

import net.minecraft.item.Item
import net.minecraft.text.Text

abstract class IgnitedGemItem(settings: Settings): Item(settings) {

    abstract fun giveTooltipHint():List<Text>

}