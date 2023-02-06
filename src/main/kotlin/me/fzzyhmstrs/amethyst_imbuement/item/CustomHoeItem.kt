package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.item.HoeItem
import net.minecraft.item.ToolMaterial

class CustomHoeItem(material: ToolMaterial, attackDamage: Int, attackSpeed: Float, settings: Settings):
    HoeItem(material, attackDamage, attackSpeed, settings), Flavorful<CustomHoeItem> {
    // just to make this public
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun getFlavorItem(): CustomHoeItem {
        return this
    }
}