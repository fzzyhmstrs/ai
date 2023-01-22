package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class FlavorHorseArmorItem(bonus: Int, name: String, settings: Settings): HorseArmorItem(bonus, name, settings), Flavorful<FlavorHorseArmorItem> {
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun getFlavorItem(): FlavorHorseArmorItem {
        return this
    }
}