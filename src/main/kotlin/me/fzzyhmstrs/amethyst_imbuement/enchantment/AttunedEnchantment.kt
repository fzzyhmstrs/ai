package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

class AttunedEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight,EnchantmentTarget.WEAPON,slot) {


    override fun getMinPower(level: Int): Int {
        return 15 + level * 15
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 30
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ScepterItem
    }


}