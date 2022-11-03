package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.scepter.ResonateAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.SmitingBlowAugment
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingRecipeBookScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler

object RegisterNetworking {

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        ImbuingTableScreenHandler.registerServer()
    }
    fun registerClient(){
        ImbuingTableScreenHandler.registerClient()
        ResonateAugment.registerClient()
        SmitingBlowAugment.registerClient()
        ImbuingRecipeBookScreen.registerClient()
    }
}