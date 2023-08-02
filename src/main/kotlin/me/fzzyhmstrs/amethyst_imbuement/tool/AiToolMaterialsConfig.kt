package me.fzzyhmstrs.amethyst_imbuement.tool

import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient

object AiToolMaterialsConfig{
    val BUILDERS_SCEPTER = ScepterToolMaterial.Builder(2).attackSpeed(-3.0).durability(650,1650).miningSpeedMultiplier(5f).attackDamage(2f).miningLevel(1).enchantability(25).repairIngredient(Ingredient.ofItems(Items.GOLD_INGOT)).build()
}
