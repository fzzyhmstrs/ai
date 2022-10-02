package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.*

class LifestealEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.TRIDENT,*slot) {

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
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)) && enabled
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (!enabled) return
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