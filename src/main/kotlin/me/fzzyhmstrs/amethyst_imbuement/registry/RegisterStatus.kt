package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.event.AfterSpellEvent
import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.effects.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.world.World

object RegisterStatus {

    fun<T: StatusEffect> register(status: T, name: String): T{
        return Registry.register(Registries.STATUS_EFFECT, Identifier(AI.MOD_ID,name), status)
    }

    //basic effects
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
    val STUNNED = register(CustomStatusEffect(StatusEffectCategory.HARMFUL,0xFFFF00),"stunned")

    //attribute effects
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
    val SANCTUARY: StatusEffect = register(CustomStatusEffect(StatusEffectCategory.BENEFICIAL,0xFEFBEA)
        .addAttributeModifier(RegisterAttribute.DAMAGE_MULTIPLICATION,"26613cdc-5d35-11ee-8c99-0242ac120002",-1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,"266141a0-5d35-11ee-8c99-0242ac120002",1.0, EntityAttributeModifier.Operation.ADDITION)
        ,"sanctuary")

    //auras
    val LIGHTNING_AURA: StatusEffect = register(LightningAuraStatusEffect(StatusEffectCategory.BENEFICIAL,0x00A1FF)
        .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,"0a3a25d6-5ccd-11ee-8c99-0242ac120002", 0.075,EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .addAttributeModifier(RegisterAttribute.SPELL_COOLDOWN,"0a3a2842-5ccd-11ee-8c99-0242ac120002", 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        ,"lightning_aura")
    val ARCANE_AURA: StatusEffect = register(ArcaneAuraStatusEffect(StatusEffectCategory.BENEFICIAL,0xDDA0DD)
        .addAttributeModifier(RegisterAttribute.SPELL_AMPLIFIER,"26613746-5d35-11ee-8c99-0242ac120002", 1.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(RegisterAttribute.SPELL_DAMAGE,"2661398a-5d35-11ee-8c99-0242ac120002", 0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        ,"arcane_aura")
    val MENDING_AURA: StatusEffect = register(MendingAuraStatusEffect(StatusEffectCategory.BENEFICIAL,0xFFFFFF)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,"26613ab6-5d35-11ee-8c99-0242ac120002",2.0,EntityAttributeModifier.Operation.ADDITION)
        .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"26613bd8-5d35-11ee-8c99-0242ac120002",1.0,EntityAttributeModifier.Operation.ADDITION)
        ,"mending_aura")

    fun registerAll(){
        AfterSpellEvent.EVENT.register{ _: World, user: LivingEntity, _: ItemStack, spell: ScepterAugment ->
            if (user.hasStatusEffect(SANCTUARY) && AugmentHelper.getAugmentType(spell) == SpellType.FURY){
                user.removeStatusEffect(SANCTUARY)
                user.removeStatusEffect(IMMUNITY)
            }
        }
    }
}
