package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.entity.MissileEntity
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.world.World

abstract class SummonProjectileAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(
        world: World,
        user: LivingEntity,
        hand: Hand,
        level: Int,
        modifiers: List<AugmentModifier>?
    ): Boolean {
        return spawnProjectileEntity(world, user, entityClass(world, user, level), soundEvent())
    }

    open fun entityClass(world: World, user: LivingEntity,level: Int = 1): ProjectileEntity {
        val me = MissileEntity(world, user, false)
            me.setVelocity(user,user.pitch,user.yaw,0.0f,
                2.0f,
                0.1f)
        return me
    }

    private fun spawnProjectileEntity(world: World, entity: LivingEntity, projectile: ProjectileEntity, soundEvent: SoundEvent): Boolean{
        val bl = world.spawnEntity(projectile)
        world.playSound(
            null,
            entity.blockPos,
            soundEvent,
            SoundCategory.PLAYERS,
            1.0f,
            world.getRandom().nextFloat() * 0.4f + 0.8f)
        return bl
    }
}