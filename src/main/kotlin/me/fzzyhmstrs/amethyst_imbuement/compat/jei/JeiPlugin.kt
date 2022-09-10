package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.IModPlugin
import mezz.jei.api.registration.*
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object JeiPlugin: IModPlugin {

    override fun getPluginUid(): Identifier {
        return Identifier(AI.MOD_ID,"jei_plugin")
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val list = MinecraftClient.getInstance()?.world?.recipeManager?.listAllOfType(ImbuingRecipe.Type)
        if (list != null) {
            registration.addRecipes(JeiImbuingCategory.IMBUING_TYPE,list)
        }
        val list2 = MinecraftClient.getInstance()?.world?.recipeManager?.listAllOfType(AltarRecipe.Type)
        if (list2 != null) {
            registration.addRecipes(JeiAltarCategory.ENHANCING_TYPE,list2)
        }
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(RegisterBlock.IMBUING_TABLE.asItem()),JeiImbuingCategory.IMBUING_TYPE)
        registration.addRecipeCatalyst(ItemStack(RegisterBlock.ALTAR_OF_EXPERIENCE.asItem()),JeiAltarCategory.ENHANCING_TYPE)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        if (!ModCompatHelper.isViewerSuperseded("jei")) {
            registration.addRecipeClickArea(ImbuingTableScreen::class.java, 6, 91, 20, 18)
        }
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(JeiImbuingCategory(registration.jeiHelpers.guiHelper),JeiAltarCategory(registration.jeiHelpers.guiHelper))
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(ImbuingTableScreenHandler::class.java, RegisterHandler.IMBUING_SCREEN_HANDLER, JeiImbuingCategory.IMBUING_TYPE, 0, 13, 13, 36)
        registration.addRecipeTransferHandler(CrystalAltarScreenHandler::class.java, RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER, JeiAltarCategory.ENHANCING_TYPE, 0, 2, 3, 36)
    }

}
