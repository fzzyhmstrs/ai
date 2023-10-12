package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.ChaosBoltEntity
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class ChaosBoltAugment: SummonProjectileAugment(ScepterTier.THREE,21) {

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(1.95f,0.05f)
                                                .withAmplifier(0)
                                                .withDuration(80)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,24,8,
            21,imbueLevel,2,BookOfTalesItem.TALES_TIER,Items.SUSPICIOUS_STEW)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 2.0F
        val div = 0.75F
        return ChaosBoltEntity.createChaosBolt(world, user, speed, div, effects, level,this)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }
}
