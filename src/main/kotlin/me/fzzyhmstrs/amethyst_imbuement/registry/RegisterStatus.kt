package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.status.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object RegisterStatus {



    val SHIELDING = ShieldingStatusEffect(StatusEffectCategory.BENEFICIAL,0x2552A5)
    val DRACONIC_VISION = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xA606E5)
    val SPECTRAL_VISION = SpectralVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
    val LEAPT = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val INSPIRED = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xB277FF)
    val IMMUNITY = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000)
    val STRIDING = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x48f369)
    val CHARMED = CharmedStatusEffect(StatusEffectCategory.BENEFICIAL,0xFF80FF)
    val STUNNED = StunnedStatusEffect(StatusEffectCategory.HARMFUL,0xFFFF00)
    val SOULBINDING = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x4000000)
    val RESONATING = CustomStatusEffect(StatusEffectCategory.HARMFUL,0x39D6E0)
    val BONE_ARMOR: StatusEffect = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xC6C6C6)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"0c7600d6-2695-11ee-be56-0242ac120002",3.0,EntityAttributeModifier.Operation.ADDITION)
    val CURSED: StatusEffect = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x010101)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"94974394-c38a-11ed-afa1-0242ac120002",-2.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"2c4481ee-c5f1-11ed-afa1-0242ac120002",-1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"94974614-c38a-11ed-afa1-0242ac120002",0.1,EntityAttributeModifier.Operation.ADDITION)
    val BLESSED: StatusEffect = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"03c3f40c-ce8e-11ed-afa1-0242ac120002",1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"03c3f7ae-ce8e-11ed-afa1-0242ac120002",-0.05,EntityAttributeModifier.Operation.ADDITION)
    val INSIGHTFUL: StatusEffect = CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xC8FF8F)
        .addAttributeModifier(RegisterAttribute.PLAYER_EXPERIENCE,"063b1430-d641-11ed-afa1-0242ac120002",0.25,EntityAttributeModifier.Operation.ADDITION)

    fun registerAll(){
        Registry.register(Registries.STATUS_EFFECT, AI.identity("custom_absorption"), SHIELDING)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("draconic_vision"), DRACONIC_VISION)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("spectral_vision"), SPECTRAL_VISION)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("leapt"), LEAPT)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("inspired"), INSPIRED)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("immunity"), IMMUNITY)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("striding"), STRIDING)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("charmed"), CHARMED)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("stunned"), STUNNED)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("soulbinding"), SOULBINDING)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("resonating"), RESONATING)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("bone_armor"), BONE_ARMOR)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("cursed"), CURSED)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("blessed"), BLESSED)
        Registry.register(Registries.STATUS_EFFECT, AI.identity("insightful"), INSIGHTFUL)
    }
}
