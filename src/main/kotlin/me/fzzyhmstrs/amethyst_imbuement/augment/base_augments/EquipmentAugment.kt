package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.registry.Registry

open class EquipmentAugment(weight: Rarity, mxLvl: Int = 1, target: EnchantmentTarget = EnchantmentTarget.ARMOR, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl, target, *slot) {

    override fun canAccept(other: Enchantment?): Boolean {
        return super.canAccept(other) || ((Registries.ENCHANTMENT.getId(other) == Registries.ENCHANTMENT.getId(this) && this.maxLevel > 1))
    }

    override fun checkEnabled(): Boolean{
        val id = Registry.ENCHANTMENT.getId(this)?:return true
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.path,true)
    }
}