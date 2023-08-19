package me.fzzyhmstrs.amethyst_imbuement.material

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvents

object AiArmorMaterialsConfig{
    val AMETRINE = ValidatedArmorMaterial.Builder("ai_ametrine",SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND)
        .repairIngredient(Ingredient.ofItems(RegisterItem.AMETRINE))
        .enchantability(18)
        .protectionAmounts(4,9,7,4)
        .durabilityMultiplier(39)
        .knockbackResistance(0.1f)
        .toughness(3.2f)
        .build()
    val STEEL = ValidatedArmorMaterial.Builder("ai_steel",SoundEvents.ITEM_ARMOR_EQUIP_IRON)
        .repairIngredient(Ingredient.ofItems(RegisterItem.STEEL_INGOT))
        .enchantability(10)
        .protectionAmounts(2,7,6,2)
        .durabilityMultiplier(20)
        .toughness(1.0f)
        .build()
    val GARNET = ValidatedArmorMaterial.Builder("ai_garnet",SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND)
        .repairIngredient(Ingredient.ofItems(RegisterItem.GARNET))
        .enchantability(12)
        .protectionAmounts(3,8,6,3)
        .durabilityMultiplier(55,200)
        .knockbackResistance(0.05f)
        .toughness(2.0f)
        .build()
    val GLOWING = ValidatedArmorMaterial.Builder("ai_glowing",SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND)
        .repairIngredient(Ingredient.ofItems(RegisterItem.GLOWING_FRAGMENT))
        .enchantability(35)
        .protectionAmounts(3,9,7,3)
        .durabilityMultiplier(14)
        .knockbackResistance(0.05f)
        .toughness(3f)
        .build()
    val SHIMMERING = ValidatedArmorMaterial.Builder("ai_shimmering",SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
        .repairIngredient(Ingredient.ofItems(RegisterItem.GLOWING_FRAGMENT))
        .enchantability(25)
        .protectionAmounts(2,6,5,2)
        .durabilityMultiplier(12)
        .build()
}
