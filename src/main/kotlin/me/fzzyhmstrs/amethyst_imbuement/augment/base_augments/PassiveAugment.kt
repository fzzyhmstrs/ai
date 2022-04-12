package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

open class PassiveAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight,mxLvl,EnchantmentTarget.ARMOR, *slot) {

    override fun canAccept(other: Enchantment): Boolean {
        return (other !is PassiveAugment)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ImbuedJewelryItem) || (stack.isOf(RegisterItem.TOTEM_OF_AMETHYST))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.indexedEntries
        list.add(ItemStack(RegisterItem.TOTEM_OF_AMETHYST,1))
        for (entry in entries){
            val item = entry.value()
            if (item is ImbuedJewelryItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }
}