package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.entity.MissileEntity
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World

open class SummonProjectileAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int): Boolean {
        val pierce = EnchantmentHelper.getEnchantmentId(this)?.path == "soul_missile"
        val bl = spawnProjectileEntity(world,user,entityClass(world,user,pierce,level),soundEvent())
        if (bl) {
            if (needsClient()) ScepterObject.addClientTaskToQueue(this, ScepterItem.ClientTaskInstance(null, level, null))
        }
        return bl
    }

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