package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class JeiAltarCategory(guiHelper: IGuiHelper): IRecipeCategory<AltarRecipe> {

    companion object{
        val ENHANCING_TYPE = RecipeType(Identifier(AI.MOD_ID,"enhancing"),AltarRecipe::class.java)
    }

    private val icon = guiHelper.createDrawableItemStack(ItemStack(RegisterBlock.CRYSTAL_ALTAR.asItem()))
    private val background = guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/jei_background.png"),0,62,112,18)

    override fun getRecipeType(): RecipeType<AltarRecipe> {
        return RecipeType(Identifier(AI.MOD_ID,"enhancing"),AltarRecipe::class.java)
    }

    override fun getTitle(): Text {
        return AcText.translatable("recipe.enhancing")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun getIcon(): IDrawable {
        return icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: AltarRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT,1,1).addIngredients(recipe.dust)
        builder.addSlot(RecipeIngredientRole.INPUT,19,1).addIngredients(recipe.base)
        builder.addSlot(RecipeIngredientRole.INPUT,37,1).addIngredients(recipe.addition)
        builder.addSlot(RecipeIngredientRole.OUTPUT,95,1).addItemStack(recipe.result)
    }
}
