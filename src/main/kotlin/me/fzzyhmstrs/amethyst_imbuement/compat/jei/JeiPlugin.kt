package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import mezz.jei.api.IModPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeTransferRegistration
import net.minecraft.util.Identifier

object JeiPlugin: IModPlugin {

    override fun getPluginUid(): Identifier {
        return Identifier(AI.MOD_ID,"jei_plugin")
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addRecipeClickArea(ImbuingTableScreen::class.java,6,91,20,18)
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(JeiImbuingCategory(registration.jeiHelpers.guiHelper),JeiAltarCategory(registration.jeiHelpers.guiHelper))
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(JeiImbuingTranserHandler(),JeiImbuingCategory.IMBUING_TYPE)
    }

}