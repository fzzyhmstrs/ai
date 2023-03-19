package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractEquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.util.registry.Registry
import net.minecraft.util.Identifier

class BulwarkEnchantment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW,*slot) {

    private val id: Identifier by lazy {
        Registry.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.enchants.enabledEnchants.getOrDefault(id.toString(),true)
    }

    override fun getMinPower(level: Int): Int {
        return 25
    }

    override fun getMaxPower(level: Int): Int {
        return 45
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(id.toString(),1)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ShieldItem)
    }

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        //user is the user
        if (user.world.isClient || !enabled) return false
        user.heal(1.0f)
        return true
    }

}