package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient


object ScepterOfHarvestsScepterToolMaterial: ScepterToolMaterial(){
    override fun getDurability(): Int {
        return AiConfig.items.scepters.harvestDurability.get()
    }
    fun defaultDurability(): Int{
        return 750
    }
    override fun getMiningSpeedMultiplier(): Float {
        return AiConfig.items.scepters.harvestMiningSpeed.get()
    }
    fun defaultMiningSpeed(): Float {
        return 4.0f
    }
    override fun getAttackDamage(): Float {
        return 0.0f
    }
    override fun getAttackSpeed(): Double {
        return -3.0
    }
    override fun getMiningLevel(): Int {
        return 4
    }
    override fun getEnchantability(): Int {
        return 15
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(Items.GOLD_INGOT)
    }
    override fun healCooldown(): Long {
        return AiConfig.items.scepters.harvestCooldown.get()
    }
    override fun baseCooldown(): Long {
        return 110L
    }
    override fun scepterTier(): Int{
        return 2
    }
}
