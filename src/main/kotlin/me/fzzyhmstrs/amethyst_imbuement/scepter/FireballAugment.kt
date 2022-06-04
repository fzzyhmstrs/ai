package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity.Companion.createFireball
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.FireAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class FireballAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot), FireAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(6.0F)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 3.4F
        val div = 1.0F
        val f = (-MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val g = (-MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val h = (MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        return createFireball(world, user, Vec3d(f,g,h), user.eyePos.subtract(0.0,0.2,0.0),effects, level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_GHAST_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,32,10,6,imbueLevel,LoreTier.LOW_TIER, Items.WATER_BUCKET)
    }
}