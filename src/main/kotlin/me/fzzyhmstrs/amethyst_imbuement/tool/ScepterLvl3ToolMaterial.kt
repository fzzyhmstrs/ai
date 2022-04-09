package me.fzzyhmstrs.amethyst_imbuement.tool

import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient




object ScepterLvl3ToolMaterial: ToolMaterial,ScepterMaterialAddon{
    override fun getDurability(): Int {
        return 2876
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 1.0f
    }
    override fun getAttackDamage(): Float {
        return 2.0f
    }
    override fun getMiningLevel(): Int {
        return 1
    }
    override fun getEnchantability(): Int {
        return 25
    }
    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(Items.NETHERITE_INGOT)
    }
    override fun healCooldown(): Long {
        return 80L
    }
}