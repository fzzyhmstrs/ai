package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient


object ScepterSoFoulToolMaterial: ScepterToolMaterial() {
    override fun getDurability(): Int {
        return AiConfig.items.scepters.fowlDurability.get()
    }
    fun defaultDurability(): Int{
        return 600
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 1.0f
    }
    override fun getAttackDamage(): Float {
        return 0.0f
    }
    override fun getAttackSpeed(): Double {
        return -3.0
    }
    override fun getMiningLevel(): Int {
        return 1
    }
    override fun getEnchantability(): Int {
        return 25
    }
    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(Items.COOKED_CHICKEN)
    }
    override fun healCooldown(): Long {
        return AiConfig.items.scepters.fowlCooldown.get()
    }
    override fun baseCooldown(): Long {
        return 120L
    }
    override fun scepterTier(): Int{
        return 2
    }
}
