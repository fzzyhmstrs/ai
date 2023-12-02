package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.screen.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(value = EnvType.CLIENT)
object RegisterScreen {

    fun registerAll(){

        ScepterHud.registerClient()

        HandledScreens.register(RegisterHandler.IMBUING_SCREEN_HANDLER) {
                handler: ImbuingTableScreenHandler, playerInventory: PlayerInventory, title: Text ->
            ImbuingTableScreen(
                handler,
                playerInventory,
                title
            )
        }

        HandledScreens.register(RegisterHandler.ALTAR_OF_EXPERIENCE_SCREEN_HANDLER) {
                handler: AltarOfExperienceScreenHandler, playerInventory: PlayerInventory, title: Text ->
            AltarOfExperienceScreen(
                handler,
                playerInventory,
                title
            )
        }

        HandledScreens.register(RegisterHandler.DISENCHANTING_TABLE_SCREEN_HANDLER) {
                handler: DisenchantingTableScreenHandler, playerInventory: PlayerInventory, title: Text ->
            DisenchantingTableScreen(
                handler,
                playerInventory,
                title
            )
        }

        HandledScreens.register(RegisterHandler.CRYSTAL_ALTAR_SCREEN_HANDLER) {
                handler: CrystalAltarScreenHandler, playerInventory: PlayerInventory, title: Text ->
            CrystalAltarScreen(
                handler,
                playerInventory,
                title
            )
        }

        HandledScreens.register(RegisterHandler.SPELLCASTERS_FOCUS_SCREEN_HANDLER) {
                handler: SpellcastersFocusScreenHandler, playerInventory: PlayerInventory, title: Text ->
            SpellcastersFocusScreen(
                handler,
                playerInventory,
                title
            )
        }
    }
}