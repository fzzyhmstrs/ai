package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.FireAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.entity.FlameboltEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonProjectileAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class FlamewaveAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot),
    FireAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(6.0F,0.0F,0.0F)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 1.5F
        val div = 1.25F
        return FlameboltEntity.createFlamebolt(world, user, speed, div, effects, level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,2,3,16,imbueLevel, LoreTier.HIGH_TIER, Items.FIRE_CHARGE)
    }

}