package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.interfaces.Flavorful
import net.minecraft.item.AxeItem
import net.minecraft.item.ToolMaterial

class CustomAxeItem(material: ToolMaterial, attackDamage: Float, attackSpeed: Float, settings: Settings):
    AxeItem(material, attackDamage, attackSpeed, settings), Flavorful<CustomAxeItem> {
    // just to make this public
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun getFlavorItem(): CustomAxeItem {
        return this
    }
}