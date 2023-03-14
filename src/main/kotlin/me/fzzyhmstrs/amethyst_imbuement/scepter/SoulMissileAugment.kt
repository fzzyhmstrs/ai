package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.SoulMissileEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class SoulMissileAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.9F,0.1F,0.0F)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val me = SoulMissileEntity(world, user)
        me.setVelocity(user,user.pitch,user.yaw,0.0f,
            2.0f,
            0.1f)
        me.passEffects(effects, level)
        return me
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,16,3,1,imbueLevel,1,LoreTier.NO_TIER,Items.SOUL_SAND)
    }

}