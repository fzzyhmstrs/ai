package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.util.Identifier
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.EmiPlugin

class EmiClientPlugin: EmiPlugin{
    val SPRITE_SHEET = Identifier(AI.MOD_ID,"textures/gui/imbuing_textures.png")
    val IMBUING_WORKSTATION = EmiStack.of(RegisterBlock.IMBUING_TABLE.asItem())
    val IMBUING_CATEGORY = EmiRecipeCategory(IMBUING_WORKSTATION, EmiTexture(SPRITE_SHEET, 0, 0, 16, 16))
    
    override fun register(registry: EmiRegistry){
        registry.addCategory(IMBUING_CATEGORY)
        registry.addWorkstation(IMBUING_CATEGORY, IMBUING_WORKSTATION)
        
        val manager = registry.getRecipeManager()
        
        for (recipe in manager.listAllOfType(ImbuingRecipe.Type)) {
            registry.addRecipe(ImbuingEmiRecipe(recipe));
        }
    }
    
}
