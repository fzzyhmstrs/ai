package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class KnowledgeBookScreen(private val book: ItemStack): Screen(AcText.translatable("lore_book.screen")) {

    private var recipe: ImbuingRecipe? = null
    private val recipeInputs: Array<StackProvider> by lazy{
        buildInputProviders()
    }
    private val recipeOutputs: StackProvider by lazy{
        buildOutputProvider()
    }
    private var tooltipList: List<Text> = listOf()
    private var i: Int = 2
    private var j: Int = 2

    private fun buildInputProviders(): Array<StackProvider>{
        if (recipe == null){
            return Array(13) { EmptyStackProvider() }
        }
        val list: MutableList<StackProvider> = mutableListOf()
        recipe?.ingredients?.forEach {
            list.add(StackProvider.getProvider(it))
        }?: return Array<StackProvider>(13) { EmptyStackProvider() }
        return list.toTypedArray()
    }

    private fun buildOutputProvider(): StackProvider{
        if (recipe == null) return EmptyStackProvider()
        val augment = recipe?.getAugment()?:return EmptyStackProvider()
        if (augment != ""){
            val stack = ItemStack(Items.ENCHANTED_BOOK)
            val enchant = Registry.ENCHANTMENT.get(Identifier(augment))?:return EmptyStackProvider()
            EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(enchant,1))
            return SingleStackProvider(stack)
        }
        val output = recipe?.output?:return EmptyStackProvider()
        return SingleStackProvider(output)
    }

    override fun init() {
        super.init()
        val item = book.item
        if (item!is AbstractAugmentBookItem){
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
            }
        }

        i = (width - 256)/2
        j = (height - 179)/2
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE)
        //the book background
        drawTexture(matrices, i, j, 0, 0, 256, 179)
        //the recipe title on the right page
        DrawableHelper.drawCenteredText(matrices,textRenderer,title,i + 186,11,0x404040)
        //the 13 inputs, 0 to 12
        renderItem(i+138,j+38,recipeInputs[0].getStack())
        renderItem(i+222,j+38,recipeInputs[1].getStack())
        renderItem(i+161,j+48,recipeInputs[2].getStack())
        renderItem(i+180,j+48,recipeInputs[3].getStack())
        renderItem(i+199,j+48,recipeInputs[4].getStack())
        renderItem(i+161,j+67,recipeInputs[5].getStack())
        renderItem(i+180,j+67,recipeInputs[6].getStack())
        renderItem(i+199,j+67,recipeInputs[7].getStack())
        renderItem(i+161,j+86,recipeInputs[8].getStack())
        renderItem(i+180,j+86,recipeInputs[9].getStack())
        renderItem(i+199,j+86,recipeInputs[10].getStack())
        renderItem(i+138,j+96,recipeInputs[11].getStack())
        renderItem(i+222,j+96,recipeInputs[12].getStack())
        //the output item
        renderItem(i+180,j+141,recipeOutputs.getStack())
        //draw the augment title
        val augmentTitle = tooltipList.getOrElse(0) { AcText.empty() }
        DrawableHelper.drawCenteredText(matrices,textRenderer,augmentTitle,i + 65,11,0x404040)
        //draw the enchantment desc
        val desc = tooltipList.getOrElse(1) { AcText.empty() }
        val squashedDesc = textRenderer.wrapLines(desc,120)
        var currentOffset = 0
        for (orderedText in squashedDesc) {
            textRenderer.draw(matrices,orderedText,17f,23f+currentOffset,0x404040)
            currentOffset += 10
        }
        //spell type
        val typeOffset = 23f + currentOffset
        val type = tooltipList.getOrElse(2) { AcText.empty() }
        textRenderer.draw(matrices,type,17f,typeOffset,0x404040)
        //minimum XP level (skipping key item because recipe)
        val listDiff = if(tooltipList.size == 7) -1 else 0
        val xpOffset = typeOffset + 11f
        val xp = tooltipList.getOrElse(4 + listDiff) { AcText.empty() }
        textRenderer.draw(matrices,xp,17f,xpOffset,0x404040)
        //cooldown text
        val cooldownOffset = xpOffset + 11f
        val cooldown = tooltipList.getOrElse(5 + listDiff) { AcText.empty() }
        textRenderer.draw(matrices,cooldown,17f,cooldownOffset,0x404040)
        //cooldown text
        val costOffset = cooldownOffset + 11f
        val cost = tooltipList.getOrElse(6 + listDiff) { AcText.empty() }
        textRenderer.draw(matrices,cost,17f,costOffset,0x404040)
        //tier text
        val tierOffset = cooldownOffset + 11f
        val tier = tooltipList.getOrElse(7 + listDiff) { AcText.empty() }
        textRenderer.draw(matrices,tier,17f,tierOffset,0x404040)

    }

    private fun renderItem(x: Int, y: Int, stack: ItemStack){
        itemRenderer.renderInGuiWithOverrides(client?.player, stack, x, y, x + y * 256)
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, null)
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        super.resize(client, width, height)
        i = (width - 256)/2
        j = (height - 179)/2
    }

    private class MultiStackProvider(private val stacks: Array<ItemStack>): StackProvider{
        var timer = -1L
        var currentIndex = 0

        override fun getStack(): ItemStack{
            val time = System.currentTimeMillis()
            if (timer == -1L){
                timer = time
            } else if (time - timer >= 1000L){
                currentIndex++
                if (currentIndex >= stacks.size){
                    currentIndex = 0
                }
            }
            return stacks[currentIndex]
        }
    }
    private class SingleStackProvider(private val stack: ItemStack): StackProvider{
        override fun getStack(): ItemStack {
            return stack
        }

    }
    private class EmptyStackProvider(): StackProvider{
        override fun getStack(): ItemStack {
            return ItemStack.EMPTY
        }
    }
    private interface StackProvider{
        fun getStack(): ItemStack
        companion object{
            fun getProvider(ingredient: Ingredient): StackProvider{
                if (ingredient.isEmpty){
                    return EmptyStackProvider()
                }
                val stacks = ingredient.matchingStacks
                if (stacks.size == 1){
                    return SingleStackProvider(stacks[0])
                }
                return MultiStackProvider(stacks)
            }
        }
    }


    companion object{
        private val BOOK_TEXTURE = Identifier(AI.MOD_ID,"textures/gui/book.png")
    }


}