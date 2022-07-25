package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import net.minecraft.util.Identifier
import dev.emi.emi.api.recipe.*
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.EmiPlugin

class ImbuingEmiRecipe(recipe: AltarRecipe): EmiRecipe{
    
    private val id: Identifier
    private val base: EmiIngredient
    private val addition: EmiIngredient
    private val output: EmiStack

    init{
        id = recipe.id
        base = EmiIngredient.of(recipe.base)
        addition = EmiIngredient.of(recipe.addition)
	result = EmiStack.of(recipe.result)
    
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
    }
    
    override fun getId(){
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return listOf(base,addition)
    }
    
    override fun getOutputs(): List<EmiStack>{
        return output
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
		    widgets.addSlot(base, 0, 0);
		    widgets.addSlot(addition, 49, 0);
		    widgets.addSlot(output, 107, 0).recipeContext(this);
    }

}
