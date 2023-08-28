package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfKnowledge
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil.buildOutputProvider
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class KnowledgeBookScreen(private val book: ItemStack): ImbuingRecipeBaseScreen(AcText.translatable("lore_book.screen")) {

    private var recipe: ImbuingRecipe? = null
    private var recipeInputs: Array<RecipeUtil.StackProvider> = Array(13) { RecipeUtil.EmptyStackProvider() }
    private val recipeOutputs: RecipeUtil.StackProvider by lazy{
        recipe?.let { buildOutputProvider(it) } ?: RecipeUtil.EmptyStackProvider()
    }
    private var tooltipList: List<Text> = listOf()
    private var imbuingCost: Int = 0
    private var i: Int = 2
    private var j: Int = 2
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
        item.appendTooltip(book,client?.world,list, TooltipContext.Default.BASIC)
        if (list.isEmpty()){
            this.close()
            return
        }
        tooltipList = list
        val augId = Identifier(nbt.getString(NbtKeys.LORE_KEY.str()))
        bookmarkUV = AugmentHelper.getAugmentType(augId.toString()).uv()
        val recipeId = augId.namespace + ":imbuing/" + augId.path + "_imbuing"
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

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        //the book background
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(BOOK_TEXTURE, i, j, 0, 0, 256, 179)
        //the bookmark
        context.drawTexture(BOOK_TEXTURE,i+246,j+52,bookmarkUV.first,bookmarkUV.second,15,19)
        //the bindings
        context.drawTexture(BOOK_TEXTURE,i+8,j+28,bindingUV.first,bindingUV.second,4,14)
        context.drawTexture(BOOK_TEXTURE,i+244,j+28,bindingUV.first + 4,bindingUV.second,4,14)
        context.drawTexture(BOOK_TEXTURE,i+8,j+132,bindingUV.first + 8,bindingUV.second,5,14)
        context.drawTexture(BOOK_TEXTURE,i+243,j+132,bindingUV.first + 13,bindingUV.second,5,14)
        //the recipe title on the right page
        context.drawCenteredTextWithShadow(textRenderer,AcText.translatable("lore_book.screen").formatted(Formatting.GOLD),i + 187,j+11,0x404040)
        //the recipe cost on the right page
        val imbueCost = AcText.translatable("lore_book.screen.cost",imbuingCost.toString()).formatted(Formatting.GREEN)
        context.drawCenteredTextWithShadow(textRenderer,imbueCost,i + 187,j+30,0x404040)
        val imbueWidth = textRenderer.getWidth(imbueCost)

        val tooltips: MutableList<TooltipBox> = mutableListOf()
        tooltips.add(TooltipBox(i + 187-(imbueWidth/2),j+30,imbueWidth,9, COST_HINT))
        var offset = j + 11f
        var oldOffset: Float
        val x = i+17f

        val augmentTitle = tooltipList.getOrElse(0) { AcText.empty() }
        offset = addText(context,reformatText(augmentTitle),i+67f,offset,11,true)
        //drawing a horizontal line break
        offset += 2
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(BOOK_TEXTURE, i+15, offset.toInt(), 0, 179, 105, 4)
        offset += 6
        //draw the enchantment desc
        val desc = tooltipList.getOrElse(1) { AcText.empty() }
        val cooldown = tooltipList.getOrElse(5) { AcText.empty() }
        offset = addText(context,reformatText(desc),x,offset,11, maxRows = if(textRenderer.getWidth(cooldown) > 111) 3 else 4, width = 111)
        offset = renderLine(context,x.toInt(),offset)
        //spell type
        val type = tooltipList.getOrElse(2) { AcText.empty() }
        oldOffset = offset
        offset = addText(context,reformatText(type),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), TYPE_HINT))
        offset = renderLine(context,x.toInt(),offset)
        //cooldown text

        oldOffset = offset
        offset = addText(context,reformatText(cooldown),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), COOLDOWN_HINT))
        offset = renderLine(context,x.toInt(),offset)
        //mana cost text
        val cost = tooltipList.getOrElse(6) { AcText.empty() }
        oldOffset = offset
        offset = addText(context,reformatText(cost),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), MANA_HINT))
        //tier text
        if (tooltipList.size > 8) {
            offset = renderLine(context,x.toInt(),offset)
            val tier = tooltipList.getOrElse(7) { AcText.empty() }
            oldOffset = offset
            offset = addText(context, reformatText(tier), x, offset, 11)
            tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), TIER_HINT))
        }
        offset = renderLine(context, x.toInt(), offset)
        val lastIndex = if (tooltipList.size > 8) 8 else 7
        val castXp = tooltipList.getOrElse(lastIndex) { AcText.empty() }
        offset = addText(context,reformatText(castXp),x,offset,11)
        tooltips.add(TooltipBox(x.toInt(),oldOffset.toInt(),107,(offset-oldOffset).toInt(), CAST_XP_HINT))
        //the 13 inputs, 0 to 12
        renderItem(context,i+138,j+38,mouseX, mouseY,recipeInputs[0].getStack())
        renderItem(context,i+222,j+38,mouseX, mouseY,recipeInputs[1].getStack())
        renderItem(context,i+161,j+48,mouseX, mouseY,recipeInputs[2].getStack())
        renderItem(context,i+180,j+48,mouseX, mouseY,recipeInputs[3].getStack())
        renderItem(context,i+199,j+48,mouseX, mouseY,recipeInputs[4].getStack())
        renderItem(context,i+161,j+67,mouseX, mouseY,recipeInputs[5].getStack())
        renderItem(context,i+180,j+67,mouseX, mouseY,recipeInputs[6].getStack())
        renderItem(context,i+199,j+67,mouseX, mouseY,recipeInputs[7].getStack())
        renderItem(context,i+161,j+86,mouseX, mouseY,recipeInputs[8].getStack())
        renderItem(context,i+180,j+86,mouseX, mouseY,recipeInputs[9].getStack())
        renderItem(context,i+199,j+86,mouseX, mouseY,recipeInputs[10].getStack())
        renderItem(context,i+138,j+96,mouseX, mouseY,recipeInputs[11].getStack())
        renderItem(context,i+222,j+96,mouseX, mouseY,recipeInputs[12].getStack())
        //the output item
        renderItem(context,i+180,j+141,mouseX, mouseY,recipeOutputs.getStack())
        //draw tooltips as applicable to the current mouse pos
        for (it in tooltips) {
            if (renderBookTooltip(context,mouseX,mouseY,it.x,it.y,it.width,it.height,it.hint)) break
        }
    }

    private fun renderLine(context: DrawContext,x: Int,offset: Float): Float{
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(BOOK_TEXTURE,x,(offset).toInt()-1,0,183,50,1)
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
        internal val COST_HINT = listOf<Text>(
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
        private val CAST_XP_HINT = listOf<Text>(
            AcText.translatable("lore_book.screen.cast_xp_hint1")
        )
    }


}