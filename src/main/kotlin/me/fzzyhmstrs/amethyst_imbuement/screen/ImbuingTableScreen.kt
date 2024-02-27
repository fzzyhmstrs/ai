package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper.runHandlerViewer
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.max

@Environment(value = EnvType.CLIENT)
class ImbuingTableScreen(handler: ImbuingTableScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<ImbuingTableScreenHandler>(handler, playerInventory, title) {

    private val texture = Identifier(AI.MOD_ID,"textures/gui/container/imbuing_table_gui.png")
    private val backgrdWidth = 234
    private val backgrdHeight = 174
    private val player = playerInventory.player
    private val previousRecipe = AcText.literal(AcText.translatable("container.imbuing_table.previous_recipe").string).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")).withItalic(true))
    private val nextRecipe = AcText.literal(AcText.translatable("container.imbuing_table.next_recipe").string).fillStyle(Style.EMPTY.withFont(Identifier("minecraft", "default")).withItalic(true))
    private val recipesOffset = ModCompatHelper.getScreenHandlerOffset()

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        return mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + backgrdWidth).toDouble() || mouseY >= (top + backgrdHeight).toDouble()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2

        for (k in 0..2) {
            val m = if (k == 0 && handler.resultsCanUp){
                3
            } else if (k == 2 && handler.resultsCanDown){
                4
            } else {
                k
            }
            val d = mouseX - (i + 118).toDouble()
            val e = mouseY - (j + 14 + 4 + 19 * k).toDouble()
            if (d < 0.0 || e < 0.0 || d >= 108.0 || e >= 19.0 || !player.let {
                    (handler as ImbuingTableScreenHandler).onButtonClick(
                        it, m
                    )
                }
            ) continue
            client?.interactionManager?.clickButton(handler.syncId, m)
            return true
        }
        if(recipesOffset >= 0) {
            val d = mouseX - (i + 6).toDouble()
            val e = mouseY - (j + 91).toDouble()
            if (!(d < 0.0 || e < 0.0 || d >= 20.0 || e >= 18.0)) {
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
                runHandlerViewer(recipesOffset)
                return true
            }
        }
        if(ModCompatHelper.isPatchouliLoaded()) {
            val d = mouseX - (i + 6).toDouble()
            val e = mouseY - (j + 111).toDouble()
            if (!(d < 0.0 || e < 0.0 || d >= 20.0 || e >= 18.0)) {
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
                ModCompatHelper.openBookEntry(AI.identity("altars/imbuing_table"))
                return true
            }
        }
        if (AiConfig.blocks.imbuing.getRerollEnabled()){
            val d = mouseX - (i + 94).toDouble()
            val e = mouseY - (j + 37).toDouble()
            if (!(d < 0.0 || e < 0.0 || d >= 18.0 || e >= 18.0 || !player.let {
                    (handler as ImbuingTableScreenHandler).onButtonClick(
                        it, 5
                    )
                })) {
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1f))
                client?.interactionManager?.clickButton(handler.syncId, 5)
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }


    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2
        val ofst2 = 4 //ofst to handle the screen height change
        context.drawTexture(this.texture, i, j, 0, 0, backgrdWidth, backgrdHeight)
        if (handler.lapisSlot.get() > 0){
            context.drawTexture(this.texture,i+72,j+38,8,13,16,16)
        }
        /*val k = client?.window?.scaleFactor?.toInt()?:1
        RenderSystem.viewport((width - 320) / 2 * k, (height - 240) / 2 * k, 320 * k, 240 * k)
        val matrix4f = Matrix4f().translation(-0.34f, 0.23f, 0.0f).perspective(1.5707964f, 1.3333334f, 9.0f, 80.0f)
        RenderSystem.backupProjectionMatrix()
        RenderSystem.setProjectionMatrix(matrix4f)

        client?.window?.framebufferWidth?.let { client?.window?.framebufferHeight?.let { it1 ->
            RenderSystem.viewport(0, 0, it,
                it1
            )
        } }

        RenderSystem.restoreProjectionMatrix()
        DiffuseLighting.enableGuiDepthLighting()*/
        EnchantingPhrases.getInstance().setSeed((handler as ImbuingTableScreenHandler).getSeed().toLong())
        for (o in 0..2) {
            val p = i + 118 //offset from left edge of enchantment boxes
            val q = p + 20
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            val r = handler.resultsIndexes[o]
            val result = handler.results.getOrElse(r) { ImbuingTableScreenHandler.EmptyResult() }
            val power = result.power
            if (r == -1 || result.type == 3 || power == 0) {
                //draws the "browned out" no-entry box
                context.drawTexture(this.texture, p, j + 14 + ofst2 + 19 * o, 0, 193, 108, 19)
                continue //jumps to the next enchantment in the list
            }
            val string = "" + power
            val s :Int
            val stringVisitable = if((handler.resultsCanUp && o == 0)){
                s = 86
                previousRecipe
            } else if( (handler.resultsCanDown && o == 2)){
                s = 86
                nextRecipe
            } else {
                s = 86 - textRenderer.getWidth(string)
                result.buttonStringVisitable(textRenderer, s)
            }

            var tens: Int
            var ones: Int
            var tensOfst = 1
            var onesOfst = 8
            var tensImageOfst: Int
            var onesImageOfst = 9

            val u = mouseX - (i + 118)
            val v = mouseY - (j + 14 + ofst2 + 19 * o)
            val hovered = (u >= 0 && v >= 0 && u < 108 && v < 19)

            val lapisOffset = max(0,result.lapis - 1)
            val lapisOffset2: Int
            val buttonOffset: Int
            val buttonHoveredOffset: Int
            val numeralOffset: Int
            val powerTextColor: Int
            val descTextColor: Int

            if (!result.isReady(o, player, handler)) {
                buttonOffset = 193
                lapisOffset2 = 190
                buttonHoveredOffset = 193
                numeralOffset = 222
                powerTextColor = 4226832
                descTextColor = 6839882
            } else {
                buttonOffset = 174
                lapisOffset2 = 174
                buttonHoveredOffset = 212
                numeralOffset = 212
                powerTextColor = 8453920
                descTextColor = if(hovered){
                    0xFFFF80
                } else {
                    6839882
                }
            }

            if (hovered) {
                context.drawTexture(this.texture, p, j + 14 + ofst2 + 19 * o, 0, buttonHoveredOffset, 108, 19)
            } else {
                context.drawTexture(this.texture, p, j + 14 + ofst2 + 19 * o, 0, buttonOffset, 108, 19)
            }
            if (o == 0 && handler.resultsCanUp){
                context.drawTexture(this.texture,
                    p + 1,
                    j + 15 + ofst2 + 19 * o,
                    if(hovered){32} else {0},
                    231,
                    16,
                    16
                )
            } else if (o == 2 && handler.resultsCanDown){
                context.drawTexture(this.texture,
                    p + 1,
                    j + 15 + ofst2 + 19 * o,
                    if(hovered){48} else {16},
                    231,
                    16,
                    16
                )
            } else if ((lapisOffset) <= 5) {
                context.drawTexture(this.texture,
                    p + 1,
                    j + 15 + ofst2 + 19 * o,
                    108 + 16 * (lapisOffset),
                    lapisOffset2,
                    16,
                    16
                )
            } else {
                //draw in an empty level 6 experience orb picture as a background for the numbers
                context.drawTexture(this.texture,
                    p + 1,
                    j + 15 + ofst2 + 19 * o,
                    108 + 16 * 6,
                    174 + 16,
                    16,
                    16
                )

                //split the needed levels into tens place and one's place. Integer division to get the tens place, then subtract out to get the remainder
                if (power > 9){
                    tens = power/10
                    ones = power - tens * 10
                    if(tens > 9){
                        tens = 9
                        ones = 9
                    }
                } else{
                    tens = 0
                    ones = power
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
                context.drawTexture(this.texture,
                    p + 1 + onesOfst,
                    j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                    108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )
                if (tens>0) context.drawTexture(this.texture,
                    p + 1 + tensOfst,
                    j + 15 + ofst2 + 3 + 19 * o, //three additional offset to align the number with the usual position
                    108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )

            }
            val ofst3 = if (textRenderer.getWidth(stringVisitable) > s){
                -1
            } else if(o == 0 && handler.resultsCanUp|| o == 2 && handler.resultsCanDown){
                3
            } else {
                0
            }
            context.drawTextWrapped(textRenderer,stringVisitable, q, j + 16 + ofst2 + ofst3 + 19 * o, s, descTextColor and 0xFEFEFE shr 1)
            if (!(o == 0 && handler.resultsCanUp || o == 2 && handler.resultsCanDown))
            context.drawTextWithShadow(textRenderer,
                string,
                (q + 86 - textRenderer.getWidth(string)),
                (j + 16 + 19 * o + 7),
                powerTextColor
            )
        }
        if (recipesOffset >= 0){
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            val u = mouseX - (i + 6)
            val v = mouseY - (j + 91)
            val hovered = (u >= 0 && v >= 0 && u < 20 && v < 18)
            context.drawTexture(this.texture,
                i + 6,
                j + 91,
                236,
                if(hovered){18 + 36 * recipesOffset} else {0 + 36*recipesOffset},
                20,
                18
            )
        }
        if (ModCompatHelper.isPatchouliLoaded()){
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            val u = mouseX - (i + 6)
            val v = mouseY - (j + 111)
            val hovered = (u >= 0 && v >= 0 && u < 20 && v < 18)
            context.drawTexture(this.texture,
                i + 6,
                j + 111,
                236,
                if(hovered){18 + 144} else {144},
                20,
                18
            )
        }
        if (handler.reroll.get() != 0){
            val offset = if (handler.reroll.get() > 0) {
                val u = mouseX - (i + 94)
                val v = mouseY - (j + 37)
                val hovered = (u >= 0 && v >= 0 && u < 18 && v < 18)
                if (hovered){
                    38
                } else {
                    0
                }
            } else {
                19
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            context.drawTexture(this.texture,
                i + 94,
                j + 37,
                64 + offset,
                231,
                18,
                18
            )
        }
    }


    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
        if (isPointWithinBounds(94, 37, 18, 18, mouseX.toDouble(), mouseY.toDouble()) && handler.reroll.get() != 0){
            val list = if(handler.reroll.get() > 0){
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.reroll"))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.reroll_lapis",AiConfig.blocks.imbuing.getLapisCost()).formatted(Formatting.ITALIC, Formatting.BLUE))
                tempList.add(AcText.translatable("container.imbuing_table.reroll_levels",AiConfig.blocks.imbuing.getLevelCost()).formatted(Formatting.ITALIC, Formatting.GREEN))
                tempList
            } else if (handler.reroll.get() == -1) {
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.reroll").formatted(Formatting.RED))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.reroll_lapis_bad").formatted(Formatting.ITALIC, Formatting.RED))
                tempList
            } else if (handler.reroll.get() == -2) {
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.reroll").formatted(Formatting.RED))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.reroll_levels_bad").formatted(Formatting.ITALIC, Formatting.RED))
                tempList
            } else {
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.reroll").formatted(Formatting.RED))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.reroll_both_bad").formatted(Formatting.ITALIC, Formatting.RED))
                tempList
            }
            context.drawTooltip(textRenderer, list, mouseX, mouseY)
            return
        }
        for (j in 0..2) {
            val r = handler.resultsIndexes[j]
            if (r < 0) continue
            val result = handler.results.getOrNull(r)?:continue
            if (result.type == -2) continue
            if (!isPointWithinBounds(118, 14 + 4 + 19 * j, 108, 17, mouseX.toDouble(), mouseY.toDouble())) continue
            val list = if(j == 0 && handler.resultsCanUp){
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.previous_recipe"))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.next_recipe_1",result.nextRecipeTooltipText(player, handler)).formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
                tempList
            } else if (j == 2 && handler.resultsCanDown){
                val tempList: MutableList<Text> = mutableListOf()
                tempList.add(AcText.translatable("container.imbuing_table.next_recipe"))
                tempList.add(AcText.empty())
                tempList.add(AcText.translatable("container.imbuing_table.next_recipe_1",result.nextRecipeTooltipText(player, handler)).formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
                tempList
            } else {
                result.tooltipList(player, handler)
            }
            context.drawTooltip(textRenderer, list, mouseX, mouseY)
            break
        }

    }

    override fun init() {
        this.backgroundWidth = backgrdWidth
        this.backgroundHeight = backgrdHeight
        //x = (width - backgrdWidth) / 2
        //y = (height - backgrdHeight) / 2
        titleX = 30+29-14
        titleY = 5
        playerInventoryTitleX = 8+29
        playerInventoryTitleY = this.backgrdHeight-94
        super.init()
        handler.requestContent()
    }


}