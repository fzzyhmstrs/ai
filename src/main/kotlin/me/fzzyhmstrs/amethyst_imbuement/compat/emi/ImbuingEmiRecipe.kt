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
        TODO()
    }
    
    override fun getDisplayHeight(): Int{
        TODO()
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        TODO()
    }

}
