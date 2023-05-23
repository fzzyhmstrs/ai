package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

class ImbuingEmiRecipe(recipe: ImbuingRecipe): EmiRecipe{
    
    private val id: Identifier
    private val inputs: List<EmiIngredient>
    private val outputs: List<EmiStack>
    private val reactantChk: EmiIngredient
    private val costText: OrderedText
    private val costOffset: Int
    private val isEnchantingType: Boolean

    init{
        id = recipe.id
        inputs = initInputs(recipe)
        outputs = initOutputs(recipe)
        reactantChk = if(recipe.getAugment() != "") EmiIngredient.of(recipe.getInputs()[6]) else EmiIngredient.of(Ingredient.ofStacks(recipe.output))
        val cost = MathHelper.ceil(recipe.getCost() * AiConfig.blocks.imbuing.difficultyModifier.get())
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
        widgets.add(ReagentAlertSlot(inputs[0],reactantChk, 0, 0))
        widgets.add(ReagentAlertSlot(inputs[1],reactantChk, 81, 0))
        widgets.add(ReagentAlertSlot(inputs[2],reactantChk, 20, 2))
        widgets.add(ReagentAlertSlot(inputs[3],reactantChk, 40, 2))
        widgets.add(ReagentAlertSlot(inputs[4],reactantChk, 60, 2))
        widgets.add(ReagentAlertSlot(inputs[5],reactantChk, 20, 21))
        widgets.addSlot(inputs[6], 40, 21)
        widgets.add(ReagentAlertSlot(inputs[7],reactantChk, 60, 21))
        widgets.add(ReagentAlertSlot(inputs[8],reactantChk, 20, 40))
        widgets.add(ReagentAlertSlot(inputs[9],reactantChk, 40, 40))
        widgets.add(ReagentAlertSlot(inputs[10],reactantChk, 60, 40))
        widgets.add(ReagentAlertSlot(inputs[11],reactantChk, 0, 42))
        widgets.add(ReagentAlertSlot(inputs[12],reactantChk, 81, 42))
        widgets.addSlot(outputs[0], 111, 21).recipeContext(this)
        widgets.addText(costText,costOffset,42,0x55FF55,true)
    }

}
