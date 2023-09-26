package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlayerFireballEntity.Companion.createMeteor
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class MeteorAugment: SummonProjectileAugment(ScepterTier.THREE,5){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(12.25F,0.75f)
            .withAmplifier(2)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,32,10,
            10,imbueLevel,2, LoreTier.LOW_TIER, Items.TNT)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 4.0F
        val div = 1.0F
        val f = (-MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val g = (-MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val h = (MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        return createMeteor(world, user, Vec3d(f,g,h), user.eyePos.subtract(0.0,0.2,0.0),effects, level,this)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_GHAST_SHOOT
    }
}
