package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.interfaces.Flavorful
import net.minecraft.item.AxeItem
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterial

class CustomSwordItem(material: ToolMaterial, attackDamage: Int, attackSpeed: Float, settings: Settings):
    SwordItem(material, attackDamage, attackSpeed, settings), Flavorful<CustomSwordItem> {
    // just to make this public
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun getFlavorItem(): CustomSwordItem {
        return this
    }
}