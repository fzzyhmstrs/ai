package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class WastingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight,EnchantmentTarget.WEAPON,*slot) {

    override fun getMinPower(level: Int): Int {
        return 5 + (level - 1) * 15
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,4)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return super.isAcceptableItem(stack) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is LivingEntity && checkEnabled()) {
            if(!user.world.isClient()){
                var i = 2
                if (level > 2) {
                    i = 3
                }
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 15+(5*level), i))
            }
        }
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.KNOCKBACK
    }
}
