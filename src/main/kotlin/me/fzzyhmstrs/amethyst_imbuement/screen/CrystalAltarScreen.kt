package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.ForgingScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@Environment(value = EnvType.CLIENT)
class CrystalAltarScreen(handler: CrystalAltarScreenHandler, playerInventory: PlayerInventory, title: Text) :
    HandledScreen<CrystalAltarScreenHandler>(handler, playerInventory, title) {

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        //super.drawBackground(matrices, delta, mouseX, mouseY)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        if (handler.flowerSlot.get() > 0) {
            this.drawTexture(matrices, i + 76, j + 47, 27, 47, 16, 16)
        }

    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
    }

    companion object {
        private val TEXTURE = Identifier(AI.MOD_ID,"textures/gui/container/crystal_altar.png")
    }

    init {
        titleX = 60
        titleY = 18
    }
}