package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.entity.attribute.EntityAttributeModifier

interface SpellcastersReagent: Reagent{
    fun getAttributeModifier(): EntityAttributeModifier
}
