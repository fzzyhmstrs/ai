package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects

class WastingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight,EnchantmentTarget.WEAPON,slot) {

    companion object{
        var applied = false
    }

    override fun getMinPower(level: Int): Int {
        return 5 + (level - 1) * 15
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return 4
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is LivingEntity) {
            applied = if(!applied){
                var i = 2
                if (level > 2) {
                    i = 3
                }
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 15+(5*level), i))
                true
            } else {
                false
            }
        }
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.KNOCKBACK
    }
}