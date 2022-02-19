package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.entity.MissileEntity
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

open class SummonProjectileAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    open fun entityClass(world: World, user: LivingEntity,pierce: Boolean = false, level: Int = 1): ProjectileEntity {
        val me = MissileEntity(world, user, pierce)
            me.setVelocity(user,user.pitch,user.yaw,0.0f,
                2.0f,
                0.1f)
        return me
    }

    open fun soundEvent(): SoundEvent{
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }
}