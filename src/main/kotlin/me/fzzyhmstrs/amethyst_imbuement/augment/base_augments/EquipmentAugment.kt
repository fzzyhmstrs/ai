package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.registry.Registries

open class EquipmentAugment(weight: Rarity, mxLvl: Int = 1, target: EnchantmentTarget = EnchantmentTarget.ARMOR, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl, target, *slot) {

    override fun checkEnabled(): Boolean{
        val id = Registries.ENCHANTMENT.getId(this)?:return true
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.path,true)
    }
}