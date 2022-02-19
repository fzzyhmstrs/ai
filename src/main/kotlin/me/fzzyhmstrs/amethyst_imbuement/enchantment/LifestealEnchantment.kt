package me.fzzyhmstrs.amethyst_imbuement.enchantment

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
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.sound.SoundEvent

class LifestealEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.TRIDENT,slot) {

    companion object{
        var time1 = 0L
        var time2 = 0L
        var applied = false
    }

    override fun getMinPower(level: Int): Int {
        return level * 20
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 30
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        applied = if(!applied) {
            time1 = user.world.time
            if ((time1 - 20L) >= time2 && (target is LivingEntity)) {
                user.heal(0.5f * level)
                time2 = user.world.time
            }
            true
        } else{
            false
        }
    }
}