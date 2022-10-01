package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext

class ImbuedFamiliarInventoryScreenHandler(
    syncId: Int,
    private val familiar: ImbuedFamiliarEntity?,
    private val playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext):
    ScreenHandler(RegisterHandler.FAMILIAR_SCREEN_HANDLER,syncId) {

        constructor(syncId: Int, playerInventory: PlayerInventory):
                this(syncId, null,playerInventory, ScreenHandlerContext.EMPTY)


    override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack {
        TODO("Not yet implemented")
    }


    override fun canUse(player: PlayerEntity?): Boolean {
        TODO("Not yet implemented")
    }

}