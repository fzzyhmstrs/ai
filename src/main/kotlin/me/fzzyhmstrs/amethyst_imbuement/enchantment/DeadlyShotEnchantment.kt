package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

class DeadlyShotEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.CROSSBOW,*slot) {

    override fun getMinPower(level: Int): Int {
        return 10 + (level - 1) * 23
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 15
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    fun getLevel(stack: ItemStack): Int{
        if (!checkEnabled()) return 0
        return EnchantmentHelper.getLevel(RegisterEnchantment.DEADLY_SHOT,stack)
    }

    fun getSpeed(level: Int, firework: Boolean, sniper: Boolean): Float {
        return if (firework) {
            if (sniper) {
                2.2f + (0.35f * level)
            } else {
                1.6f + (0.25f * level)
            }
        } else {
            if (sniper) {
                4.0f + (0.85f * level)
            } else {
                3.15f + (0.75f * level)
            }
        }
    }

}