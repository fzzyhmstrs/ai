package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.util.Identifier
import dev.emi.emi.api.recipe.*
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.EmiPlugin

class ImbuingEmiRecipe(recipe: ImbuingRecipe): EmiRecipe{
    
    private val id: Identifier
    private val inputs: List<EmiIngredient>
    private val outputs: List<EmiStack>
    private val isEnchantingType: Boolean

    init{
        id = recipe.id
        inputs = initInputs(recipe)
        outputs = initOutputs(recipe)
        isEnchantingType = recipe.getAugment() != ""
    
    }
    
    private fun initInputs(recipe: ImbuingRecipe): List<EmiIngredient>{
        TODO()
    }

    private fun initoutputs(recipe: ImbuingRecipe): List<EmiStack>{
        TODO()
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.IMBUING_CATEGORY
    }
    
    override fun getId(){
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
        152
    }
    
    override fun getDisplayHeight(): Int{
        76
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        val xOffset = 5
        val yOffset = 5
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 88, 25)
        widgets.addSlot(inputs[0], xOffset, yOffset)
        widgets.addSlot(inputs[1], xOffset + 87, yOffset)
        widgets.addSlot(inputs[2], xOffset + 20, yOffset + 2)
        widgets.addSlot(inputs[3], xOffset + 43, yOffset + 2)
        widgets.addSlot(inputs[4], xOffset + 66, yOffset + 2)
        widgets.addSlot(inputs[5], xOffset + 20, yOffset + 25)
        widgets.addSlot(inputs[6], xOffset + 43, yOffset + 25)
        widgets.addSlot(inputs[7], xOffset + 66, yOffset + 25)
        widgets.addSlot(inputs[8], xOffset + 20, yOffset + 48)
        widgets.addSlot(inputs[9], xOffset + 43, yOffset + 48)
        widgets.addSlot(inputs[10], xOffset + 66, yOffset + 48)
        widgets.addSlot(inputs[11], xOffset, yOffset + 50)
        widgets.addSlot(inputs[12], xOffset + 87, yOffset + 50)
        widgets.addSlot(outputs[0], xOffset + 120, yOffset + 25).recipeContext(this)
    }

}
