package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f
import org.lwjgl.glfw.GLFW
import kotlin.math.min


@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER", "SpellCheckingInspection")
class AltarOfExperienceScreen(handler: AltarOfExperienceScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<AltarOfExperienceScreenHandler>(handler, playerInventory, title) {

    private val texture = Identifier(AI.MOD_ID,"textures/gui/container/altar_of_experience_gui.png")
    private var xp = IntArray(4)
    private var shifted = false
    private val player = playerInventory.player

    override fun init() {
        super.init()
        handler.requestXpUpdates()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val shifted = if (this.shifted){
            4
        } else {
            0
        }
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        for (k in 0..3) {
            val d = mouseX - (i + 26).toDouble()
            val e = mouseY - (j + 33 + 11 * k).toDouble()
            if (d < 0.0 || e < 0.0 || d >= 124.0 || e >= 11.0 || !player.let {
                    (handler as AltarOfExperienceScreenHandler).onButtonClick(it, k + shifted)
                }
            ) continue
            client?.interactionManager?.clickButton(handler.syncId, k + shifted)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT){
            shifted = true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT){
            shifted = false
        }
        return super.keyReleased(keyCode, scanCode, modifiers)
    }


    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        DiffuseLighting.disableGuiDepthLighting()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, this.texture)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)
        val k = client?.window?.scaleFactor?.toInt()?:1
        RenderSystem.viewport((width - 320) / 2 * k, (height - 240) / 2 * k, 320 * k, 240 * k)
        val matrix4f = Matrix4f.translate(-0.34f, 0.23f, 0.0f)
        matrix4f.multiply(Matrix4f.viewboxMatrix(90.0, 1.3333334f, 9.0f, 80.0f))
        RenderSystem.backupProjectionMatrix()
        RenderSystem.setProjectionMatrix(matrix4f)

        client?.window?.framebufferWidth?.let { client?.window?.framebufferHeight?.let { it1 ->
            RenderSystem.viewport(0, 0, it,
                it1
            )
        } }

        RenderSystem.restoreProjectionMatrix()
        DiffuseLighting.enableGuiDepthLighting()

        val xpStored = handler.getSyncedStoredXp()
        val xpMax = handler.getSyncedMaxXp()
        val xpLeft = xpMax - xpStored
        val xpPlayer = handler.getPlayerXp()
        if (shifted) {
            xp[0] = if (xpLeft < xpPlayer) {
                xpLeft
            } else {
                xpPlayer
            }
            xp[1] = if (xpLeft < 5000 || xpPlayer < 5000) {
                min(xpLeft, xpPlayer)
            } else {
                5000
            }
            xp[2] = if (xpStored < 5000) {
                xpStored
            } else {
                5000
            }
            xp[3] = xpStored
        } else {
            xp[0] = if (xpLeft < 500 || xpPlayer < 500) {
                min(xpLeft, xpPlayer)
            } else {
                500
            }
            xp[1] = if (xpLeft < 50 || xpPlayer < 50) {
                min(xpLeft, xpPlayer)
            } else {
                50
            }
            xp[2] = if (xpStored < 50) {
                xpStored
            } else {
                50
            }
            xp[3] = if (xpStored < 500) {
                xpStored
            } else {
                500
            }
        }
        //println("array of xp: ${xp[0]}, ${xp[1]}, ${xp[2]}, ${xp[3]}")
        //base text color and string
        val t = 8453920

        //screen offsets
        val p = i + 26 //offset from left edge of enchantment boxes

        //top button
        for (b in 0..3){
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, this.texture)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            val bl1 = xp[b] == 0 || (b == 0 && xp[b] <= 50) || (b == 1 && xp[b] <= 0)  || (b == 2 && xp[b] <= 0) || (b == 3 && xp[b] <= 50)
            val bl2 = xp[b] == 0 || (b == 0 && xp[b] <= 5000) || (b == 1 && xp[b] <= 0) || (b == 2 && xp[b] <= 0) || (b == 3 && xp[b] <= 5000)
            if ((!shifted && bl1) || (shifted && bl2)){
                this.drawTexture(matrices, p, j + 33 + 11 * b, 0, 177, 124, 11)
            } else {
                //base mouse positions (top left corner of buttons)
                val u = mouseX - (i + 34)
                val v = mouseY - (j + 33 + 11 * b)
                val button1Text: Text = if(b < 2){
                    AcText.translatable("container.altar_of_experience_button_a").append(AcText.literal(xp[b].toString())).append(
                        AcText.translatable("container.altar_of_experience_button_b"))
                } else {
                    AcText.translatable("container.altar_of_experience_button_c").append(AcText.literal(xp[b].toString())).append(
                        AcText.translatable("container.altar_of_experience_button_b"))
                }
                if (u >= 0 && v >= 0 && u < 108 && v < 11) {
                    this.drawTexture(matrices, p, j + 33 + 11 * b, 0, 188, 124, 11)
                } else {
                    this.drawTexture(matrices, p, j + 33 + 11 * b, 0, 166, 124, 11)
                }
                textRenderer.drawWithShadow(
                    matrices,
                    button1Text,
                    (p + 62 - textRenderer.getWidth(button1Text)/2.0f),
                    (j + 33 + 11 * b + 2).toFloat(),
                    t
                )
            }
        }
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        textRenderer.draw(matrices, title, titleX.toFloat(), titleY.toFloat(), 0x404040)
        //super.drawForeground(matrices, mouseX, mouseY)
        val storedXp = handler.getSyncedStoredXp()
        val maxXp = handler.getSyncedMaxXp()
        val text = AcText.translatable("container.altar_of_experience_1").append(AcText.literal("$storedXp/$maxXp"))
        if (text != null) {
            val k = backgroundWidth/2.0f - this.textRenderer.getWidth(text)/2.0f
            val k2 = backgroundWidth/2.0f + this.textRenderer.getWidth(text)/2.0f
            fill(matrices, (k - 2).toInt(), 17, (k2 + 2).toInt(), 29, 0x4F000000)
            val t = when (storedXp) {
                0 -> {
                    0xFF6060
                }
                maxXp -> {
                    0x60BFFF
                }
                else -> {
                    8453920
                }
            }
            textRenderer.drawWithShadow(matrices, text, k, 19.0f, t)
        }
    }


    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val dlta = client?.tickDelta?:delta
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, dlta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        for (j in 0..3) {
            if (xp[j] == 0) continue
            if (!isPointWithinBounds(
                    26,
                    33 + 11 * j,
                    124,
                    11,
                    mouseX.toDouble(),
                    mouseY.toDouble()
                )) continue
            val tooltipText = if (j < 2){
                AcText.translatable("container.altar_of_experience_tooltip_1").formatted(Formatting.WHITE)
            } else {
                AcText.translatable("container.altar_of_experience_tooltip_2").formatted(Formatting.WHITE)
            }
            val tooltipText2 = if (!shifted){
                AcText.translatable("container.altar_of_experience_tooltip_3").formatted(Formatting.ITALIC)
            } else {
                AcText.translatable("container.altar_of_experience_tooltip_4").formatted(Formatting.ITALIC)
            }

            val tooltipList = listOf(
                tooltipText,
                tooltipText2
            )

            this.renderTooltip(matrices, tooltipList, mouseX, mouseY)
            break
        }
    }
}