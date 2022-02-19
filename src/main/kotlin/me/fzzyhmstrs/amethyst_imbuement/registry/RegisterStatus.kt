package me.fzzyhmstrs.amethyst_imbuement.registry

import it.unimi.dsi.fastutil.longs.LongImmutableList
import me.fzzyhmstrs.amethyst_imbuement.effects.*
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterStatus {

    val CUSTOM_ABSORPTION = ShieldingStatusEffect(StatusEffectCategory.BENEFICIAL,0x2552A5)
    val DRACONIC_VISION = DraconicVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xA606E5)
    val SPECTRAL_VISION = SpectralVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
    val LEAPT = LeaptStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val IMMUNITY = ImmunityStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val STRIDING = StridingStatusEffect(StatusEffectCategory.BENEFICIAL,0x48f369)
    val CHARMED = CharmedStatusEffect(StatusEffectCategory.BENEFICIAL,0xFF80FF)
    val SOULBINDING = SoulbindingStatusEffect(StatusEffectCategory.BENEFICIAL,0x4000000)

    fun registerAll(){
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","custom_absorption"), CUSTOM_ABSORPTION)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","draconic_vision"), DRACONIC_VISION)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","spectral_vision"), SPECTRAL_VISION)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","leapt"), LEAPT)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","immunity"), IMMUNITY)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","striding"), STRIDING)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","charmed"), CHARMED)
        Registry.register(Registry.STATUS_EFFECT, Identifier("amethyst_imbuement","soulbinding"), SOULBINDING)
    }

}