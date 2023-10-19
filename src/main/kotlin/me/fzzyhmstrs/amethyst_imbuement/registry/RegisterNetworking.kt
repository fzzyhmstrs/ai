package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.spells.ResonateAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.SmitingBlowAugment
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil

object RegisterNetworking {

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        AltarOfExperienceScreenHandler.registerServer()
        ImbuingTableScreenHandler.registerServer()
        RecipeUtil.registerServer()
    }
    fun registerClient(){
        ImbuingTableScreenHandler.registerClient()
        AltarOfExperienceScreenHandler.registerClient()
        ResonateAugment.registerClient()
        SmitingBlowAugment.registerClient()
        BookOfTalesItem.registerClient()
        //ImbuingRecipeBookScreen.registerClientReceiver()
    }
}
