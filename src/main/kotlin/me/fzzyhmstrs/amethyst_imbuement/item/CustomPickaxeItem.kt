package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.interfaces.Flavorful
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial

class CustomPickaxeItem(material: ToolMaterial,attackDamage: Int,attackSpeed: Float,settings: Settings):
    PickaxeItem(material, attackDamage, attackSpeed, settings), Flavorful<CustomPickaxeItem> {
    // just to make this public
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun getFlavorItem(): CustomPickaxeItem {
        return this
    }
}