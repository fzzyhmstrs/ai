package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient


object LethalityToolMaterial: ScepterToolMaterial() {
    override fun getDurability(): Int {
        return AiConfig.items.scepters.lethalityDurability.get()
    }
    fun defaultDurability(): Int{
        return 1325
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 1.0f
    }
    override fun getAttackDamage(): Float {
        return AiConfig.items.scepters.lethalityDamage.get()
    }
    override fun getAttackSpeed(): Double {
        return -2.7
    }
    fun defaultAttackDamage(): Float {
        return 4.0f
    }
    override fun getMiningLevel(): Int {
        return 1
    }
    override fun getEnchantability(): Int {
        return 35
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(Items.GOLD_INGOT)
    }
    override fun healCooldown(): Long {
        return AiConfig.items.scepters.lethalityCooldown.get()
    }
    override fun baseCooldown(): Long {
        return 90L
    }
    override fun scepterTier(): Int{
        return 3
    }
}
