package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class ImbuingEmiRecipe(recipe: ImbuingRecipe): EmiRecipe{
    
    private val id: Identifier
    private val inputs: List<EmiIngredient>
    private val outputs: List<EmiStack>
    private val costText: OrderedText
    private val costOffset: Int
    private val isEnchantingType: Boolean

    init{
        id = recipe.id
        inputs = initInputs(recipe)
        outputs = initOutputs(recipe)
        val cost = recipe.getCost()
        if(cost > 99){
            costText = AcText.translatable("display.imbuing.cost.big",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 116 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        } else{
            costText = AcText.translatable("display.imbuing.cost.small",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 119 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        }
        isEnchantingType = recipe.getAugment() != ""
    
    }
    
    private fun initInputs(recipe: ImbuingRecipe): List<EmiIngredient>{
        val list: MutableList<EmiIngredient> = mutableListOf()
        val inputs = recipe.getInputs()
        for (i in inputs.indices){
            val input = inputs[i]
            if (recipe.getAugment() != "" && i == 6){
                val ingredient = recipe.getCenterIngredient()
                list.add(EmiIngredient.of(ingredient))
                continue
            }
            val stacks = input.matchingStacks
            val bookIndex = getBookStackIndex(stacks)
            if (bookIndex >= 0){
                val stack = stacks[bookIndex].copy()
                AbstractAugmentBookItem.addLoreKeyForREI(stack,recipe.getAugment())
                list.add(EmiStack.of(stack))
                continue
            }
            list.add(EmiIngredient.of(input))
        }
        return list
    }

    private fun getBookStackIndex(stacks: Array<ItemStack>?): Int{
        if (stacks.isNullOrEmpty()) return -1
        stacks.forEachIndexed { index, itemStack ->
            val item = itemStack.item
            if (item is AbstractAugmentBookItem) return index
        }
        return -1
    }

    private fun initOutputs(recipe: ImbuingRecipe): List<EmiStack>{
        return listOf(EmiStack.of(recipe.output))
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.IMBUING_CATEGORY
    }
    
    override fun getId(): Identifier{
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return inputs
    }
    
    override fun getOutputs(): List<EmiStack>{
        return outputs
    }
    
    override fun supportsRecipeTree(): Boolean{
        return super.supportsRecipeTree() && !isEnchantingType
    }
    
    override fun getDisplayWidth(): Int{
        return 134
    }
    
    override fun getDisplayHeight(): Int{
        return 60
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 82, 21)
        widgets.addSlot(inputs[0], 0, 0)
        widgets.addSlot(inputs[1], 81, 0)
        widgets.addSlot(inputs[2], 20, 2)
        widgets.addSlot(inputs[3], 40, 2)
        widgets.addSlot(inputs[4], 60, 2)
        widgets.addSlot(inputs[5], 20, 21)
        widgets.addSlot(inputs[6], 40, 21)
        widgets.addSlot(inputs[7], 60, 21)
        widgets.addSlot(inputs[8], 20, 40)
        widgets.addSlot(inputs[9], 40, 40)
        widgets.addSlot(inputs[10], 60, 40)
        widgets.addSlot(inputs[11], 0, 42)
        widgets.addSlot(inputs[12], 81, 42)
        widgets.addSlot(outputs[0], 111, 21).recipeContext(this)
        widgets.addText(costText,costOffset,42,0x55FF55,true)
    }

}
