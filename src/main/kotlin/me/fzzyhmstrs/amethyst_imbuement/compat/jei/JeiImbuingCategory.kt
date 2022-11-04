package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class JeiImbuingCategory(private val guiHelper: IGuiHelper): IRecipeCategory<ImbuingRecipe> {

    companion object{
        val IMBUING_TYPE = RecipeType(Identifier(AI.MOD_ID,"imbuing"),ImbuingRecipe::class.java)
    }

    private val icon = guiHelper.createDrawableItemStack(ItemStack(RegisterBlock.IMBUING_TABLE.asItem()))
    private val background = guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/jei_background.png"),0,0,135,62)

    override fun getRecipeType(): RecipeType<ImbuingRecipe> {
        return IMBUING_TYPE
    }

    override fun getTitle(): Text {
        return AcText.translatable("recipe.imbuing")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun getIcon(): IDrawable {
        return  icon
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
            costText = AcText.translatable("display.imbuing.cost.big",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 113 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        } else{
            costText = AcText.translatable("display.imbuing.cost.small",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 116 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        }

        client.textRenderer.drawWithShadow(stack,costText,costOffset.toFloat(),44.0f,Formatting.GREEN.colorIndex)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ImbuingRecipe, focuses: IFocusGroup) {
        val slots = recipe.getInputs()
        val centerSlot: Ingredient = recipe.getCenterIngredient()
        val resultSlot: ItemStack = recipe.output
        
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(slots[0])
        builder.addSlot(RecipeIngredientRole.INPUT, 81, 1).addIngredients(slots[1])
        
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 3).addIngredients(slots[2])
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 3).addIngredients(slots[3])
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 3).addIngredients(slots[4])
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 23).addIngredients(slots[5])
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 23).addIngredients(centerSlot)
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 23).addIngredients(slots[7])
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 43).addIngredients(slots[8])
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 43).addIngredients(slots[9])
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 43).addIngredients(slots[10])
        
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 45).addIngredients(slots[11])
        builder.addSlot(RecipeIngredientRole.INPUT, 81, 45).addIngredients(slots[12])
        
        builder.addSlot(RecipeIngredientRole.OUTPUT,117, 23).addItemStack(resultSlot)
    }
}
