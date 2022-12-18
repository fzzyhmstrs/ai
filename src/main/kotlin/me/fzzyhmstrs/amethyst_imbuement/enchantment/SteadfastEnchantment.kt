package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack

class SteadfastEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.ARMOR,*slot) {


    override fun getMinPower(level: Int): Int {
        return 15 * level
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ArmorItem) && enabled
    }

}