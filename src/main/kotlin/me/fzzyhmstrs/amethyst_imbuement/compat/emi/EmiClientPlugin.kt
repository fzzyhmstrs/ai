package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.Comparison
import dev.emi.emi.api.stack.EmiStack
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import java.util.function.Function


object EmiClientPlugin: EmiPlugin{
    private val IMBUING_WORKSTATION: EmiStack = EmiStack.of(RegisterBlock.IMBUING_TABLE.asItem())
    val IMBUING_CATEGORY = EmiRecipeCategory(IMBUING_WORKSTATION.id, IMBUING_WORKSTATION, AiSimplifiedRenderer(0,0))
    private val IMBUING_HANDLER = ImbuingRecipeHandler()
    private val ALTAR_WORKSTATION: EmiStack = EmiStack.of(RegisterBlock.CRYSTAL_ALTAR.asItem())
    val ALTAR_CATEGORY = EmiRecipeCategory(ALTAR_WORKSTATION.id, ALTAR_WORKSTATION, AiSimplifiedRenderer(16,0))
    private val ALTAR_HANDLER = AltarRecipeHandler()
    
    override fun register(registry: EmiRegistry){

        val manager = registry.recipeManager

        registry.addCategory(IMBUING_CATEGORY)
        registry.addWorkstation(IMBUING_CATEGORY, IMBUING_WORKSTATION)
        
        for (recipe in manager.listAllOfType(ImbuingRecipe.Type)) {
            registry.addRecipe(ImbuingEmiRecipe(recipe))
        }

        registry.addRecipeHandler(RegisterHandler.IMBUING_SCREEN_HANDLER, IMBUING_HANDLER)

        val compareNbt =
            Function { c: Comparison ->
                c.copy().nbt(true).build()
            }
        registry.setDefaultComparison(RegisterItem.SPELL_SCROLL, compareNbt)

        registry.addCategory(ALTAR_CATEGORY)
        registry.addWorkstation(ALTAR_CATEGORY, ALTAR_WORKSTATION)
        
        for (recipe in manager.listAllOfType(AltarRecipe.Type)) {
            registry.addRecipe(AltarEmiRecipe(recipe))
        }

        registry.addRecipeHandler(RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER, ALTAR_HANDLER)
    }
    
}
