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

    companion object{
        val IMBUING_TYPE = RecipeType(Identifier(AI.MOD_ID,"imbuing"),ImbuingRecipe::class.java)
    }

    private val background = guiHelper.createDrawable(Identifier(AI.MOD_ID,"textures/gui/imbuing_background.png"),5,5,142,68)

    override fun getRecipeType(): RecipeType<ImbuingRecipe> {
        return IMBUING_TYPE
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
        val slots = recipe.getInputs()
        val centerSlot: Indgredient
        val resultSlot: ItemStack
        val identifier = Identifier(recipe.getAugment())
        val enchant = Registry.ENCHANTMENT.get(identifier)
        val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
        if (enchant != null){
            when (enchant) {
                is BaseAugment -> {
                    val stacks = enchant.acceptableItemStacks().toTypedArray()
                    centerSlot = Ingredient.ofStacks(*stacks)
                }
                is ScepterAugment -> {
                    val stacks = enchant.acceptableItemStacks().toTypedArray()
                    for (chk in stacks.indices){
                        val item = stacks[chk].item
                        if (item is AbstractAugmentBookItem) {
                            val augBookStack = stacks[chk].copy()
                            AbstractAugmentBookItem.addLoreKeyForREI(augBookStack, recipe.getAugment())
                            stacks[chk] = augBookStack
                        }
                    }
                    centerSlot = Ingredient.ofStacks(*stacks)
                }
                else -> {
                    val enchantItemList: MutableList<ItemStack> = mutableListOf()
                    for (item in Registry.ITEM.iterator()){
                        val stack = ItemStack(item)
                        if (enchant.isAcceptableItem(stack)){
                            enchantItemList.add(stack)
                        }
                    }
                    centerSlot = Ingredient.ofStacks(*enchantItemList.toTypedArray())
                }
            }
            resultSlot = ItemStack(Items.ENCHANTED_BOOK,1)
            EnchantedBookItem.addEnchantment(resultSlot,EnchantmentLevelEntry(enchant,1))
        } else if(modifier != null) {
            val stacks = modifier.acceptableItemStacks().toTypedArray()
            centerSlot = Ingredient.ofStacks(*stacks)
            val moddedStack = modifier.acceptableItemStacks().first().copy()
            ModifierHelper.addModifierForREI(modifier.modifierId, moddedStack)
            resultSlot = moddedStack
        } else {
            centerSlot = slots[6]
            resultSlot = if (recipe.getAugment() == ""){recipe.getOutput()} else {ItemStack(Items.BOOK, 1)}
        }
        
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(slots[0])
        builder.addSlot(RecipeIngredientRole.INPUT, 88, 1).addIngredients(slots[1])
        
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 3).addIngredients(slots[2])
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 3).addIngredients(slots[3])
        builder.addSlot(RecipeIngredientRole.INPUT, 67, 3).addIngredients(slots[4])
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 26).addIngredients(slots[5])
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 26).addIngredients(centerSlot)
        builder.addSlot(RecipeIngredientRole.INPUT, 67, 26).addIngredients(slots[7])
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 49).addIngredients(slots[8])
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 49).addIngredients(slots[9])
        builder.addSlot(RecipeIngredientRole.INPUT, 67, 49).addIngredients(slots[10])
        
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 51).addIngredients(slots[11])
        builder.addSlot(RecipeIngredientRole.INPUT, 88, 51).addIngredients(slots[12])
        
        builder.addSlot(RecipeIngredientRole.OUTPUT, , 26).addItemStack(resultSlot)
    }
}
