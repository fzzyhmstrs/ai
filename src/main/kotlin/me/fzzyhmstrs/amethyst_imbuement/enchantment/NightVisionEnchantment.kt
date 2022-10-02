package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry

class NightVisionEnchantment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_HEAD, *slot) {

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
                .append(Text.translatable("scepter.augment.disabled"))
                .formatted(Formatting.DARK_RED)
                .formatted(Formatting.STRIKETHROUGH)
        }
        return baseText
    }

    override fun equipmentEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (!enabled) return
        EffectQueue.addStatusToQueue(user,StatusEffects.NIGHT_VISION,400,0)
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

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.AQUA_AFFINITY && other !== Enchantments.RESPIRATION
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return super.isAcceptableItem(stack) && enabled
    }

}