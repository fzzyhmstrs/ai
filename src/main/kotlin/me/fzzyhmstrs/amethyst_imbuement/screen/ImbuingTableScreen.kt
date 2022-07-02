package me.fzzyhmstrs.amethyst_imbuement.screen

import com.google.common.collect.Lists
import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.registry.Registry


class ImbuingTableScreen(handler: ImbuingTableScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<ImbuingTableScreenHandler>(handler, playerInventory, title) {

    private val texture = Identifier(AI.MOD_ID,"textures/gui/container/imbuing_table_gui.png")
    private val backgrdWidth = 234
    private val backgrdHeight = 174
    private val player = playerInventory.player

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        return mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + backgrdWidth).toDouble() || mouseY >= (top + backgrdHeight).toDouble()
    }


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2
        for (k in 0..2) {
            val d = mouseX - (i + 118).toDouble()
            val e = mouseY - (j + 14 + 4 + 19 * k).toDouble()
            if (d < 0.0 || e < 0.0 || d >= 108.0 || e >= 19.0 || player.let {
                    (handler as ImbuingTableScreenHandler).onButtonClick(
                        it, k
                    )
                }
            ) continue
            client?.interactionManager?.clickButton(handler.syncId, k)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }


    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        DiffuseLighting.disableGuiDepthLighting()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, this.texture)
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2
        val ofst2 = 4 //ofst to handle the screen height change
        this.drawTexture(matrices, i, j, 0, 0, backgrdWidth, backgrdHeight)
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
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        EnchantingPhrases.getInstance().setSeed((handler as ImbuingTableScreenHandler).getSeed().toLong())
        val n = handler.getLapisCount()
        for (o in 0..2) {
            val p = i + 118 //offset from left edge of enchantment boxes
            val q = p + 20
            zOffset = 0
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, this.texture)
            val r = handler.enchantmentPower[o]
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            if (r == 0) {
                //draws the "browned out" no-entry box
                this.drawTexture(matrices, p, j + 14 + ofst2 + 19 * o, 0, 193, 108, 19)
                continue //jumps to the next enchantment in the list
            }
            val string = "" + r
            val s = 86 - textRenderer.getWidth(string)
            val stringVisitable = EnchantingPhrases.getInstance().generatePhrase(textRenderer, s)
            var t = 6839882
            var oOfst = 0
            //println(handler.imbueId[o].toString())
            if (handler.imbueId[o] == 0) {
                when (r) {
                    in 31..40 -> oOfst = 3 - o
                    in 41..50 -> oOfst = 4 - o
                    in 51..60 -> oOfst = 5 - o
                }
            } else{
                oOfst = r - o - 1
            }
            var tens: Int
            var ones: Int
            var tensOfst = 1
            var onesOfst = 8
            var tensImageOfst: Int
            var onesImageOfst = 9
            if (!(((n >= o+oOfst + 1) || handler.imbueId[o] != 0 || handler.modId[o] > 0) && (player.experienceLevel >= r && handler.levelLow[o] == 0 || player.abilities.creativeMode))) {
                this.drawTexture(matrices, p, j + 14 + ofst2 + 19 * o, 0, 193, 108, 19)
                if ((o+oOfst) <= 5) {
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + ofst2 + 19 * o,
                        108 + 16 * (o + oOfst),
                        174 + 16,
                        16,
                        16
                    )
                } else {
                    //draw in an empty level 6 experience orb picture as a background for the numbers
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + ofst2 + 19 * o,
                        108 + 16 * 6,
                        174+16,
                        16,
                        16
                    )

                    //split the needed levels into tens place and one's place. Integer division to get the tens place, then subtract out to get the remainder
                    if ((r) > 9){
                        tens = r/10
                        ones = r - tens * 10
                        if(tens > 9){
                            tens = 9
                            ones = 9
                        }
                    } else{
                        tens = 0
                        ones = r
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
                        j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        222,
                        9,
                        9
                    )
                    if (tens>0) this.drawTexture(
                        matrices,
                        p + 1 + tensOfst,
                        j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        222,
                        9,
                        9
                    )

                }
                textRenderer.drawTrimmed(stringVisitable, q, j + 16 + ofst2 + 19 * o, s, t and 0xFEFEFE shr 1)
                t = 4226832
            } else {
                val u = mouseX - (i + 118)
                val v = mouseY - (j + 14 + ofst2 + 19 * o)
                if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                    this.drawTexture(matrices, p, j + 14 + ofst2 + 19 * o, 0, 212, 108, 19)
                    t = 0xFFFF80
                } else {
                    this.drawTexture(matrices, p, j + 14 + ofst2 + 19 * o, 0, 174, 108, 19)
                }
                if ((o+oOfst) <= 5) {
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + ofst2 + 19 * o,
                        108 + 16 * (o + oOfst),
                        174,
                        16,
                        16
                    )
                } else {
                    //draw in an empty level 6 experience orb picture as a background for the numbers
                    this.drawTexture(
                        matrices,
                        p + 1,
                        j + 15 + ofst2 + 19 * o,
                        108 + 16 * 6,
                        174,
                        16,
                        16
                    )

                    //split the needed levels into tens place and one's place. Integer division to get the tens place, then subtract out to get the remainder
                    if (r > 9){
                        tens = r/10
                        ones = r - tens * 10
                        if(tens > 9){
                            tens = 9
                            ones = 9
                        }
                    } else{
                        tens = 0
                        ones = r
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
                        j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        212,
                        9,
                        9
                    )
                    if (tens>0) this.drawTexture(
                        matrices,
                        p + 1 + tensOfst,
                        j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                        108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                        212,
                        9,
                        9
                    )

                }
                //this.drawTexture(matrices, p + 1, j + 15 + ofst2 + 19 * o, 108 + 16 * (o+o_ofst), 174, 16, 16)
                textRenderer.drawTrimmed(stringVisitable, q, j + 16 + ofst2 + 19 * o, s, t)
                t = 8453920
            }

            textRenderer.drawWithShadow(
                matrices,
                string,
                (q + 86 - textRenderer.getWidth(string)).toFloat(),
                (j + 16 + 19 * o + 7).toFloat(),
                t
            )
        }
    }


    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        @Suppress("SpellCheckingInspection")
        val dlta = client?.tickDelta?:delta
        val ofst2 = 4 //ofst to handle the screen height change
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, dlta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        val bl = player.abilities.creativeMode
        val i = (handler as ImbuingTableScreenHandler).getLapisCount()
        for (j in 0..2) {

            val k = handler.enchantmentPower[j]
            val l = handler.enchantmentLevel[j]
            var m = j + 1
            when (k) {
                in 31..40-> m = 4
                in 41..50-> m = 5
                in 51..60-> m = 6
            }
            if (!isPointWithinBounds(
                    118,
                    14 + ofst2 + 19 * j,
                    108,
                    17,
                    mouseX.toDouble(),
                    mouseY.toDouble()
                ) || k <= 0 || l < 0
            ) continue
            val list = Lists.newArrayList<Text>()
            if (handler.imbueId[j] == 0 && handler.modId[j] == 0){
                val enchantment = if (handler.levelLow[j] > 0){
                    list.add(TranslatableText("container.imbuing_table.level_low").formatted(Formatting.RED))
                    Enchantment.byRawId(handler.levelLow[j])
                } else if (handler.levelLow[j] > 0) {
                    list.add(TranslatableText("container.imbuing_table.scepter_low").formatted(Formatting.RED))
                    Enchantment.byRawId(handler.levelLow[j] * -1)
                }else {
                    Enchantment.byRawId(handler.enchantmentId[j])
                }
                if (enchantment != null) {
                    list.add(
                        TranslatableText(
                            "container.enchant.clue",
                            enchantment.getName(l)
                        ).formatted(Formatting.WHITE)
                    )
                } else{
                    continue
                }
                if (handler.levelLow[j] == 0){
                    if (!bl) {
                        list.add(LiteralText.EMPTY)
                        if (player.experienceLevel < k) {
                            list.add(
                                TranslatableText(
                                    "container.enchant.level.requirement",
                                    handler.enchantmentPower[j]
                                ).formatted(Formatting.RED)
                            )
                        } else {
                            val mutableText = if (m == 1) TranslatableText("container.enchant.lapis.one") else TranslatableText(
                                "container.enchant.lapis.many",
                                m
                            )
                            list.add(mutableText.formatted(if (i >= m) Formatting.GRAY else Formatting.RED))
                            val mutableText2 =
                                if (m == 1) TranslatableText("container.enchant.level.one") else TranslatableText(
                                    "container.enchant.level.many",
                                    m
                                )
                            list.add(mutableText2.formatted(Formatting.GRAY))
                        }
                    }
                }
            } else {
                if(handler.imbueId[j] < 0) {
                    val textName: MutableText = Registry.ITEM.get(handler.imbueId[j]*-1).name as MutableText
                    //println(textName.toString())
                    list.add(TranslatableText("container.enchant.clue", textName.formatted(Formatting.WHITE)))
                    list.add(LiteralText.EMPTY)
                } else if (handler.imbueId[j] > 0){
                    if (Registry.ENCHANTMENT.get(handler.imbueId[j]) != null) {
                        val textName: MutableText =
                            Registry.ENCHANTMENT.get(handler.imbueId[j])?.getName(1) as MutableText
                        //println(textName.toString())
                        list.add(TranslatableText("container.enchant.clue", textName.formatted(Formatting.WHITE)))
                        list.add(LiteralText.EMPTY)

                    }
                }
                if(handler.modId[j] > 0) {
                    val id = ModifierRegistry.getIdByRawId(handler.modId[j])
                    val textName: MutableText = TranslatableText("scepter.modifiers.${id}")
                    //println(textName.toString())
                    list.add(TranslatableText("container.enchant.clue", textName.formatted(Formatting.WHITE)))
                    list.add(LiteralText.EMPTY)
                } else if(handler.modId[j] < 0){
                    val id = ModifierRegistry.getIdByRawId(handler.modId[j] * -1)
                    val textName: MutableText = (ModifierRegistry.get(id)?.getName()?:LiteralText.EMPTY) as MutableText
                    list.add(TranslatableText("container.imbuing_table.modifier_max").formatted(Formatting.RED))
                    list.add(TranslatableText("container.enchant.clue", textName.formatted(Formatting.WHITE)))
                    list.add(LiteralText.EMPTY)
                }
                if (player.experienceLevel < k) {
                    list.add(
                        TranslatableText(
                            "container.enchant.level.requirement",
                            handler.enchantmentPower[j]
                        ).formatted(Formatting.RED)
                    )
                } else{
                    val mutableText2 =
                        if (k == 1) TranslatableText("container.enchant.level.one") else TranslatableText(
                            "container.enchant.level.many",
                            k
                        )
                    list.add(mutableText2.formatted(Formatting.GRAY))
                }
            }
            this.renderTooltip(matrices, list, mouseX, mouseY)
            break
        }
    }


    init{
        this.backgroundWidth = backgrdWidth
        this.backgroundHeight = backgrdHeight
        super.init()
        //x = (width - backgrdWidth) / 2
        //y = (height - backgrdHeight) / 2
        titleX = 30+29-14
        titleY = 5
        playerInventoryTitleX = 8+29
        playerInventoryTitleY = this.backgrdHeight-94
    }


}