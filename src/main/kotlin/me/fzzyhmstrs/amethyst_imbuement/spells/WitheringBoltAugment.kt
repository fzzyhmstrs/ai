package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlayerWitherSkullEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class WitheringBoltAugment: SummonProjectileAugment(ScepterTier.TWO,5){

    override val baseEffect: AugmentEffect = super.baseEffect.withDamage(7.5f,0.5f)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(30,-2),13,
            11,imbueLevel,2,LoreTier.LOW_TIER, Items.WITHER_SKELETON_SKULL)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 4.0F
        val f = (-MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed) + user.velocity.x
        val g = (-MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * speed) + user.velocity.y
        val h = (MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * speed) + user.velocity.z
        val wse = PlayerWitherSkullEntity(world,user,f,g,h)
        wse.passEffects(effects, level)
        wse.setPos(user.x,user.eyeY-0.2,user.z)
        wse.isCharged = false
        wse.setAugment(this)
        return wse
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SHOOT
    }
}
