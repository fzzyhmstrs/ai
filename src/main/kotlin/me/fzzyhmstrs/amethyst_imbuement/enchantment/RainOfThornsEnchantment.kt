package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.ItemStack

class RainOfThornsEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.BOW,*slot) {

    override fun getMinPower(level: Int): Int {
        return 25 + level * 15
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 35
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FLAME && other !== Enchantments.INFINITY
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return super.isAcceptableItem(stack) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (user.world.isClient || !checkEnabled()) return
        for (i in 1..level) {
            val arrow = ArrowEntity(target.world, target.x, target.eyeY, target.z)
            val rnd = user.world.random.nextInt(360) - 180
            arrow.setVelocity(user,0.0F,rnd.toFloat(),0.0f,3.0f,1.0f)
            target.world.spawnEntity(arrow)
        }
    }
}