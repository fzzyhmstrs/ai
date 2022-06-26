package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

class ResilienceAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.indexedEntries
        for (entry in entries){
            val item = entry.value()
            if (item is ArmorItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }

}