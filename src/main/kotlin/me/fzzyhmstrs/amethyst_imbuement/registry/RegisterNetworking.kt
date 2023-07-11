package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.recipe.RecipeUtil
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.spells.SmitingBlowAugment

object RegisterNetworking {

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        ImbuingTableScreenHandler.registerServer()
        RecipeUtil.registerServer()
    }
    fun registerClient(){
        ImbuingTableScreenHandler.registerClient()
        SmitingBlowAugment.registerClient()
        //ImbuingRecipeBookScreen.registerClientReceiver()
    }
}