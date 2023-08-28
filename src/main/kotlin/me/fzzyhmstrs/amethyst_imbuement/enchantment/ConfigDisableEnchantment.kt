package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.AbstractConfigDisableEnchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot


open class ConfigDisableEnchantment(weight: Rarity,target: EnchantmentTarget, vararg slot: EquipmentSlot): AbstractConfigDisableEnchantment(weight, target, *slot) {

    override fun checkEnabled(): Boolean{
        return AiConfig.enchants.isEnchantEnabled(this)
    }
}