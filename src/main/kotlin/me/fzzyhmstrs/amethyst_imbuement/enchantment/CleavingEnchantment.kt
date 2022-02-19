package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.DamageEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*

class CleavingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight,EnchantmentTarget.WEAPON,slot) {


    override fun getMinPower(level: Int): Int {
        return 15 + (level - 1) * 15
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun getAttackDamage(level: Int, group: EntityGroup): Float {
        return 1.0F + 1.0F * level
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is PlayerEntity){
            target.itemCooldownManager.set(Items.SHIELD, 10*level)
        }

    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !is DamageEnchantment
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is AxeItem)
    }
}