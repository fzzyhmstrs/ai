package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.recipe.Ingredient


object ScepterOfTheVanguardToolMaterial: ScepterToolMaterial() {
    override fun getDurability(): Int {
        return AiConfig.items.scepters.vanguardDurability.get()
    }
    fun defaultDurability(): Int{
        return 650
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 1.0f
    }
    override fun getAttackDamage(): Float {
        return AiConfig.items.scepters.vanguardDamage.get()
    }
    fun defaultAttackDamage(): Float {
        return 2.0f
    }
    override fun getAttackSpeed(): Double {
        return -3.0
    }
    override fun getMiningLevel(): Int {
        return 1
    }
    override fun getEnchantability(): Int {
        return 35
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(RegisterItem.GLOWING_BLADE)
    }
    override fun healCooldown(): Long {
        return AiConfig.items.scepters.vanguardCooldown.get()
    }
    override fun baseCooldown(): Long {
        return 125L
    }
    override fun scepterTier(): Int{
        return 2
    }
}
