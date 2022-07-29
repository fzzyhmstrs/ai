package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import net.minecraft.util.Identifier

class AltarEmiRecipe(recipe: AltarRecipe): EmiRecipe{
    
    private val id: Identifier
    private val base: EmiIngredient
    private val addition: EmiIngredient
    private val result: EmiStack

    init{
        id = recipe.id
        base = EmiIngredient.of(recipe.base)
        addition = EmiIngredient.of(recipe.addition)
	    result = EmiStack.of(recipe.result)
    
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
    }
    
    override fun getId(): Identifier{
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return listOf(base,addition)
    }
    
    override fun getOutputs(): List<EmiStack>{
        return listOf(result)
    }
    
    override fun getDisplayWidth(): Int{
        return 125
    }
    
    override fun getDisplayHeight(): Int{
        return 40
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        widgets.addTexture(EmiTexture.PLUS, 27, 14)
		    widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 12)
		    widgets.addSlot(base, 0, 11)
		    widgets.addSlot(addition, 49, 11)
		    widgets.addSlot(result, 107, 11).recipeContext(this)
    }

}
