package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.screen.*
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

object RegisterHandler {
    var IMBUING_SCREEN_HANDLER: ScreenHandlerType<ImbuingTableScreenHandler> = FzzyPort.buildHandlerType{ s: Int, p: PlayerInventory ->
        ImbuingTableScreenHandler(s, p)
    }
    var ALTAR_OF_EXPERIENCE_SCREEN_HANDLER: ScreenHandlerType<AltarOfExperienceScreenHandler> = FzzyPort.buildHandlerType{ s: Int, p: PlayerInventory ->
        AltarOfExperienceScreenHandler(s, p)
    }
    var DISENCHANTING_TABLE_SCREEN_HANDLER: ScreenHandlerType<DisenchantingTableScreenHandler> = FzzyPort.buildHandlerType{ s: Int, p: PlayerInventory ->
        DisenchantingTableScreenHandler(s, p)
    }
    var CRYSTAL_ALTAR_SCREEN_HANDLER: ScreenHandlerType<CrystalAltarScreenHandler> = FzzyPort.buildHandlerType{ s: Int, p: PlayerInventory ->
        CrystalAltarScreenHandler(s, p)
    }
    var SPELLCASTERS_FOCUS_SCREEN_HANDLER: ExtendedScreenHandlerType<SpellcastersFocusScreenHandler> = ExtendedScreenHandlerType { syncID: Int, playerInventory: PlayerInventory, buf: PacketByteBuf ->
        SpellcastersFocusScreenHandler(syncID, playerInventory, buf)
    }

    fun registerAll(){
        FzzyPort.SCREEN_HANDLER.register(AI.identity("imbuing_table"), IMBUING_SCREEN_HANDLER)
        FzzyPort.SCREEN_HANDLER.register(AI.identity("altar_of_experience"), ALTAR_OF_EXPERIENCE_SCREEN_HANDLER)
        FzzyPort.SCREEN_HANDLER.register(AI.identity("disenchanting_table"), DISENCHANTING_TABLE_SCREEN_HANDLER)
        FzzyPort.SCREEN_HANDLER.register(AI.identity("crystal_altar"), CRYSTAL_ALTAR_SCREEN_HANDLER)
        FzzyPort.SCREEN_HANDLER.register(AI.identity("spellcasters_focus"), SPELLCASTERS_FOCUS_SCREEN_HANDLER)
    }
}
