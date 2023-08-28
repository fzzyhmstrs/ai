package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.DamageEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

class CleavingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight,EnchantmentTarget.WEAPON,*slot) {


    override fun getMinPower(level: Int): Int {
        return 15 + (level - 1) * 15
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 20
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    override fun getAttackDamage(level: Int, group: EntityGroup): Float {
        return if(checkEnabled()) {
            1.0F + 1.0F * level
        } else {
            0.0f
        }
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (target is PlayerEntity && checkEnabled()){
            target.itemCooldownManager.set(Items.SHIELD, 10*level)
        }
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !is DamageEnchantment
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is AxeItem) && checkEnabled()
    }
}