package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.scepter.ResonateAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.SmitingBlowAugment
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil

object RegisterNetworking {

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        ImbuingTableScreenHandler.registerServer()
        RecipeUtil.registerServer()
    }
    fun registerClient(){
        ImbuingTableScreenHandler.registerClient()
        ResonateAugment.registerClient()
        SmitingBlowAugment.registerClient()
        //ImbuingRecipeBookScreen.registerClientReceiver()
    }
}