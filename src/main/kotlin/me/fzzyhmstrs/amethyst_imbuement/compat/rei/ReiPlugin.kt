@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.api.client.registry.screen.SimpleClickArea
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.data.server.ItemTagProvider
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import java.util.function.Supplier

object ReiPlugin: REIClientPlugin {

    private val IMBUING_TABLE_CATEGORY = ImbuingTableCategory()
    private val CRYSTAL_ALTAR_CATEGORY = CrystalAltarCategory()

    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(IMBUING_TABLE_CATEGORY)
        registry.addWorkstations(IMBUING_TABLE_CATEGORY.categoryIdentifier,IMBUING_TABLE_CATEGORY.getIconEntryStack())

        registry.add(CRYSTAL_ALTAR_CATEGORY)
        registry.addWorkstations(CRYSTAL_ALTAR_CATEGORY.categoryIdentifier,CRYSTAL_ALTAR_CATEGORY.getIconEntryStack())
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        registry.registerRecipeFiller(ImbuingRecipe::class.java,ImbuingRecipe.Type) { recipe -> ImbuingTableDisplay(recipe)}
        registry.registerRecipeFiller(AltarRecipe::class.java,AltarRecipe.Type) { recipe -> CrystalAltarDisplay(recipe)}
    }

    override fun registerScreens(registry: ScreenRegistry) {
        val rectangle = Rectangle(6,91,20,18)
        registry.registerContainerClickArea(rectangle,ImbuingTableScreen::class.java, IMBUING_TABLE_CATEGORY.categoryIdentifier)
    }
}