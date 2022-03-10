package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class FlameboltAugment(weight: Rarity, _tier: Int, _maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(weight, _tier, _maxLvl, *slot) {

    override fun entityClass(world: World, user: LivingEntity, pierce: Boolean, level: Int): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        println(pitch)
        println(user.eyeY - 0.3 + 0.8 * MathHelper.sin(pitch * (Math.PI.toFloat() / 180)))
        val roll = user.roll
        val speed = 5.0F
        val div = 1.0F
        val userVelocity = user.velocity
        val f = -MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed + userVelocity.x
        val g = -MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed + userVelocity.y
        val h = MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed + userVelocity.z
        val sfe = SmallFireballEntity(world,
            user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)),
            user.eyeY - 0.3 - 0.8 * MathHelper.sin(pitch * (Math.PI.toFloat() / 180)),
            user.z + (user.width + 0.5f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)),
            f,g,h)
        sfe.owner = user
        return sfe
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_SHOOT
    }

}