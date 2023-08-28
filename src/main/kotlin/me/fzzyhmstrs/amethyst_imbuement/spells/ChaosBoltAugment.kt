package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class ChaosBoltAugment: SummonProjectileAugment(ScepterTier.THREE,15) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.0F)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,18,3,
            21,imbueLevel,1,BookOfTalesItem.TALES_TIER,Items.SUSPICIOUS_STEW)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val me = MissileEntity(world, user, false)
        me.setVelocity(user,user.pitch,user.yaw,0.0f,
            2.0f,
            0.1f)
        me.passEffects(effects, level)
        return me
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }
}