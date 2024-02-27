package me.fzzyhmstrs.amethyst_imbuement.compat.mcde

import net.backupcup.mcde.util.EnchantmentUtils
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack

object McdeCompat {
    fun ignoreEnchantment(enchantment: Enchantment, stack: ItemStack): Boolean{
        return EnchantmentUtils.isGilding(enchantment,stack)
    }
}