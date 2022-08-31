package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.TravelerAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.thrown.EnderPearlEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class TeleportAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonProjectileAugment(tier, maxLvl, *slot), TravelerAugment {

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val enderPearlEntity = EnderPearlEntity(world, user)
        enderPearlEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f)
        return enderPearlEntity
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_PEARL_THROW
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,200,15,7,imbueLevel,LoreTier.LOW_TIER, Items.ENDER_PEARL)
    }
}