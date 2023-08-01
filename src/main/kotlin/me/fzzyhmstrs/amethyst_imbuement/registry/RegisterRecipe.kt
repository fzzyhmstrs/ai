package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipeSerializer
import me.fzzyhmstrs.amethyst_imbuement.recipe.ImbuingRecipe
import me.fzzyhmstrs.amethyst_imbuement.recipe.ImbuingRecipeSerializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object RegisterRecipe {

    fun registerAll(){
        Registry.register(Registries.RECIPE_SERIALIZER, ImbuingRecipeSerializer.ID, ImbuingRecipeSerializer)
        Registry.register(Registries.RECIPE_TYPE, AI.identity( ImbuingRecipe.Type.ID), ImbuingRecipe.Type)
        Registry.register(Registries.RECIPE_SERIALIZER, AltarRecipeSerializer.ID, AltarRecipeSerializer)
        Registry.register(Registries.RECIPE_TYPE, AI.identity( AltarRecipe.Type.ID), AltarRecipe.Type)
    }

}