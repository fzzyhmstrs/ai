package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

class MultiJumpEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.ARMOR_FEET,*slot) {

    override fun getMinPower(level: Int): Int {
        return 30
    }

    override fun getMaxPower(level: Int): Int {
        return 90
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,1)
    }

    override fun isTreasure(): Boolean {
        return false
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return true
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return true
    }
}