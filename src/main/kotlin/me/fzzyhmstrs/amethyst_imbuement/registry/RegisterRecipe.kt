package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipeSerializer
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterRecipe {

    fun registerAll(){
        Registry.register(Registry.RECIPE_SERIALIZER, ImbuingRecipeSerializer.ID, ImbuingRecipeSerializer)
        Registry.register(Registry.RECIPE_TYPE, Identifier(AI.MOD_ID, ImbuingRecipe.Type.ID), ImbuingRecipe.Type)
        Registry.register(Registry.RECIPE_SERIALIZER, AltarRecipeSerializer.ID, AltarRecipeSerializer)
        Registry.register(Registry.RECIPE_TYPE, Identifier(AI.MOD_ID, AltarRecipe.Type.ID), AltarRecipe.Type)
    }

}