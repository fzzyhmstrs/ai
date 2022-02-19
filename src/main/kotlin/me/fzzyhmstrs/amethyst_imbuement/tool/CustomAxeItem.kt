package me.fzzyhmstrs.amethyst_imbuement.tool

import net.minecraft.item.AxeItem
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial

class CustomAxeItem(material: ToolMaterial, attackDamage: Float, attackSpeed: Float, settings: Settings): AxeItem(material, attackDamage, attackSpeed, settings) {
    // just to make this public
}