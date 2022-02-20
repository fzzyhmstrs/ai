@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry

object REIPlugin: REIClientPlugin {

    override fun registerCategories(registry: CategoryRegistry) {
        val IMBUING_TABLE_CATEGORY = ImbuingTableCategory()
        val CRYSTAL_ALTAR_CATEGORY = CrystalAltarCategory()

        registry.add(IMBUING_TABLE_CATEGORY)
        registry.addWorkstations(IMBUING_TABLE_CATEGORY.categoryIdentifier,IMBUING_TABLE_CATEGORY.getIconEntryStack())

        registry.add(CRYSTAL_ALTAR_CATEGORY)
        registry.addWorkstations(CRYSTAL_ALTAR_CATEGORY.categoryIdentifier,CRYSTAL_ALTAR_CATEGORY.getIconEntryStack())
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        registry.registerFiller(ImbuingRecipe::class.java) {recipe -> ImbuingTableDisplay(recipe)}
        registry.registerFiller(AltarRecipe::class.java) { recipe -> CrystalAltarDisplay(recipe)}
    }

}