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
        .knockbackResistance(0.1f)
        .toughness(1.0f)
        .build()
}
