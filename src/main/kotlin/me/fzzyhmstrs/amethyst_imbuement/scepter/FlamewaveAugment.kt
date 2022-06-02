package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.FlameboltEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
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
import net.minecraft.world.World

class FlamewaveAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(6.0F,0.0F,0.0F)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val pitch = user.pitch
        val speed = 1.5F
        val div = 1.25F
        val fbe = FlameboltEntity(
            world, user, speed, div,
            user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                pitch * (Math.PI.toFloat() / 180)
            ),
            user.eyeY - 0.6 - 0.8 * MathHelper.sin(pitch * (Math.PI.toFloat() / 180)),
            user.z + (user.width + 0.5f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                pitch * (Math.PI.toFloat() / 180)
            ),
        )
        fbe.entityEffects.setDamage(effects.damage(level))
        fbe.entityEffects.addAmplifier(effects.amplifier(level))
        fbe.entityEffects.setConsumers(effects)
        return fbe
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,2,3,16,imbueLevel,LoreTier.HIGH_TIER, Items.FIRE_CHARGE)
    }

}