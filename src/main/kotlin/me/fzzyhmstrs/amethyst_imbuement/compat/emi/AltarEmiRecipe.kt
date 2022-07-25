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
    
    }
    
    private fun initInputs(recipe: ImbuingRecipe): List<EmiIngredient>{
        TODO()
    }

    private fun initoutputs(recipe: ImbuingRecipe): List<EmiStack>{
        TODO()
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
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
    
    override fun getDisplayWidth(): Int{
        125
    }
    
    override fun getDisplayHeight(): Int{
        18
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
		    widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
		    widgets.addSlot(inputs[0], 0, 0);
		    widgets.addSlot(inputs[1], 49, 0);
		    widgets.addSlot(outputs[0], 107, 0).recipeContext(this);
    }

}
