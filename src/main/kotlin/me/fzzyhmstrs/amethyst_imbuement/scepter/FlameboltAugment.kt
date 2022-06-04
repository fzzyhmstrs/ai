package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.FlameboltEntity
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
import net.minecraft.world.World

class FlameboltAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot), FireAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(6.0F,0.0F,0.0F)

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 2.0F
        val div = 0.75F
        return FlameboltEntity.createFlamebolt(world, user, speed, div, effects, level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_BLAZE_SHOOT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,19,4,1,imbueLevel,LoreTier.NO_TIER, Items.FIRE_CHARGE)
    }

}