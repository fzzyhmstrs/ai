package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.effects.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterStatus {



    val SHIELDING = ShieldingStatusEffect(StatusEffectCategory.BENEFICIAL,0x2552A5)
    val DRACONIC_VISION = DraconicVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xA606E5)
    val SPECTRAL_VISION = SpectralVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
    val LEAPT = LeaptStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val INSPIRED = InspiredStatusEffect(StatusEffectCategory.BENEFICIAL,0xB277FF)
    val IMMUNITY = ImmunityStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val STRIDING = StridingStatusEffect(StatusEffectCategory.BENEFICIAL,0x48f369)
    val CHARMED = CharmedStatusEffect(StatusEffectCategory.BENEFICIAL,0xFF80FF)
    val SOULBINDING = SoulbindingStatusEffect(StatusEffectCategory.BENEFICIAL,0x4000000)
    val RESONATING = ResonatingStatusEffect(StatusEffectCategory.HARMFUL,0x39D6E0)
    val CURSED = CursedStatusEffect(StatusEffectCategory.BENEFICIAL,0x000000)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"94974394-c38a-11ed-afa1-0242ac120002",-2.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"2c4481ee-c5f1-11ed-afa1-0242ac120002",-1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"94974614-c38a-11ed-afa1-0242ac120002",0.1,EntityAttributeModifier.Operation.ADDITION)

    fun registerAll(){
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"custom_absorption"), SHIELDING)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"draconic_vision"), DRACONIC_VISION)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"spectral_vision"), SPECTRAL_VISION)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"leapt"), LEAPT)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"inspired"), INSPIRED)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"immunity"), IMMUNITY)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"striding"), STRIDING)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"charmed"), CHARMED)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"soulbinding"), SOULBINDING)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"resonating"), RESONATING)
        Registry.register(Registry.STATUS_EFFECT, Identifier(AI.MOD_ID,"cursed"), CURSED)
    }
}