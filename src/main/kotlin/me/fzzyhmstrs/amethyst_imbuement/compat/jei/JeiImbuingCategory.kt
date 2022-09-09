package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class JeiImbuingCategory(private val guiHelper: IGuiHelper): IRecipeCategory<ImbuingRecipe> {

    private val background = guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/imbuing_background.png"),5,5,142,68)

    override fun getRecipeType(): RecipeType<ImbuingRecipe> {
        return RecipeType(Identifier(AI.MOD_ID,"imbuing"),ImbuingRecipe::class.java)
    }

    override fun getTitle(): Text {
        return Text.translatable("recipe.imbuing")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun getIcon(): IDrawable {
        return  guiHelper.createDrawableItemStack(ItemStack(RegisterBlock.IMBUING_TABLE.asItem()))
    }

    override fun draw(
        recipe: ImbuingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        stack: MatrixStack,
        mouseX: Double,
        mouseY: Double
    ) {
        val cost = recipe.getCost()
        val client = MinecraftClient.getInstance()
        val costText: OrderedText
        val costOffset: Int
        if(cost > 99){
            costText = Text.translatable("display.imbuing.cost.big",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 116 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        } else{
            costText = Text.translatable("display.imbuing.cost.small",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 119 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        }

        client.textRenderer.drawWithShadow(stack,costText,costOffset.toFloat(),45.0f,Formatting.GREEN.colorIndex)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ImbuingRecipe, focuses: IFocusGroup) {

        TODO("Not yet implemented")
    }
}