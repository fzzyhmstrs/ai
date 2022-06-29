package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import net.minecraft.util.Identifier

object ReiCategoryIds {

    val IMBUING_TABLE_CATEGORY_ID: CategoryIdentifier<ImbuingTableDisplay> = CategoryIdentifier.of(Identifier(AI.MOD_ID, ImbuingRecipe.Type.ID))
    val CRYSTAL_ALTAR_CATEGORY_ID: CategoryIdentifier<CrystalAltarDisplay> = CategoryIdentifier.of(Identifier(AI.MOD_ID, AltarRecipe.Type.ID))

}