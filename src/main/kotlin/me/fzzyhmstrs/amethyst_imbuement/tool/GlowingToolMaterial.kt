package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient




object GlowingToolMaterial: ToolMaterial{
    override fun getDurability(): Int {
        return 185
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 7.5f
    }
    override fun getAttackDamage(): Float {
        return 3.5f
    }
    override fun getMiningLevel(): Int {
        return 1
    }
    override fun getEnchantability(): Int {
        return 35
    }

    override fun getRepairIngredient(): Ingredient? {
        return Ingredient.ofItems(RegisterItem.GLOWING_FRAGMENT)
    }
}