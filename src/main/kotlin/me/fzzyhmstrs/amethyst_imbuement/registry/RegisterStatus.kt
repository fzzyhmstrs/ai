package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.effects.CharmedStatusEffect
import me.fzzyhmstrs.amethyst_imbuement.effects.CustomStatusEffect
import me.fzzyhmstrs.amethyst_imbuement.effects.ShieldingStatusEffect
import me.fzzyhmstrs.amethyst_imbuement.effects.SpectralVisionStatusEffect
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object RegisterStatus {

    fun<T: StatusEffect> register(status: T, name: String): T{
        return Registry.register(Registries.STATUS_EFFECT, Identifier(AI.MOD_ID,name), status)
    }

    val SHIELDING = register(ShieldingStatusEffect(StatusEffectCategory.BENEFICIAL,0x2552A5),"shielding")
    val DRACONIC_VISION = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xA606E5),"draconic_vision")
    val SPECTRAL_VISION = register(SpectralVisionStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF),"spectral_vision")
    val LEAPT = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000),"leapt")
    val INSPIRED = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xB277FF),"inspired")
    val IMMUNITY = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x00000000),"immunity")
    val STRIDING = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x48f369),"striding")
    val CHARMED = register(CharmedStatusEffect(StatusEffectCategory.BENEFICIAL,0xFF80FF),"charmed")
    val BLAST_RESISTANT = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xFF0000),"blast_resistant")
    val SOULBINDING = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x4000000),"soulbinding")
    val RESONATING = register(CustomStatusEffect(StatusEffectCategory.HARMFUL,0x39D6E0),"resonating")
    val SOUL_SHIELD: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x60F5FA)
        .addAttributeModifier(RegisterAttribute.MAGIC_RESISTANCE,"c6914e3a-3266-11ee-be56-0242ac120002",0.1,EntityAttributeModifier.Operation.ADDITION)
        ,"soul_shield")
    val BONE_ARMOR: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xC6C6C6)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"0c7600d6-2695-11ee-be56-0242ac120002",3.0,EntityAttributeModifier.Operation.ADDITION)
        ,"bone_armor")
    val CURSED: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0x010101)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"94974394-c38a-11ed-afa1-0242ac120002",-2.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"2c4481ee-c5f1-11ed-afa1-0242ac120002",-1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"94974614-c38a-11ed-afa1-0242ac120002",0.1,EntityAttributeModifier.Operation.ADDITION)
        ,"cursed")
    val BLESSED: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"03c3f40c-ce8e-11ed-afa1-0242ac120002",1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"03c3f7ae-ce8e-11ed-afa1-0242ac120002",-0.05,EntityAttributeModifier.Operation.ADDITION)
        ,"blessed")
    val INSIGHTFUL: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xC8FF8F)
        .addAttributeModifier(RegisterAttribute.PLAYER_EXPERIENCE,"063b1430-d641-11ed-afa1-0242ac120002",0.25,EntityAttributeModifier.Operation.ADDITION)
        ,"insightful")

    fun registerAll(){}
}