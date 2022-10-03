package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import java.util.*

class JeiImbuingTransferHandler: IRecipeTransferHandler<ImbuingTableScreenHandler,ImbuingRecipe> {
    override fun getContainerClass(): Class<out ImbuingTableScreenHandler> {
        return ImbuingTableScreenHandler::class.java
    }

    override fun getMenuType(): Optional<ScreenHandlerType<ImbuingTableScreenHandler>> {
        return Optional.ofNullable(RegisterHandler.IMBUING_SCREEN_HANDLER)
    }

    override fun getRecipeType(): RecipeType<ImbuingRecipe> {
        return JeiImbuingCategory.IMBUING_TYPE
    }

    override fun transferRecipe(
        container: ImbuingTableScreenHandler,
        recipe: ImbuingRecipe,
        recipeSlots: IRecipeSlotsView,
        player: PlayerEntity,
        maxTransfer: Boolean,
        doTransfer: Boolean
    ): IRecipeTransferError? {
        return null
    }
}
