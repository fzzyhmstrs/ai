package me.fzzyhmstrs.amethyst_imbuement.material

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.minecraft.recipe.Ingredient

class CompatScepterToolMaterial(
    private val attackDamage: Float,
    private val attackSpeed: Double,
    private val durability: Int,
    private val enchantability: Int,
    private val miningLevel: Int,
    private val miningSpeedMultiplier: Float,
    private val repairIngredient: Ingredient,
    private val healCooldown: Long,
    private val scepterTier: Int)
    :
    ScepterToolMaterial()
{
    override fun getAttackDamage(): Float {
        return attackDamage
    }

    override fun getAttackSpeed(): Double {
        return attackSpeed
    }

    override fun getDurability(): Int {
        return durability
    }

    override fun getEnchantability(): Int {
        return enchantability
    }

    override fun getMiningLevel(): Int {
        return miningLevel
    }

    override fun getMiningSpeedMultiplier(): Float {
        return miningSpeedMultiplier
    }

    override fun getRepairIngredient(): Ingredient {
        return repairIngredient
    }

    override fun healCooldown(): Long {
        return healCooldown
    }

    override fun scepterTier(): Int {
        return scepterTier
    }

}