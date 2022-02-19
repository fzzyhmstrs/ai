package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

class ResilienceAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem && stack.item !is DynamicAttributeTool
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.entries
        for (entry in entries){
            val item = entry.value
            if (item is ArmorItem && item !is DynamicAttributeTool){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }

}