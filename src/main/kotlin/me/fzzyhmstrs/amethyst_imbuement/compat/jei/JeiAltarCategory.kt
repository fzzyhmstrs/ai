package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import mezz.jei.api.recipe.RecipeIngredientRole
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class JeiAltarCategory(private val guiHelper: IGuiHelper): IRecipeCategory<AltarRecipe> {

    companion object{
        val ENCHANCING_UID = Identifier(AI.MOD_ID,"enhancing")
        val ENHANCING_CLASS = AltarRecipe::class.java
        val ENHANCING_TYPE = RecipeType(ENCHANCING_UID, ENHANCING_CLASS)
    }
    
    private val background = guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/jei_background.png"),0,62,125,18)

    override fun getRecipeType(): RecipeType<AltarRecipe> {
        return RecipeType(Identifier(AI.MOD_ID,"enhancing"),AltarRecipe::class.java)
    }

    override fun getTitle(): Text {
        return TranslatableText("recipe.enhancing")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun getIcon(): IDrawable {
        return  guiHelper.createDrawableItemStack(ItemStack(RegisterBlock.CRYSTAL_ALTAR.asItem()))
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: AltarRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT,1,1).addIngredients(recipe.base)
        builder.addSlot(RecipeIngredientRole.INPUT,50,1).addIngredients(recipe.addition)
        builder.addSlot(RecipeIngredientRole.OUTPUT,108,1).addItemStack(recipe.result)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "ENCHANCING_UID",
        "me.fzzyhmstrs.amethyst_imbuement.compat.jei.JeiAltarCategory.Companion.ENCHANCING_UID"
    )
    )
    override fun getUid(): Identifier {
        return ENCHANCING_UID
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "ENHANCING_CLASS",
        "me.fzzyhmstrs.amethyst_imbuement.compat.jei.JeiAltarCategory.Companion.ENHANCING_CLASS"
    )
    )
    override fun getRecipeClass(): Class<out AltarRecipe> {
        return ENHANCING_CLASS
    }
}
