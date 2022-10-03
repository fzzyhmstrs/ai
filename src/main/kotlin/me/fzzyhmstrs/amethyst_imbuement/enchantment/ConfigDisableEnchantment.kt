package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry


open class ConfigDisableEnchantment(weight: Rarity,target: EnchantmentTarget, vararg slot: EquipmentSlot): Enchantment(weight, target, slot) {

    protected val enabled: Boolean by lazy {
        checkEnabled()
    }

    private fun checkEnabled(): Boolean{
        val id = Registry.ENCHANTMENT.getId(this)?:return true
        return AiConfig.enchantments.enabledEnchantments.getOrDefault(id.path,true)
    }


    override fun getName(level: Int): Text {
        val baseText = super.getName(level) as MutableText
        if (!enabled) {
            return baseText
                .append(AcText.translatable("scepter.augment.disabled"))
                .formatted(Formatting.DARK_RED)
                .formatted(Formatting.STRIKETHROUGH)
        }
        return baseText
    }
}