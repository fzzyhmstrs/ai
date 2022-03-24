package me.fzzyhmstrs.amethyst_imbuement.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem

class PuncturingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.TRIDENT,slot) {

    companion object{
        var health1 = 0.0F
        var health2 = 0.0F
        var time1 = 0L
        var time2 = 0L
    }

    override fun getMinPower(level: Int): Int {
        return 20 + level * 10
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 50
    }

    override fun getMaxLevel(): Int {
        return 6
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.PIERCING && other !== Enchantments.MULTISHOT && other !== Enchantments.IMPALING
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is CrossbowItem) || (stack.item is TridentItem)
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is LivingEntity) {
            health1 = target.health //prevents double damaging
            time1 = target.world.time //stops players from spamming their attacks to get true damage. limits to 1 per second
            if(!target.isDead){
                if(health2 != health1 && (time1-20L) >= time2) {
                    target.setInvulnerable(false) //these two lines take away damage invulnerability
                    target.timeUntilRegen = 0
                    target.damage(DamageSource.GENERIC, 0.5f * level) //GENERIC damage bypasses armor
                    health2 = target.health
                    time2 = target.world.time
                } else {
                    health1 = 0.0F
                    health2 = 0.0F
                }
            }
        }
    }
}