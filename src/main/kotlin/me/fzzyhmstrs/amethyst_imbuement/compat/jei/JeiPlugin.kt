package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.IModPlugin
import mezz.jei.api.gui.handlers.IGuiClickableArea
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import mezz.jei.api.recipe.IFocusFactory
import mezz.jei.api.registration.*
import mezz.jei.api.runtime.IRecipesGui
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.Rect2i
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
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
        registration.addRecipeCatalyst(ItemStack(RegisterBlock.CRYSTAL_ALTAR.asItem()),JeiAltarCategory.ENHANCING_TYPE)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        if (!ModCompatHelper.isViewerSuperseded("jei")) {
            //registration.addRecipeClickArea(ImbuingTableScreen::class.java, 6, 91, 20, 18, JeiImbuingCategory.IMBUING_TYPE)
            registration.addGuiContainerHandler(ImbuingTableScreen::class.java, guiClickerHandler)
        }
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(JeiImbuingCategory(registration.jeiHelpers.guiHelper),JeiAltarCategory(registration.jeiHelpers.guiHelper))
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(ImbuingTableScreenHandler::class.java, JeiImbuingCategory.IMBUING_TYPE, 0, 13, 13, 36)
        registration.addRecipeTransferHandler(CrystalAltarScreenHandler::class.java, JeiAltarCategory.ENHANCING_TYPE, 0, 2, 3, 36)
    }

    private val guiClicker = object: IGuiClickableArea{
        var rect = Rect2i(6, 91, 20, 18)
        override fun getArea(): Rect2i {
            return rect
        }

        override fun onClick(focusFactory: IFocusFactory, recipesGui: IRecipesGui) {
            val player = MinecraftClient.getInstance().player
            if (player != null) {
                MinecraftClient.getInstance().world?.playSound(
                    player, player.blockPos,
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundCategory.BLOCKS,
                    0.5f,
                    1.2f
                )
            }
            recipesGui.showTypes(listOf(JeiImbuingCategory.IMBUING_TYPE))
        }

    }

    private val guiClickerHandler = object: IGuiContainerHandler < ImbuingTableScreen > {
        override fun getGuiClickableAreas(
            containerScreen: ImbuingTableScreen,
            mouseX: Double,
            mouseY: Double
        ): Collection<IGuiClickableArea> {
            val clickableArea = guiClicker
            return listOf(clickableArea)
        }
    }
}
