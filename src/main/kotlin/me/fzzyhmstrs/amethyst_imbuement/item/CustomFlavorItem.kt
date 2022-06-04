package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

class CustomFlavorItem(settings: Settings, nameSpace: String = AI.MOD_ID, flavor: String, glint: Boolean) : Item(settings) {

    private val ns: String = nameSpace
    private val ttn: String = flavor
    private val itemGlint: Boolean = glint

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip.add(TranslatableText("item.$ns.$ttn.tooltip1").formatted(Formatting.WHITE, Formatting.ITALIC))
    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return if (itemGlint) {
            true
        } else {
            super.hasGlint(stack)
        }
    }
}