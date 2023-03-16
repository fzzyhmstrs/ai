package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.recipe.Ingredient
import kotlin.math.max


object LethalityToolMaterial: ScepterToolMaterial() {
    override fun getDurability(): Int {
        return AiConfig.items.lethalityDurability
    }
    fun defaultDurability(): Int{
        return 1250
    }
    override fun getMiningSpeedMultiplier(): Float {
        return 1.0f
    }
    override fun getAttackDamage(): Float {
        return AiConfig.items.lethalityDamage
    }
    override fun getAttackSpeed(): Double {
        return -3.0
    }
    fun defaultAttackDamage(): Float {
        return 8.0f
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
        return max(AiConfig.items.baseRegenRateTicks - 60L,minCooldown())
    }
    override fun baseCooldown(): Long {
        return 90L
    }
    override fun scepterTier(): Int{
        return 3
    }
}
