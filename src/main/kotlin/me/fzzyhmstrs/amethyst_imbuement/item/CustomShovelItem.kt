package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.interfaces.Flavorful
import net.minecraft.item.ShovelItem
import net.minecraft.item.ToolMaterial

class CustomShovelItem(material: ToolMaterial, attackDamage: Float, attackSpeed: Float, settings: Settings):
    ShovelItem(material, attackDamage, attackSpeed, settings), Flavorful<CustomShovelItem> {
    // just to make this public
    override var flavor: String = ""
    override var flavorDesc: String = ""
    override var glint: Boolean = false

    override fun getFlavorItem(): CustomShovelItem {
        return this
    }
}