package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.thrown.EnderPearlEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class TeleportAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.Builder().with(AugmentType.ENTITY).with(AugmentType.PROJECTILE).with(AugmentType.BENEFICIAL).build()) {
    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(1.4,0.1)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(210,-10),30,
            13,imbueLevel,8,LoreTier.LOW_TIER, Items.ENDER_PEARL)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val enderPearlEntity = EnderPearlEntity(world, user)
        enderPearlEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, effects.range(level).toFloat(), 1.0f)
        return enderPearlEntity
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_PEARL_THROW
    }
}