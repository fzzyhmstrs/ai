package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SlashAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes

@Suppress("SameParameterValue")
open class SpectralSlashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SlashAugment(tier, maxLvl, *slot){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F,0.0F)
            .withRange(2.625,0.125,0.0)


    override fun particleType(): DefaultParticleType{
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,18,4,8,imbueLevel,1,LoreTier.LOW_TIER, Items.IRON_SWORD)
    }
}