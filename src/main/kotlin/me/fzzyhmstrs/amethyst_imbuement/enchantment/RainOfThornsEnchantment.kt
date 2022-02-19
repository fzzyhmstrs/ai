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
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.TridentItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent

class RainOfThornsEnchantment(weight: Rarity, vararg slot: EquipmentSlot): Enchantment(weight, EnchantmentTarget.BOW,slot) {

    companion object{
        var health1 = 0.0F
        var health2 = 0.0F
        var time1 = 0L
        var time2 = 0L
    }

    override fun getMinPower(level: Int): Int {
        return 25 + level * 15
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 35
    }

    override fun getMaxLevel(): Int {
        return 3
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FLAME && other !== Enchantments.INFINITY
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (user.world !is ServerWorld) return
        for (i in 1..level) {
            val arrow = ArrowEntity(target.world, target.x, target.eyeY, target.z)
            val rnd = user.world.random.nextInt(360) - 180
            arrow.setVelocity(user,0.0F,rnd.toFloat(),0.0f,3.0f,1.0f)
            target.world.spawnEntity(arrow)
        }
    }
}