package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
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

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 2.0F
        val f = (-MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed) + user.velocity.x
        val g = (-MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * speed) + user.velocity.y
        val h = (MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed) + user.velocity.z
        val wse = WitherSkullEntity(world,user,f,g,h)
        wse.setPos(user.x,user.eyeY-0.2,user.z)
        wse.isCharged = false
        return wse
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,28,13,10,imbueLevel,LoreTier.LOW_TIER, Items.WITHER_SKELETON_SKULL)
    }
}