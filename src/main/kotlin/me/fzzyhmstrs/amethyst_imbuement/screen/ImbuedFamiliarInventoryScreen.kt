package me.fzzyhmstrs.amethyst_imbuement.screen

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class ImbuedFamiliarInventoryScreen(handler: ImbuedFamiliarInventoryScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<ImbuedFamiliarInventoryScreenHandler>(handler,playerInventory,title) {


    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        TODO("Not yet implemented")
    }


}