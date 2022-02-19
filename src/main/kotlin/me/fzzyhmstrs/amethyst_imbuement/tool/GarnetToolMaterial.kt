package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient




object GarnetToolMaterial: ToolMaterial{
    override fun getDurability(): Int {
        return 3151
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 7.5f
    }
    override fun getAttackDamage(): Float {
        return 3.0f
    }
    override fun getMiningLevel(): Int {
        return 3
    }
    override fun getEnchantability(): Int {
        return 12
    }

    override fun getRepairIngredient(): Ingredient? {
        return Ingredient.ofItems(RegisterItem.GARNET)
    }
}