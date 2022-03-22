package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class WitheringBoltAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot) {

    override fun entityClass(world: World, user: LivingEntity, level: Int): ProjectileEntity {
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

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,28,13,14,imbueLevel,1, Items.WITHER_SKELETON_SKULL)
    }
}