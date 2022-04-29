package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f


@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER", "SpellCheckingInspection")
class DisenchantingTableScreen(handler: DisenchantingTableScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<DisenchantingTableScreenHandler>(handler, playerInventory, title) {

    private val texture = Identifier("amethyst_imbuement","textures/gui/container/disenchanting_table_gui.png")
    private val player = playerInventory.player

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        for (k in 0..2) {
            val d = mouseX - (i + 60).toDouble()
            val e = mouseY - (j + 14 + 19 * k).toDouble()
            if (d < 0.0 || e < 0.0 || d >= 108.0 || e >= 19.0 || !player.let {
                    (handler as DisenchantingTableScreenHandler).onButtonClick(
                        it, k
                    )
                }
            ) continue
            client?.interactionManager?.clickButton((handler as DisenchantingTableScreenHandler).syncId, k)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
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

        val cost = handler.disenchantCost[0]
        val p = i + 60
        val q = p + 20
        var tens = 0
        var ones = 0
        var tensOfst = 1
        var onesOfst = 8
        var tensImageOfst = 0
        var onesImageOfst = 9

        for (o in 0..2) {
            zOffset = 0
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, this.texture)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            val r = handler.enchantmentId[o]
            val lvl = handler.enchantmentLevel[o]
            if (r == -1 || cost == -1) {
                //draws the "browned out" no-entry box
                this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 185, 108, 19)
                continue //jumps to the next enchantment in the list
            }
            val string = "" + handler.disenchantCost[0]
            val s = 86 - textRenderer.getWidth(string)
            val str = Enchantment.byRawId(r)?.getName(lvl)?.string?: TranslatableText("container.disenchanting_table.button.missing_enchantment").toString()
            val stringVisitable: StringVisitable = LiteralText(str).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")))
            var t = 6839882
            var t2 = 0x404040//6839882
            val oOfst = handler.disenchantCost[0] - 1

            var vertOffset = 0
            var vertOffset2 = 0
            val u = mouseX - (i + 60)
            val v = mouseY - (j + 14 + 19 * o)
            if (o == 1) {
                if ((((player.experienceLevel) >= cost) || (player.abilities.creativeMode)) && handler.getSlotStack(1).isOf(Items.BOOK)) {
                    t2 = if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                        this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 204, 108, 19)
                        0xFFFF80
                    } else {
                        this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 166, 108, 19)
                        0x404040
                        //6839882
                    }
                    vertOffset = 0
                    vertOffset2 = 0
                    t = 8453920
                } else {
                    this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 185, 108, 19)
                    vertOffset = 16
                    vertOffset2 = 10
                    t = 4226832
                    t2 = 0xC0C0C0//0x342F25
                }
                if (oOfst <= 6) {
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + 19 * o,
                        108 + 16 * oOfst,
                        166+vertOffset,
                        16,
                        16
                    )
                } else {
                    //draw in an empty level 6 experience orb picture as a background for the numbers
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + 19 * o,
                        108 + 16 * 6,
                        166+vertOffset,
                        16,
                        16
                    )

                    //split the needed levels into tens place and one's place. Integer division to get the tens place, then subtract out to get the remainder
                    if (cost > 9){
                        tens = cost/10
                        ones = cost - tens * 10
                        if(tens > 9){
                            tens = 9
                            ones = 9
                        }
                    } else{
                        tens = 0
                        ones = cost
                    }
                    //determine the tens place visual offset. numbers show up with a 1 pixel gap, but the "1" is 2 pixels narrower so when 1s are present the offset changes.
                    if (tens == 1){tensOfst += 1}
                    if (ones == 1){tensOfst += 2}
                    tensImageOfst = tens - 1
                    //determine the visual offset for the ones place
                    if(ones == 1) {onesOfst = 9}
                    if (ones != 0) {
                        onesImageOfst = ones - 1
                    }
                    //draw the ones place numeral
                    this.drawTexture(
                        matrices,
                        p + 1 + onesOfst,
                        j + 15 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        204 + vertOffset2,
                        9,
                        9
                    )
                    if (tens>0) this.drawTexture(
                        matrices,
                        p + 1 + tensOfst,
                        j + 15 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        204 + vertOffset2,
                        9,
                        9
                    )

                }
                if (textRenderer.getWidth(stringVisitable) > s) {
                    textRenderer.drawTrimmed(stringVisitable, q, (j + 16 - 1 + 19 * o), s, t2)
                } else {
                    textRenderer.drawTrimmed(stringVisitable, q, (j + 16 + 3 + 19 * o), s, t2)
                }
                textRenderer.drawWithShadow(
                    matrices,
                    string,
                    (q + 86 - textRenderer.getWidth(string)).toFloat(),
                    (j + 16 + 19 * o + 7).toFloat(),
                    t
                )
            } else {
                val horOffset = o * 8 //0 at o = 0, 16 at o = 2
                if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                    this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 204, 108, 19)
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + 19 * o,
                        0 + horOffset,
                        239,
                        16,
                        16
                    )
                    t2 = 0xFFFF80
                } else {
                    this.drawTexture(matrices, p, j + 14 + 19 * o, 0, 166, 108, 19)
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + 19 * o,
                        0 + horOffset,
                        223,
                        16,
                        16
                    )
                    t2 = 0x404040
                }
                if (textRenderer.getWidth(stringVisitable) > s) {
                    textRenderer.drawTrimmed(stringVisitable, q, (j + 16 - 1 + 19 * o), s, t2)
                } else {
                    textRenderer.drawTrimmed(stringVisitable, q, (j + 16 + 3 + 19 * o), s, t2)
                }
            }

        }
    }


    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val dlta = client?.tickDelta?:delta
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, dlta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        for (j in 0..2) {
            if (!isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX.toDouble(), mouseY.toDouble())) continue
            val tooltipText = if (handler.disenchantCost[0] < 0){
                TranslatableText("container.disenchanting_table.tooltip.limit").formatted(Formatting.WHITE)
            } else if ((player.experienceLevel) < handler.disenchantCost[0] && j == 1){
                TranslatableText("container.disenchanting_table.tooltip${j+1}.level").formatted(Formatting.WHITE)
            }else if ((!handler.getSlotStack(1).isOf(Items.BOOK)) && j == 1){
                TranslatableText("container.disenchanting_table.tooltip${j+1}.level").formatted(Formatting.WHITE)
            } else {
                if (handler.enchantmentId[j] == -1) continue
                TranslatableText("container.disenchanting_table.tooltip${j + 1}").formatted(Formatting.WHITE)
            }
            this.renderTooltip(matrices, tooltipText, mouseX, mouseY)
            break
        }
    }

    init{
        super.init()
    }
}