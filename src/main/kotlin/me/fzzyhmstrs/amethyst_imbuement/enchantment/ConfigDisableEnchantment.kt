package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.AbstractConfigDisableEnchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


open class ConfigDisableEnchantment(weight: Rarity,target: EnchantmentTarget, vararg slot: EquipmentSlot): AbstractConfigDisableEnchantment(weight, target, *slot) {

    protected val id: Identifier by lazy {
        Registries.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.enchants.enabledEnchants.getOrDefault(id.toString(),true)
    }
}