package me.fzzyhmstrs.amethyst_imbuement.compat.jei

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
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
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class JeiImbuingCategory(private val guiHelper: IGuiHelper): IRecipeCategory<ImbuingRecipe> {

    companion object{
        val IMBUING_UID = Identifier(AI.MOD_ID,"imbuing")
        val IMBUING_CLASS = ImbuingRecipe::class.java
        val IMBUING_TYPE = RecipeType(IMBUING_UID, IMBUING_CLASS)
    }

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
        val centerSlot: Ingredient
        val resultSlot: ItemStack = ModCompatHelper.outputGenerator(recipe)
        val ingredient = ModCompatHelper.centerSlotGenerator(recipe)
        centerSlot = if (ingredient.isEmpty){
            slots[6]
        } else {
            ingredient
        }
        
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

    @Deprecated("Deprecated in Java", ReplaceWith(
        "IMBUING_UID",
        "me.fzzyhmstrs.amethyst_imbuement.compat.jei.JeiImbuingCategory.Companion.IMBUING_UID"
    )
    )
    override fun getUid(): Identifier {
        return IMBUING_UID
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "IMBUING_CLASS",
        "me.fzzyhmstrs.amethyst_imbuement.compat.jei.JeiImbuingCategory.Companion.IMBUING_CLASS"
    )
    )
    override fun getRecipeClass(): Class<out ImbuingRecipe> {
        return IMBUING_CLASS
    }
}
