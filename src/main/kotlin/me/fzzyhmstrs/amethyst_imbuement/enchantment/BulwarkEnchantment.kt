package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry

class BulwarkEnchantment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW,*slot) {

    private val enabled: Boolean by lazy {
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

    override fun getMinPower(level: Int): Int {
        return 25
    }

    override fun getMaxPower(level: Int): Int {
        return 45
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ShieldItem)
    }

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        //user is the user
        if (user.world !is ServerWorld || !enabled) return false
        user.heal(1.0f)
        return true
    }

}