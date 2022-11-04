package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil.buildOutputProvider
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.item.TooltipData
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import java.util.*
import java.util.function.Consumer
import kotlin.math.ceil

class ImbuingRecipeBookScreen(private val oldScreen: HandledScreen<*>): Screen(AcText.translatable("")) {

    private var i: Int = 2
    private var j: Int = 2
    internal var currentRecipes: List<ImbuingRecipe> = listOf()
    internal var recipeIndex = -1
    private val tooltips: MutableList<KnowledgeBookScreen.TooltipBox> = mutableListOf()
    private var recipeInputs: Array<RecipeUtil.StackProvider> = Array(13) { RecipeUtil.EmptyStackProvider() }
    private var recipeOutputs: RecipeUtil.StackProvider = RecipeUtil.EmptyStackProvider()
    private var imbuingCost: Int = 0

    private val leftItemButtonAction = PressAction {
        currentItemPage = if (currentItemPage == 0) maxPageIndex else currentItemPage - 1
        updateItemButtons()
        client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
    }
    private val rightItemButtonAction = PressAction {
        currentItemPage = if (currentItemPage == maxPageIndex) 0 else currentItemPage + 1
        updateItemButtons()
        client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
    }

    private val leftRecipeButtonAction = PressAction {
        if (recipeIndex > -1){
            recipeIndex = if (recipeIndex == 0) currentRecipes.lastIndex else recipeIndex - 1
            updateRecipe()
            client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
        }
    }
    private val leftRecipeButton = TexturedButtonWidget(i+145,j+152,20,12,0,203,12,KnowledgeBookScreen.BOOK_TEXTURE,leftRecipeButtonAction)

    private val rightRecipeButtonAction = PressAction {
        if (recipeIndex > -1){
            recipeIndex = if (recipeIndex == currentRecipes.lastIndex) 0 else recipeIndex + 1
            updateRecipe()
            client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
        }
    }
    private val rightRecipeButton = TexturedButtonWidget(i+211,j+152,20,12,0,203,12,KnowledgeBookScreen.BOOK_TEXTURE,rightRecipeButtonAction)

    private var itemButtons: Array<ItemButtonWidget> = arrayOf()

    internal val context: TooltipContext by lazy{
        if(client?.options?.advancedItemTooltips == true){
            TooltipContext.Default.ADVANCED
        } else {
            TooltipContext.Default.NORMAL
        }
    }

    override fun init() {
        super.init()
        if (maxPageIndex < 0) this.close()
        i = (width - 256)/2
        j = (height - 179)/2
        if (maxPageIndex > 0)
        this.addDrawableChild(TexturedButtonWidget(i+22,j+152,20,12,22,203,12,KnowledgeBookScreen.BOOK_TEXTURE,leftItemButtonAction))
        this.addDrawableChild(TexturedButtonWidget(i+94,j+152,20,12,0,203,12,KnowledgeBookScreen.BOOK_TEXTURE,rightItemButtonAction))
        itemButtons = Array(20) { index -> generateWidget(index) }
        itemButtons.forEach {
            this.addDrawableChild(it)
        }

    }

    private fun generateWidget(index: Int): ItemButtonWidget{
        val xOffset = index % 4
        val yOffset = index / 4
        val buttonX = xOffset * 24 + i + 20
        val buttonY = yOffset * 24 + j + 29
        val entry = getItemEntry(index)
        return ItemButtonWidget(buttonX, buttonY, entry, this)
    }

    private fun updateItemButtons(){
        for (i in 0 until 20){
            itemButtons[i].updateEntry(getItemEntry(i))
        }
    }

    internal fun updateRecipeButtons(){
        this.remove(leftRecipeButton)
        this.remove(rightRecipeButton)
        if (currentRecipes.size > 1){
            this.addDrawableChild(leftRecipeButton)
            this.addDrawableChild(rightRecipeButton)
        }
    }

    private fun updateRecipe(){
        if (recipeIndex == -1) return
        val recipe = currentRecipes[recipeIndex]
        val list2 = RecipeUtil.buildInputProviders(recipe)
        recipeInputs = list2.toTypedArray()
        recipeOutputs = buildOutputProvider(recipe)
        imbuingCost = recipe.getCost()
    }

