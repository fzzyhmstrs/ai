package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.ProjectileDamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.sound.SoundEvent

class ContaminatedEnchantment(weight: Enchantment.Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.TRIDENT,slot) {

    companion object{
        var applied = false
    }

    override fun getMinPower(level: Int): Int {
        return 35
    }

    override fun getMaxPower(level: Int): Int {
        return 75
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FLAME && other !== Enchantments.FIRE_ASPECT && other !== RegisterEnchantment.WASTING && other !== RegisterEnchantment.DECAYED
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is LivingEntity) {
            if (target.recentDamageSource !is ProjectileDamageSource){
                return
            }
            applied = if(!applied) {
                target.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 100, 1))
                true
            } else {
                false
            }
        }
    }
}