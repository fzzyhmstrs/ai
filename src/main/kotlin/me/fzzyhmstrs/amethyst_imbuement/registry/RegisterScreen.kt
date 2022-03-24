package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.screen.*
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

object RegisterScreen {

    fun registerAll(){
        ScreenRegistry.register(RegisterHandler.IMBUING_SCREEN_HANDLER) {
                handler: ImbuingTableScreenHandler, playerInventory: PlayerInventory, title: Text ->
            ImbuingTableScreen(
                handler,
                playerInventory,
                title
            )
        }
        ScreenRegistry.register(RegisterHandler.ALTAR_OF_EXPERIENCE_SCREEN_HANDLER) {
                handler: AltarOfExperienceScreenHandler, playerInventory: PlayerInventory, title: Text ->
            AltarOfExperienceScreen(
                handler,
                playerInventory,
                title
            )
        }
        ScreenRegistry.register(RegisterHandler.DISENCHANTING_TABLE_SCREEN_HANDLER) {
                handler: DisenchantingTableScreenHandler, playerInventory: PlayerInventory, title: Text ->
            DisenchantingTableScreen(
                handler,
                playerInventory,
                title
            )
        }
        ScreenRegistry.register(RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER) {
                handler: CrystalAltarScreenHandler, playerInventory: PlayerInventory, title: Text ->
            CrystalAltarScreen(
                handler,
                playerInventory,
                title
            )
        }
    }

}