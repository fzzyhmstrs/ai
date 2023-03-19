package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractEquipmentAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class EquipmentAugment(weight: Rarity, mxLvl: Int = 1, target: EnchantmentTarget = EnchantmentTarget.ARMOR, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl, target, *slot) {

    val id: Identifier by lazy {
        Registry.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun canAccept(other: Enchantment?): Boolean {
        return super.canAccept(other) || ((Registry.ENCHANTMENT.getId(other) == Registry.ENCHANTMENT.getId(this) && this.maxLevel > 1))
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.toString(),true)
    }
}