    override fun close() {
        client?.setScreen(oldScreen)
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        super.resize(client, width, height)
        i = (width - 256)/2
        j = (height - 179)/2
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
        drawTexture(matrices, i, j, 0, 0, 256, 179)
        //drawables are rendered here
        super.render(matrices, mouseX, mouseY, delta)
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
        for (it in tooltips) {
            if (renderBookTooltip(matrices,mouseX,mouseY,it.x,it.y,it.width,it.height,it.hint)) break
        }
        tooltips.clear()
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
        return renderBookTooltip(matrices, mouseX, mouseY,x,y,15,15,stack.getTooltip(null,context), stack.tooltipData)

    }

    private class ItemButtonWidget(x: Int, y: Int, var entry: ItemEntry, screen: ImbuingRecipeBookScreen):
        ButtonWidget(
            x,
            y,
            24,
            24,
            AcText.empty(),
            PressAction {
                screen.recipeIndex = if (entry.list.isEmpty()) -1 else 0
                screen.currentRecipes = entry.list
                screen.updateRecipeButtons()
                screen.updateRecipe()
                screen.client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
            },
            object : TooltipSupplier{
                override fun onTooltip(button: ButtonWidget?, matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
                    screen.tooltips.add(KnowledgeBookScreen.TooltipBox(x,y,24,24,entry.stack.getTooltip(null, screen.context)))
                }
                override fun supply(consumer: Consumer<Text>) {
                    consumer.accept(entry.stack.name)
                }
            }
        ){

        private val client = MinecraftClient.getInstance()

        init{
            this.visible = !entry.empty
        }

        fun updateEntry(newEntry: ItemEntry){
            entry = newEntry
            this.visible = !entry.empty
        }

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
            var xOffset = 0
            if (this.isHovered) {
                xOffset += 24
            }
            RenderSystem.enableDepthTest()
            drawTexture(matrices, x, y, xOffset.toFloat(), 227f, width, height, 24, 24)
            if (hovered) {
                renderTooltip(matrices, mouseX, mouseY)
            }
            client.itemRenderer.renderInGuiWithOverrides(client?.player, entry.stack, x + 4, y + 4, x + y * 256)
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, entry.stack, x + 4, y + 4, null)
        }

    }

    companion object RecipeContainer{

        private var recipeList: List<ItemEntry> = listOf()
        private var pages = 0
        private var maxPageIndex = -1
        private var currentItemPage = 0


        private fun getItemEntry(index: Int): ItemEntry{
            val newIndex = currentItemPage * 20 + index
            return if (newIndex > recipeList.size) ItemEntry.EMPTY else recipeList[newIndex]
        }

        fun registerClient(){
            if (ModCompatHelper.getScreenHandlerOffset() != 0) return
            val manager = MinecraftClient.getInstance()?.world?.recipeManager?:return
            val tempMap: MutableMap<ItemStack,MutableList<ImbuingRecipe>> = mutableMapOf()
            val imbuingList = manager.listAllOfType(ImbuingRecipe.Type)
            imbuingList.forEach { recipe ->
                val output = recipe.output
                output.count = 1
                var found = false
                for (stack in tempMap.keys) {
                    if (ItemStack.areEqual(stack,output)) {
                        tempMap[stack]?.add(recipe)
                        found = true
                        break
                    }

                }
                if (!found) {
                    tempMap[output] = mutableListOf(recipe)
                }

            }
            val tempList = tempMap.toList()
            val tempList2: MutableList<ItemEntry> = mutableListOf()
            tempList.forEach {
                tempList2.add(ItemEntry(it.first,it.second))
            }
            recipeList = tempList2
            pages = ceil(recipeList.size.toFloat()/20f).toInt()
            maxPageIndex = pages - 1
        }

        private class ItemEntry(val stack: ItemStack, val list: List<ImbuingRecipe>, val empty: Boolean = false){
            companion object{
                val EMPTY = ItemEntry(ItemStack.EMPTY,listOf(), true)
            }
        }
    }
}