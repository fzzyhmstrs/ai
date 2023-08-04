package me.fzzyhmstrs.amethyst_imbuement.material

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedToolMaterial
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.recipe.Ingredient

object AiToolMaterialsConfig{
    val GARNET = ValidatedToolMaterial.Builder()
        .durability(3151)
        .miningSpeedMultiplier(7.5f)
        .attackDamage(3f)
        .miningLevel(3)
        .enchantability(12)
        .repairIngredient(Ingredient.ofItems(RegisterItem.GARNET))
        .build()
    val GLOWING = ValidatedToolMaterial.Builder()
        .durability(333)
        .miningSpeedMultiplier(11.5f)
        .attackDamage(4.5f)
        .miningLevel(MiningLevels.NETHERITE)
        .enchantability(12)
        .repairIngredient(Ingredient.ofItems(RegisterItem.GARNET))
        .build()
    val STEEL = ValidatedToolMaterial.Builder()
        .durability(400)
        .miningSpeedMultiplier(6.5f)
        .attackDamage(2.5f)
        .miningLevel(2)
        .enchantability(15)
        .repairIngredient(Ingredient.ofItems(RegisterItem.STEEL_INGOT))
        .build()
    val AMETRINE = ValidatedToolMaterial.Builder()
        .durability(2550)
        .miningSpeedMultiplier(9.5f)
        .attackDamage(5f)
        .miningLevel(MiningLevels.NETHERITE)
        .enchantability(18)
        .repairIngredient(Ingredient.ofItems(RegisterItem.AMETRINE))
        .build()
}
