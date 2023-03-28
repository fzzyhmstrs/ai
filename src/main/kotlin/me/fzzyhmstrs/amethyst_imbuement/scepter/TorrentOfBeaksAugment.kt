package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.thrown.EggEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class TorrentOfBeaksAugment: SummonProjectileAugment(ScepterTier.TWO,6){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(2.8F,0.2f)
            .withAmplifier(14,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(14,-1),2,
            7, imbueLevel,1, LoreTier.NO_TIER, Items.CHICKEN)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = effects.amplifier(level)/10f
        val div = 1.0F
        val eggEntity = EggEntity(world, user)
        eggEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, speed, div)
        return eggEntity
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EGG_THROW
    }
}
