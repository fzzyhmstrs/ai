package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class NightVisionEnchantment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR_HEAD, *slot) {


    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        addStatusToQueue(user,StatusEffects.NIGHT_VISION,400,0)
    }

    override fun getMinPower(level: Int): Int {
        return 30
    }

    override fun getMaxPower(level: Int): Int {
        return 90
    }

    override fun getMaxLevel(): Int {
        return 1
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

    override fun canAccept(other: Enchantment?): Boolean {
        return super.canAccept(other) && other !== Enchantments.AQUA_AFFINITY && other !== Enchantments.RESPIRATION
    }

}