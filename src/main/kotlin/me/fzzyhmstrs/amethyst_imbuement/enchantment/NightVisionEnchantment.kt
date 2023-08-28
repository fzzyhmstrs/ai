package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractEquipmentAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class NightVisionEnchantment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_HEAD, *slot) {

    private val id: Identifier by lazy {
        Registries.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.enchants.enabledEnchants.getOrDefault(id.toString(),true)
    }

    override fun equipmentEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (!checkEnabled()) return
        EffectQueue.addStatusToQueue(user,StatusEffects.NIGHT_VISION,400,0)
    }

    override fun getMinPower(level: Int): Int {
        return 30
    }

    override fun getMaxPower(level: Int): Int {
        return 90
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,1)
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
        return super.canAccept(other) && other !== Enchantments.RESPIRATION
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return super.isAcceptableItem(stack) && checkEnabled()
    }

}