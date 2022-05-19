package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import kotlin.math.max

object ScepterLvl1ToolMaterial: ToolMaterial,ScepterMaterialAddon{
    override fun getDurability(): Int {
        return AiConfig.scepters.opalineDurability
    }
    fun defaultDurability(): Int{
        return 250
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
        return Ingredient.ofItems(RegisterItem.BERYL_COPPER_INGOT)
    }
    override fun healCooldown(): Long {
        return max(AiConfig.scepters.baseRegenRateTicks,minCooldown())
    }
}