package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.DisenchantingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterHandler {
    var IMBUING_SCREEN_HANDLER: ScreenHandlerType<ImbuingTableScreenHandler>? = null
    var ALTAR_OF_EXPERIENCE_SCREEN_HANDLER: ScreenHandlerType<AltarOfExperienceScreenHandler>? = null
    var DISENCHANTING_TABLE_SCREEN_HANDLER: ScreenHandlerType<DisenchantingTableScreenHandler>? = null
    var CRYSTAL_ALTAR_SCREEN_HANDLER: ScreenHandlerType<CrystalAltarScreenHandler>? = null

    fun registerAll(){

        IMBUING_SCREEN_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            ImbuingTableScreenHandler(
                syncID,
                playerInventory
            )
        }

        ALTAR_OF_EXPERIENCE_SCREEN_HANDLER = ScreenHandlerType() { syncID: Int, playerInventory: PlayerInventory ->
            AltarOfExperienceScreenHandler(
                syncID,
                playerInventory
            )
        }

        DISENCHANTING_TABLE_SCREEN_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            DisenchantingTableScreenHandler(
                syncID,
                playerInventory
            )
        }

        CRYSTAL_ALTAR_SCREEN_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
        CrystalAltarScreenHandler(
            syncID,
            playerInventory
        )
        }

        Registry.register(Registry.SCREEN_HANDLER,Identifier(AI.MOD_ID,"imbuing_table"), IMBUING_SCREEN_HANDLER)
        Registry.register(Registry.SCREEN_HANDLER,Identifier(AI.MOD_ID,"altar_of_experience"), ALTAR_OF_EXPERIENCE_SCREEN_HANDLER)
        Registry.register(Registry.SCREEN_HANDLER,Identifier(AI.MOD_ID,"disenchanting_table"), DISENCHANTING_TABLE_SCREEN_HANDLER)
        Registry.register(Registry.SCREEN_HANDLER,Identifier(AI.MOD_ID,"crystal_altar"), CRYSTAL_ALTAR_SCREEN_HANDLER)

    }

    /*CRYSTAL_ALTAR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier(AI.MOD_ID,"crystal_altar")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            CrystalAltarScreenHandler(
                syncID,
                playerInventory
            )
    }*/
    /*DISENCHANTING_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
        Identifier(AI.MOD_ID,"disenchanting_table")
    ) { syncID: Int, playerInventory: PlayerInventory ->
        DisenchantingTableScreenHandler(
            syncID,
            playerInventory
        )
    }*/
    /*ALTAR_OF_EXPERIENCE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
        Identifier(AI.MOD_ID,"altar_of_experience")
    ) { syncID: Int, playerInventory: PlayerInventory ->
        AltarOfExperienceScreenHandler(
            syncID,
            playerInventory
        )
    }*/
    /*IMBUING_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
            Identifier(AI.MOD_ID,"imbuing_table")
        ) { syncID: Int, playerInventory: PlayerInventory ->
            ImbuingTableScreenHandler(
                syncID,
                playerInventory
            )
    }*/
}