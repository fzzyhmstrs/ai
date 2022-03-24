package me.fzzyhmstrs.amethyst_imbuement.tool

import net.minecraft.item.HoeItem
import net.minecraft.item.ToolMaterial

class CustomHoeItem(material: ToolMaterial, attackDamage: Int, attackSpeed: Float, settings: Settings): HoeItem(material, attackDamage, attackSpeed, settings) {
    // just to make this public
}