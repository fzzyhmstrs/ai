package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.sound.SoundEvent

class SteadfastEnchantment(weight: Enchantment.Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.ARMOR,slot) {


    override fun getMinPower(level: Int): Int {
        return 15 * level
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem && stack.item !is DynamicAttributeTool
    }

}