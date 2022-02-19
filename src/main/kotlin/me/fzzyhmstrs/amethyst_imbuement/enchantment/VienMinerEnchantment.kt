package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.AxeItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem

class VienMinerEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight,EnchantmentTarget.DIGGER,slot) {

    override fun getMinPower(level: Int): Int {
        return 30 + (level - 1) * 20
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 30
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FORTUNE
    }
}