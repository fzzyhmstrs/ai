package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class JeiImbuingCategory(private val guiHelper: IGuiHelper): IRecipeCategory<ImbuingRecipe> {

    override fun getRecipeType(): RecipeType<ImbuingRecipe> {
        return RecipeType(Identifier(AI.MOD_ID,"imbuing"),ImbuingRecipe::class.java)
    }

    override fun getTitle(): Text {
        return Text.translatable("recipe.imbuing")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/imbuing_background.png"),5,5,142,68)
    }

    override fun getIcon(): IDrawable {
        return  guiHelper.createDrawableItemStack(ItemStack(RegisterBlock.IMBUING_TABLE.asItem()))
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ImbuingRecipe, focuses: IFocusGroup) {

        TODO("Not yet implemented")
    }
}