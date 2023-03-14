package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SlashAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

@Suppress("SameParameterValue")
open class EmpoweredSlashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SlashAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.5F,1.5F,0.0F)


    override fun particleType(): DefaultParticleType{
        return ParticleTypes.CRIT
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_ATTACK_CRIT
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,20,20,15,imbueLevel,1,LoreTier.LOW_TIER, RegisterItem.GARNET_SWORD)
    }
}