package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

class CreateLavaAugment(weight: Enchantment.Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.WEAPON,slot) {
    override fun getMinPower(level: Int): Int {
        return 150000
    }

    override fun getMaxPower(level: Int): Int {
        return 155000
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return false
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return false
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isOf(RegisterItem.IRIDESCENT_SCEPTER) || stack.isOf(RegisterItem.LUSTROUS_SCEPTER)
    }
}