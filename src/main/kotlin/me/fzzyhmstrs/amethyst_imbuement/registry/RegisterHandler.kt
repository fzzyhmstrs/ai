package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.DisenchantingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object RegisterHandler {
    var IMBUING_SCREEN_HANDLER: ScreenHandlerType<ImbuingTableScreenHandler>? = null
    var ALTAR_OF_EXPERIENCE_SCREEN_HANDLER: ScreenHandlerType<AltarOfExperienceScreenHandler>? = null
    var DISENCHANTING_TABLE_SCREEN_HANDLER: ScreenHandlerType<DisenchantingTableScreenHandler>? = null
    //var STEEL_ANVIL_SCREEN_HANDLER: ScreenHandlerType<SteelAnvilScreenHandler2>? = null
    var CRYSTAL_ALTAR_SCREEN_HANDLER: ScreenHandlerType<CrystalAltarScreenHandler>? = null

    fun registerAll(){
        IMBUING_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier("amethyst_imbuement","imbuing_table")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            ImbuingTableScreenHandler(
                syncID,
                playerInventory
            )
        }
        ALTAR_OF_EXPERIENCE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier("amethyst_imbuement","altar_of_experience")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            AltarOfExperienceScreenHandler(
                syncID,
                playerInventory
            )
        }
        DISENCHANTING_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier("amethyst_imbuement","disenchanting_table")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            DisenchantingTableScreenHandler(
                syncID,
                playerInventory
            )
        }
        CRYSTAL_ALTAR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier("amethyst_imbuement","crystal_altar")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            CrystalAltarScreenHandler(
                syncID,
                playerInventory
            )
        }
        /*STEEL_ANVIL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier("amethyst_imbuement","steel_anvil")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            SteelAnvilScreenHandler2(
                syncID,
                playerInventory
            )
        }*/


    }
}