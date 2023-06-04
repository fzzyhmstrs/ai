package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient


object FzzyhammerToolMaterial: ScepterToolMaterial(){
    override fun getDurability(): Int {
        return AiConfig.items.scepters.fzzyDurability.get()
    }
    fun defaultDurability(): Int{
        return 1250
    }
    override fun getMiningSpeedMultiplier(): Float {
        return AiConfig.items.scepters.fzzyMiningSpeed.get()
    }
    fun defaultMiningSpeed(): Float {
        return 6.0f
    }
    override fun getAttackDamage(): Float {
        return AiConfig.items.scepters.fzzyDamage.get()
    }
    fun defaultAttackDamage(): Float {
        return 9.5f
    }
    override fun getAttackSpeed(): Double {
        return -3.2
    }
    override fun getMiningLevel(): Int {
        return 3
    }
    override fun getEnchantability(): Int {
        return 15
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(Items.GOLD_INGOT)
    }
    override fun healCooldown(): Long {
        return AiConfig.items.scepters.fzzyCooldown.get()
    }
    override fun baseCooldown(): Long {
        return 90L
    }
    override fun scepterTier(): Int{
        return 3
    }
}
