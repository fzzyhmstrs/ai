package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem

class PuncturingEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.CROSSBOW,*slot) {

    override fun getMinPower(level: Int): Int {
        return 20 + level * 10
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 50
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,6)
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.PIERCING && other !== Enchantments.MULTISHOT && other !== Enchantments.IMPALING
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem)) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (user.world.isClient || !checkEnabled()) return
        if (target is LivingEntity) {
            if(!target.isDead){
                target.setInvulnerable(false) //these two lines take away damage invulnerability
                target.timeUntilRegen = 0
                target.damage(user.damageSources.generic(), 0.5f * level) //GENERIC damage bypasses armor
            }
        }

    }
}