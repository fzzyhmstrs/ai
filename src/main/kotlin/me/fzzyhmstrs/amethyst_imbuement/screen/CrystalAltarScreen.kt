package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@Environment(value = EnvType.CLIENT)
class CrystalAltarScreen(handler: CrystalAltarScreenHandler, playerInventory: PlayerInventory, title: Text) :
    HandledScreen<CrystalAltarScreenHandler>(handler, playerInventory, title) {

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)
        if ((handler.getSlot(0).hasStack() || handler.getSlot(1).hasStack() || handler.getSlot(2).hasStack())
            && !handler.getSlot(3).hasStack()
        ) {
            drawTexture(matrices, i + 92, j + 45, backgroundWidth, 0, 28, 21)
        }
        if (handler.getSlot(0).hasStack()){
            drawTexture(matrices, i + 33, j + 47, 51, 47, 16, 16)
        }

    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        RenderSystem.disableBlend()
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
    }

    companion object {
        private val TEXTURE = Identifier(AI.MOD_ID,"textures/gui/container/crystal_altar_gui.png")
    }

    init {
        titleX = 60
        titleY = 18
    }
}