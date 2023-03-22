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
    private val dust: EmiIngredient
    private val base: EmiIngredient
    private val addition: EmiIngredient
    private val result: EmiStack

    init{
        id = recipe.id
        dust = EmiIngredient.of(recipe.dust)
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
        return listOf(dust,base,addition)
    }
    
    override fun getOutputs(): List<EmiStack>{
        return listOf(result)
    }
    
    override fun getDisplayWidth(): Int{
        return 112
    }
    
    override fun getDisplayHeight(): Int{
        return 18
    }
    
    override fun addWidgets(widgets: WidgetHolder){
		    widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 0)
		    widgets.addSlot(dust, 0, 0)
			widgets.addSlot(base, 18, 0)
		    widgets.addSlot(addition, 36, 0)
		    widgets.addSlot(result, 94, 0).recipeContext(this)
    }

}
