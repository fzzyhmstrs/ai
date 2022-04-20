package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import kotlin.math.max


object ScepterLvl2ToolMaterial: ToolMaterial,ScepterMaterialAddon{
    override fun getDurability(): Int {
        return AiConfig.scepters.iridescentDurability
    }
    fun defaultDurability(): Int{
        return 550
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
        return Ingredient.ofItems(Items.GOLD_INGOT)
    }
    override fun healCooldown(): Long {
        return max(AiConfig.scepters.baseRegenRateTicks - 25L,minCooldown())
    }
}