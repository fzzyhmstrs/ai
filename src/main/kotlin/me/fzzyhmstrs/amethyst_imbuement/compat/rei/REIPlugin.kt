@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry

object REIPlugin: REIClientPlugin {

    override fun registerCategories(registry: CategoryRegistry) {
        val IMBUING_TABLE_CATEGORY = ImbuingTableCategory()

        registry.add(IMBUING_TABLE_CATEGORY)
        registry.addWorkstations(IMBUING_TABLE_CATEGORY.categoryIdentifier,IMBUING_TABLE_CATEGORY.getIconEntryStack())
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        /*registry.registerRecipeFiller(ImbuingRecipe::class.java,ImbuingRecipe.Type) { recipe ->
            ImbuingTableDisplay(
                recipe
            )
        }*/
        registry.registerFiller(ImbuingRecipe::class.java) {recipe -> ImbuingTableDisplay(recipe)}
    }

}