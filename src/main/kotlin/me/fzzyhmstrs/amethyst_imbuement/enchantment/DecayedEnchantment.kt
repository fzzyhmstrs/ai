package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem

class DecayedEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.WEAPON,*slot) {

    override fun getMinPower(level: Int): Int {
        return 45
    }

    override fun getMaxPower(level: Int): Int {
        return 85
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,1)
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FLAME && other !== Enchantments.FIRE_ASPECT && other !== RegisterEnchantment.WASTING && other !== RegisterEnchantment.CONTAMINATED
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (!checkEnabled()) return
        if (target is LivingEntity) {
            if(!user.world.isClient()) {
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 100, 1))
            }
        }
    }
}
