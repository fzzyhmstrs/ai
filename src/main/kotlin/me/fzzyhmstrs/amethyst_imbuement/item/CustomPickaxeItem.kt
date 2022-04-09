package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial

class CustomPickaxeItem(material: ToolMaterial,attackDamage: Int,attackSpeed: Float,settings: Settings): PickaxeItem(material, attackDamage, attackSpeed, settings) {
    // just to make this public
}