package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.item.TooltipData
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import java.util.*

open class ImbuingRecipeBaseScreen(title: Text): Screen(title) {

    protected val context: TooltipContext by lazy{
        if(client?.options?.advancedItemTooltips == true){
            TooltipContext.Default.ADVANCED
        } else {
            TooltipContext.Default.BASIC
        }
    }

    protected fun reformatText(text: Text): Text{
        val mutableText = text.copy()
        val style = text.style
        val color = style.color ?: return mutableText.setStyle(style.withColor(Formatting.DARK_GRAY))
        if (color == TextColor.fromFormatting(Formatting.WHITE)){
            return mutableText.setStyle(style.withColor(Formatting.DARK_GRAY))
        }
        if (color == TextColor.fromFormatting(Formatting.GREEN)){
            return mutableText.setStyle(style.withColor(Formatting.DARK_GREEN))
        }
        return text
    }

    protected fun addText(matrices: MatrixStack, text: Text, x: Float, y: Float, rowOffset: Int, centered: Boolean = false,maxRows: Int = 0, width: Int = 107): Float{
        val rawLines = textRenderer.wrapLines(text,width)
        val widthTextList = if(maxRows > 0){
            if (rawLines.size > maxRows){
                val temp = mutableListOf<OrderedText>()
                for (i in 0 until maxRows - 1){
                    temp.add(rawLines[i])
                }
                temp.add(AcText.translatable("lore_book.screen.too_much_text").styled { text.style }.asOrderedText())
                temp
            } else {
                rawLines
            }
        } else {
            rawLines
        }

        var curY = y
        if (centered){
            for (orderedText in widthTextList) {
                DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer,orderedText, x.toInt(), curY.toInt(),0x404040)
                curY += rowOffset
            }
            return curY
        }
        for (orderedText in widthTextList) {
            textRenderer.draw(matrices,orderedText,x,curY,0xFFFFFF)
            curY += rowOffset
        }
        return curY
    }

    protected fun renderItem(matrices: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int, stack: ItemStack){
        itemRenderer.renderInGuiWithOverrides(client?.player, stack, x, y, x + y * 256)
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, null)
        renderBookTooltip(matrices, mouseX, mouseY, x, y, stack)
    }

    protected fun renderBookTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int, x: Int, y: Int, width: Int, height: Int,
                                  lines: List<Text> = listOf(), data: Optional<TooltipData> = Optional.empty<TooltipData>()): Boolean{
        if (mouseX - x in 0..width){
            if (mouseY - y in 0..height){
                renderTooltip(matrices, lines, data, mouseX, mouseY)
                return true
            }
        }
        return false
    }
    protected fun renderBookTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int, x: Int, y: Int, stack: ItemStack): Boolean{
        if (stack.isEmpty) return false
        return renderBookTooltip(matrices, mouseX, mouseY,x,y,15,15,stack.getTooltip(null,context), stack.tooltipData)

    }

}