package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class WitheringBoltAugment(weight: Rarity, _tier: Int, _maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(weight, _tier, _maxLvl, *slot) {
    override fun entityClass(world: World, user: LivingEntity, pierce: Boolean, level: Int): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 1.5F
        val f = -MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed
        val g = -MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * speed
        val h = MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed
        val wse = WitherSkullEntity(world,user,f.toDouble(),g.toDouble(),h.toDouble())
        wse.setPos(user.x,user.eyeY-0.2,user.z)
        wse.isCharged = false
        return wse
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SHOOT
    }
}