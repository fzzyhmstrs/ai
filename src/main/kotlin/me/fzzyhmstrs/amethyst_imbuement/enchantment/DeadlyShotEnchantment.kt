package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

class DeadlyShotEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.CROSSBOW,*slot) {

    override fun getMinPower(level: Int): Int {
        return 10 + (level - 1) * 23
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 15
    }

    override fun getMaxLevel(): Int {
        return 3
    }
    fun getSpeed(level: Int, firework: Boolean, sniper: Boolean): Float {
        val actLvl = if (enabled){level}else{0}
        return if (firework) {
            if (sniper) {
                2.2f + (0.35f * actLvl)
            } else {
                1.6f + (0.25f * actLvl)
            }
        } else {
            if (sniper) {
                4.0f + (0.85f * actLvl)
            } else {
                3.15f + (0.75f * actLvl)
            }
        }
    }

}