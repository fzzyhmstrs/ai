package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import me.fzzyhmstrs.amethyst_core.scepter_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SoulAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.base_augments.SlashAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes

@Suppress("SameParameterValue")
open class SpectralSlashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SlashAugment(tier, maxLvl, *slot),
    SoulAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.0F,2.0F,0.0F)


    override fun particleType(): DefaultParticleType{
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,16,4,8,imbueLevel,LoreTier.LOW_TIER, Items.IRON_SWORD)
    }
}