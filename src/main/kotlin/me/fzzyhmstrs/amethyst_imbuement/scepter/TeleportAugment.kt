package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.thrown.EnderPearlEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class TeleportAugment(weight: Rarity, _tier: Int, _maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(weight, _tier, _maxLvl, *slot) {
    override fun entityClass(world: World, user: LivingEntity, pierce: Boolean, level: Int): ProjectileEntity {
        val enderPearlEntity = EnderPearlEntity(world, user)
        enderPearlEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f)
        return enderPearlEntity
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_PEARL_THROW
    }
}