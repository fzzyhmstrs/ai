package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfKnowledge
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil.buildOutputProvider
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.item.TooltipData
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class KnowledgeBookScreen(private val book: ItemStack, private val player: PlayerEntity): Screen(AcText.translatable("lore_book.screen")) {

    private var recipe: ImbuingRecipe? = null
    private var recipeInputs: Array<RecipeUtil.StackProvider> = Array(13) { RecipeUtil.EmptyStackProvider() }
    private val recipeOutputs: RecipeUtil.StackProvider by lazy{
        recipe?.let { buildOutputProvider(it) } ?: RecipeUtil.EmptyStackProvider()
    }
    private var tooltipList: List<Text> = listOf()
    private var imbuingCost: Int = 0
    private var i: Int = 2
    private var j: Int = 2
    private val context: TooltipContext by lazy{
        if(client?.options?.advancedItemTooltips == true){
        TooltipContext.Default.ADVANCED
        } else {
            TooltipContext.Default.NORMAL
        }
    }
    private val item = book.item
    private val bindingUV = if (item is BookOfKnowledge){
        item.bindingUV
    } else {
        Pair(230,230)
    }
    private var bookmarkUV = Pair(230,230)

    override fun init() {
        super.init()
        if (item !is AbstractAugmentBookItem){
            this.close()
            return
        }
        val nbt = book.nbt
        if (nbt == null || !nbt.contains(NbtKeys.LORE_KEY.str())){
            this.close()
            return
        }
        val list: MutableList<Text> = mutableListOf()
        item.appendTooltip(book,client?.world,list, TooltipContext.Default.NORMAL)
        if (list.isEmpty()){
            this.close()
            return
        }
        tooltipList = list
        val augId = Identifier(Nbt.readStringNbt(NbtKeys.LORE_KEY.str(),nbt)).toString()
        bookmarkUV = AugmentHelper.getAugmentType(augId).uv()
        val recipeId = augId + "_imbuing"
        val recipeCheck = client?.world?.recipeManager?.get(Identifier(recipeId))
        if (recipeCheck == null){
            this.close()
            return
        }
        if (recipeCheck.isPresent){
            val recipeCheck2 = recipeCheck.get()
            if (recipeCheck2 is ImbuingRecipe){
                recipe = recipeCheck2
                val list2 = RecipeUtil.buildInputProviders(recipeCheck2)
                recipeInputs = list2.toTypedArray()
                imbuingCost = recipeCheck2.getCost()
            }
        }

        i = (width - 256)/2
        j = (height - 179)/2
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        //the book background
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE)
        drawTexture(matrices, i, j, 0, 0, 256, 179)
        //the bookmark
        drawTexture(matrices,i+246,j+52,bookmarkUV.first,bookmarkUV.second,15,19)
        //the bindings
        drawTexture(matrices,i+8,j+28,bindingUV.first,bindingUV.second,4,14)
        drawTexture(matrices,i+244,j+28,bindingUV.first + 4,bindingUV.second,4,14)
        drawTexture(matrices,i+8,j+132,bindingUV.first + 8,bindingUV.second,5,14)
        drawTexture(matrices,i+243,j+132,bindingUV.first + 13,bindingUV.second,5,14)
        //the recipe title on the right page
        DrawableHelper.drawCenteredText(matrices,textRenderer,AcText.translatable("lore_book.screen").formatted(Formatting.GOLD),i + 187,j+11,0x404040)
        //the recipe cost on the right page
        val imbueCost = AcText.translatable("lore_book.screen.cost",imbuingCost.toString()).formatted(Formatting.GREEN)
        DrawableHelper.drawCenteredText(matrices,textRenderer,imbueCost,i + 187,j+30,0x404040)
        val imbueWidth = textRenderer.getWidth(imbueCost)

        val tooltips: MutableList<TooltipBox> = mutableListOf()
        tooltips.add(TooltipBox(i + 187-(imbueWidth/2),j+30,imbueWidth,9, COST_HINT))
        var offset = j + 11f
        var oldOffset: Float
        val x = i+17f

        val augmentTitle = tooltipList.getOrElse(0) { AcText.empty() }
        offset = addText(matrices,reformatText(augmentTitle),i+67f,offset,11,true)
        //drawing a horizontal line break
        offset += 2
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE)
        drawTexture(matrices, i+15, offset.toInt(), 0, 179, 105, 4)
        offset += 6
        //draw the enchantment desc
        val desc = tooltipList.getOrElse(1) { AcText.empty() }
        offset = addText(matrices,reformatText(desc),x,offset,11, width = 111)
        offset = renderLine(matrices,x.toInt(),offset)
        //spell type
        val type = tooltipList.getOrElse(2) { AcText.empty() }
        oldOffset = offset
        offset = addText(matrices,reformatText(type),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), TYPE_HINT))
        offset = renderLine(matrices,x.toInt(),offset)
        //minimum XP level (skipping key item because recipe)
        val listDiff = if(tooltipList.size == 7) -1 else 0
        //cooldown text
        val cooldown = tooltipList.getOrElse(5 + listDiff) { AcText.empty() }
        oldOffset = offset
        offset = addText(matrices,reformatText(cooldown),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), COOLDOWN_HINT))
        offset = renderLine(matrices,x.toInt(),offset)
        //mana cost text
        val cost = tooltipList.getOrElse(6 + listDiff) { AcText.empty() }
        oldOffset = offset
        offset = addText(matrices,reformatText(cost),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), MANA_HINT))
        offset = renderLine(matrices,x.toInt(),offset)
        //tier text
        if (tooltipList.size > 6) {
            val tier = tooltipList.getOrElse(7 + listDiff) { AcText.empty() }
            oldOffset = offset
            offset = addText(matrices, reformatText(tier), x, offset, 11)
            tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), TIER_HINT))
        }
        //the 13 inputs, 0 to 12
        renderItem(matrices,i+138,j+38,mouseX, mouseY,recipeInputs[0].getStack())
        renderItem(matrices,i+222,j+38,mouseX, mouseY,recipeInputs[1].getStack())
        renderItem(matrices,i+161,j+48,mouseX, mouseY,recipeInputs[2].getStack())
        renderItem(matrices,i+180,j+48,mouseX, mouseY,recipeInputs[3].getStack())
        renderItem(matrices,i+199,j+48,mouseX, mouseY,recipeInputs[4].getStack())
        renderItem(matrices,i+161,j+67,mouseX, mouseY,recipeInputs[5].getStack())
        renderItem(matrices,i+180,j+67,mouseX, mouseY,recipeInputs[6].getStack())
        renderItem(matrices,i+199,j+67,mouseX, mouseY,recipeInputs[7].getStack())
        renderItem(matrices,i+161,j+86,mouseX, mouseY,recipeInputs[8].getStack())
        renderItem(matrices,i+180,j+86,mouseX, mouseY,recipeInputs[9].getStack())
        renderItem(matrices,i+199,j+86,mouseX, mouseY,recipeInputs[10].getStack())
        renderItem(matrices,i+138,j+96,mouseX, mouseY,recipeInputs[11].getStack())
        renderItem(matrices,i+222,j+96,mouseX, mouseY,recipeInputs[12].getStack())
        //the output item
        renderItem(matrices,i+180,j+141,mouseX, mouseY,recipeOutputs.getStack())
        //draw tooltips as applicable to the current mouse pos
        for (it in tooltips) {
            if (renderBookTooltip(matrices,mouseX,mouseY,it.x,it.y,it.width,it.height,it.hint)) break
        }
    }

    private fun reformatText(text: Text): Text{
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

    private fun addText(matrices: MatrixStack,text: Text,x: Float,y: Float, rowOffset: Int, centered: Boolean = false, width: Int = 107): Float{
        val widthTextList = textRenderer.wrapLines(text,width)

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

    private fun renderItem(matrices: MatrixStack,x: Int, y: Int, mouseX: Int, mouseY: Int, stack: ItemStack){
        itemRenderer.renderInGuiWithOverrides(client?.player, stack, x, y, x + y * 256)
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, null)
        renderBookTooltip(matrices, mouseX, mouseY, x, y, stack)
    }

    private fun renderBookTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int,x: Int, y: Int, width: Int, height: Int,
                                  lines: List<Text> = listOf(),data: Optional<TooltipData> = Optional.empty<TooltipData>()): Boolean{
        if (mouseX - x in 0..width){
            if (mouseY - y in 0..height){
                renderTooltip(matrices, lines, data, mouseX, mouseY)
                return true
            }
        }
        return false
    }
    private fun renderBookTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int, x: Int, y: Int, stack: ItemStack): Boolean{
        if (stack.isEmpty) return false
        return renderBookTooltip(matrices, mouseX, mouseY,x,y,15,15,stack.getTooltip(player,context), stack.tooltipData)

    }

    private fun renderLine(matrices: MatrixStack,x: Int,offset: Float): Float{
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE)
        drawTexture(matrices,x,(offset).toInt()-1,0,183,50,1)
        return offset + 3
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        super.resize(client, width, height)
        i = (width - 256)/2
        j = (height - 179)/2
    }

    override fun shouldPause(): Boolean {
        return false
    }

    internal class TooltipBox(val x: Int, val y: Int, val width: Int, val height: Int, val hint: List<Text>)


    companion object{
        internal val BOOK_TEXTURE = Identifier(AI.MOD_ID,"textures/gui/knowledge_book.png")
        private val COST_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.cost_hint1"),
            AcText.translatable("lore_book.screen.cost_hint2")
        )
        private val TYPE_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.type_hint1"),
            AcText.translatable("lore_book.screen.type_hint2"),
            AcText.translatable("lore_book.screen.type_hint3"),
            AcText.empty(),
            AcText.translatable("lore_book.screen.type_hint4").formatted(Formatting.ITALIC)
        )
        private val COOLDOWN_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.cooldown_hint1"),
            AcText.translatable("lore_book.screen.cooldown_hint2")
        )
        private val MANA_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.mana_hint1")
        )
        private val TIER_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.tier_hint1")
        )
    }


}