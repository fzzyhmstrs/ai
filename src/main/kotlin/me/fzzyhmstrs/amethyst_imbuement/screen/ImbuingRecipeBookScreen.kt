package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.PressAction
import net.minecraft.client.gui.widget.ButtonWidget.TooltipSupplier
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry
import java.util.function.Consumer
import kotlin.math.ceil

class ImbuingRecipeBookScreen(private val oldScreen: HandledScreen<*>): ImbuingRecipeBaseScreen(AcText.translatable("")) {

    private var i: Int = 2
    private var j: Int = 2
    internal var currentRecipes: List<ImbuingRecipe> = listOf()
    internal var currentStack: ItemStack = ItemStack.EMPTY
    internal var recipeIndex = -1
    private var recipeMatches: RecipeUtil.Result = RecipeUtil.Result.EMPTY
    private val screenBom: RecipeUtil.Bom = RecipeUtil.generate(oldScreen.screenHandler.slots)
    private val playerLevels by lazy{ client?.player?.experienceLevel?:0 }
    private val tooltips: MutableList<KnowledgeBookScreen.TooltipBox> = mutableListOf()
    private var favoritesButtonAdded = false

    private var recipeInputs: Array<RecipeUtil.StackProvider> = Array(13) { RecipeUtil.EmptyStackProvider() }
    private var recipeOutputs: RecipeUtil.StackProvider = RecipeUtil.EmptyStackProvider()
    private var recipeBom: Int2ObjectOpenHashMap<IntList> = Int2ObjectOpenHashMap()
    private var imbuingCost: Int = 0
    private var imbuingCostTooHigh = false

    private val leftItemButtonAction = PressAction {
        updateCurrentPage(false)
        updateItemButtons()
    }
    private val rightItemButtonAction = PressAction {
        updateCurrentPage(true)
        updateItemButtons()
    }
    private val leftRecipeButtonAction = PressAction {
        if (recipeIndex > -1){
            recipeIndex = if (recipeIndex == 0) currentRecipes.lastIndex else recipeIndex - 1
            updateRecipe()
        }
    }
    private val rightRecipeButtonAction = PressAction {
        if (recipeIndex > -1){
            recipeIndex = if (recipeIndex == currentRecipes.lastIndex) 0 else recipeIndex + 1
            updateRecipe()
        }
    }
    private val leftButtonTooltipSupplier = object: TooltipSupplier{
        override fun onTooltip(button: ButtonWidget, matrices: MatrixStack, mouseX: Int, mouseY: Int) {
            tooltips.add(KnowledgeBookScreen.TooltipBox(mouseX,mouseY,20,12,listOf(AcText.translatable("imbuing_recipes_book.prev"))))
        }

        override fun supply(consumer: Consumer<Text>) {
            consumer.accept(AcText.translatable("imbuing_recipes_book.prev"))
        }
    }
    private val rightButtonTooltipSupplier = object: TooltipSupplier{
        override fun onTooltip(button: ButtonWidget, matrices: MatrixStack, mouseX: Int, mouseY: Int) {
            tooltips.add(KnowledgeBookScreen.TooltipBox(mouseX,mouseY,20,12,listOf(AcText.translatable("imbuing_recipes_book.next"))))
        }

        override fun supply(consumer: Consumer<Text>) {
            consumer.accept(AcText.translatable("imbuing_recipes_book.next"))
        }
    }
    private val categoryButtonsAction: PressAction = PressAction {
        if (it !is ItemCategoryWidget) return@PressAction
        allCategoryButton.setSelected(false)
        gemCategoryButton.setSelected(false)
        scepterCategoryButton.setSelected(false)
        equipmentCategoryButton.setSelected(false)
        bookCategoryButton.setSelected(false)
        favoritesCategoryButton.setSelected(false)
        currentStack = ItemStack.EMPTY
        currentRecipes = listOf()
        recipeIndex = -1
        updateContainer(it.container)
        updateItemButtons()
        updateRecipe()
        updateRecipeButtons()
    }
    private val addRecipeButtonAction: PressAction = PressAction {
        val manager = client?.interactionManager?:return@PressAction
        val player = client?.player?:return@PressAction
        val syncId = oldScreen.screenHandler.syncId
        if (!recipeMatches.passed) return@PressAction
        val finalMap = RecipeUtil.truncate(recipeMatches.matches)
        if (finalMap.isEmpty()) return@PressAction
        val cloggedItems: MutableList<Int> = mutableListOf()
        for (i in 0..12){
            val rawId = Registry.ITEM.getRawId(oldScreen.screenHandler.slots[i].stack.item)
            if (!currentRecipes[recipeIndex].getBomList().contains(rawId)){
                cloggedItems.add(i)
            }
            if (cloggedItems.isNotEmpty()){
                val openSlots = generateOpenSlots()
                for (slot in cloggedItems){
                    if (openSlots.isNotEmpty()){
                        val newSlot = openSlots[0]
                        manager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player)
                        manager.clickSlot(syncId, newSlot, 0, SlotActionType.PICKUP, player)
                        openSlots.remove(newSlot)
                    } else {
                        manager.clickSlot(syncId, slot, 1, SlotActionType.THROW, player)
                    }
                }
            }
        }
        finalMap.forEach { (craft, input) ->
            val stack = oldScreen.screenHandler.slots[input].stack
            val count = stack.count
            manager.clickSlot(syncId, input, 0, SlotActionType.PICKUP, player)
            manager.clickSlot(syncId, craft, 1, SlotActionType.PICKUP, player)
            if (count > 1){
                manager.clickSlot(syncId, input, 0, SlotActionType.PICKUP, player)
            }
        }
        this.close()
    }
    private fun generateOpenSlots(): MutableList<Int> {
        val list: MutableList<Int> = mutableListOf()
        oldScreen.screenHandler.slots.forEachIndexed { index, it ->
            if (it.inventory is PlayerInventory){
                if (!it.hasStack()){
                    list.add(index)
                }
            }
        }
        return list
    }

    private val addRecipeTooltipSupplier: TooltipSupplier = object: TooltipSupplier{
        override fun onTooltip(button: ButtonWidget?, matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
            tooltips.add(KnowledgeBookScreen.TooltipBox(mouseX,mouseY,15,15,listOf(AcText.translatable("imbuing_recipes_book.move"))))
        }

        override fun supply(consumer: Consumer<Text>) {
            consumer.accept(AcText.translatable("imbuing_recipes_book.move"))
        }
    }
    private val addFavoriteButtonAction: PressAction = PressAction {
        if (it is AddFavoritesWidget) {
            if (it.toggled){
                addFavorite(currentStack, currentRecipes)
            } else {
                removeFavorite(currentStack)
                if (!hasFavorites){
                    updateContainer(AiItemSettings.AiItemGroup.ALL)
                    val entry = getCurrentDefaultItemEntry()
                    currentStack = entry.stack
                    currentRecipes = entry.list
                    updateItemButtons()
                    updateRecipe()
                    updateRecipeButtons()
                    this.remove(favoritesCategoryButton)
                } else if (currentContainer == AiItemSettings.AiItemGroup.FAVES){
                    val entry = getCurrentDefaultItemEntry()
                    currentStack = entry.stack
                    currentRecipes = entry.list
                    updateContainer(AiItemSettings.AiItemGroup.FAVES)
                    updateItemButtons()
                    updateRecipe()
                    updateRecipeButtons()
                }
            }
            updateFavoritesButton()
        }
    }

    private val leftRecipeButton = TexturedButtonWidget(i+145,j+152,20,12,22,203,12,KnowledgeBookScreen.BOOK_TEXTURE,256,256,leftRecipeButtonAction,leftButtonTooltipSupplier,AcText.empty())
    private val rightRecipeButton = TexturedButtonWidget(i+211,j+152,20,12,0,203,12,KnowledgeBookScreen.BOOK_TEXTURE,256,256,rightRecipeButtonAction, rightButtonTooltipSupplier, AcText.empty())

    private val categories: MutableMap<AiItemSettings.AiItemGroup, ItemCategoryWidget> = mutableMapOf()
    private val allCategoryButton by lazy{ ItemCategoryWidget(i-14,j+17, currentContainer == AiItemSettings.AiItemGroup.ALL, Items.COMPASS,AiItemSettings.AiItemGroup.ALL,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.all"),this)}
    private val gemCategoryButton by lazy{ ItemCategoryWidget(i-14,j+42,currentContainer == AiItemSettings.AiItemGroup.GEM, RegisterItem.AMETRINE,AiItemSettings.AiItemGroup.GEM,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.gem"),this)}
    private val scepterCategoryButton by lazy{ ItemCategoryWidget(i-14,j+67,currentContainer == AiItemSettings.AiItemGroup.SCEPTER, RegisterItem.SCEPTER_OF_THE_PACIFIST,AiItemSettings.AiItemGroup.SCEPTER,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.scepter"),this)}
    private val equipmentCategoryButton by lazy{ ItemCategoryWidget(i-14,j+92,currentContainer == AiItemSettings.AiItemGroup.EQUIPMENT, RegisterItem.GARNET_SWORD,AiItemSettings.AiItemGroup.EQUIPMENT,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.equipment"),this)}
    private val bookCategoryButton by lazy{ ItemCategoryWidget(i-14,j+117,currentContainer == AiItemSettings.AiItemGroup.BOOK, Items.ENCHANTED_BOOK,AiItemSettings.AiItemGroup.BOOK,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.book"),this)}
    private val favoritesCategoryButton by lazy{ ItemCategoryWidget(i-14,j+142,currentContainer == AiItemSettings.AiItemGroup.BOOK, RegisterItem.HEARTSTONE,AiItemSettings.AiItemGroup.FAVES,categoryButtonsAction,AcText.translatable("imbuing_recipes_book.category.faves"),this)}

    private val addRecipeButton = TexturedButtonWidget(i+214, j+129,15,15,116,226,15,KnowledgeBookScreen.BOOK_TEXTURE,256,256,addRecipeButtonAction,addRecipeTooltipSupplier,AcText.empty())
    private val addFavoriteButton = AddFavoritesWidget(i+147, j+129,false,addFavoriteButtonAction,this)

    private var itemButtons: Array<ItemButtonWidget> = arrayOf()

    override fun init() {
        super.init()
        if (maxPageIndex < 0) this.close()
        i = (width - 256)/2
        j = (height - 179)/2
        this.addDrawableChild(TexturedButtonWidget(i+22,j+152,20,12,22,203,12,KnowledgeBookScreen.BOOK_TEXTURE,256,256,leftItemButtonAction,leftButtonTooltipSupplier,AcText.empty()))
        this.addDrawableChild(TexturedButtonWidget(i+94,j+152,20,12,0,203,12,KnowledgeBookScreen.BOOK_TEXTURE,256,256,rightItemButtonAction,rightButtonTooltipSupplier,AcText.empty()))
        itemButtons = Array(20) { index -> generateWidget(index) }
        itemButtons.forEach {
            this.addDrawableChild(it)
        }
        this.addDrawableChild(allCategoryButton)
        categories[AiItemSettings.AiItemGroup.ALL] = allCategoryButton
        this.addDrawableChild(gemCategoryButton)
        categories[AiItemSettings.AiItemGroup.GEM] = gemCategoryButton
        this.addDrawableChild(scepterCategoryButton)
        categories[AiItemSettings.AiItemGroup.SCEPTER] = scepterCategoryButton
        this.addDrawableChild(equipmentCategoryButton)
        categories[AiItemSettings.AiItemGroup.EQUIPMENT] = equipmentCategoryButton
        this.addDrawableChild(bookCategoryButton)
        categories[AiItemSettings.AiItemGroup.BOOK] = bookCategoryButton
        if (hasFavorites){
            this.addDrawableChild(favoritesCategoryButton)
            categories[AiItemSettings.AiItemGroup.FAVES] = favoritesCategoryButton
        }
        if (storedRecipeIndex != -1){
            recipeIndex = storedRecipeIndex
            recipeMatches = storedRecipeMatches
            currentStack = storedCurrentStack
            currentRecipes = storedCurrentRecipes
        }
        updateRecipe()
        updateRecipeButtons()

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
            leftRecipeButton.setPos(i+145,j+152)
            rightRecipeButton.setPos(i+211,j+152)
            this.addDrawableChild(leftRecipeButton)
            this.addDrawableChild(rightRecipeButton)
        }
        this.remove(addFavoriteButton)
        if (!currentStack.isEmpty) {
            addFavoriteButton.setPos(i + 147, j + 129)
            addFavoriteButton.setToggle(checkFavorite(currentStack))
            this.addDrawableChild(addFavoriteButton)
        }
    }

    private fun updateRecipe(){
        if (recipeIndex == -1) {
            recipeInputs = Array(13) { RecipeUtil.EmptyStackProvider() }
            recipeOutputs = RecipeUtil.EmptyStackProvider()
            imbuingCost = 0
            return
        }
        val recipe = currentRecipes[recipeIndex]
        val list2 = RecipeUtil.buildInputProviders(recipe)
        recipeInputs = list2.toTypedArray()
        recipeOutputs = buildOutputProvider(recipe)
        recipeBom = recipe.getBom()
        recipeMatches = RecipeUtil.test(screenBom,recipeBom)
        this.remove(addRecipeButton)
        if (recipeMatches.passed){
            addRecipeButton.setPos(i+214, j+129)
            this.addDrawableChild(addRecipeButton)
        }

        imbuingCost = recipe.getCost()
        imbuingCostTooHigh = imbuingCost > playerLevels

    }

    private fun updateFavoritesButton(){
        if (!hasFavorites){
            this.remove(favoritesCategoryButton)
            return
        }
        if (!favoritesButtonAdded){
            favoritesButtonAdded = true
            favoritesCategoryButton.setPos(i-14,j+142)
            this.addDrawableChild(favoritesCategoryButton)
        }
    }

    override fun close() {
        if (hasFavorites){
            sendFavorites()
        }
        storedRecipeIndex = recipeIndex
        storedRecipeMatches = recipeMatches
        storedCurrentStack = currentStack
        storedCurrentRecipes = currentRecipes
        client?.setScreen(oldScreen)
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        super.resize(client, width, height)
        i = (width - 256)/2
        j = (height - 179)/2
        leftRecipeButton.setPos(i+145,j+152)
        rightRecipeButton.setPos(i+211,j+152)
        allCategoryButton.setPos(i-14,j+17)
        gemCategoryButton.setPos(i-14,j+42)
        scepterCategoryButton.setPos(i-14,j+67)
        equipmentCategoryButton.setPos(i-14,j+92)
        bookCategoryButton.setPos(i-14,j+117)
        favoritesCategoryButton.setPos(i-14,j+142)
        addRecipeButton.setPos(i+214, j+129)
        addFavoriteButton.setPos(i+147, j+129)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        val x1 = i-14
        val x2 = x1 + 25
        val x3 = i + 125
        val x4 = i + 256
        val y1 = j + 8
        val y2 = j + 168
        if (mouseY >= y1 && mouseY < y2){
            if (mouseX >= x1 && mouseX < x2){
                val newContainer = updateCurrentContainer(amount < 0.0)
                allCategoryButton.setSelected(false)
                gemCategoryButton.setSelected(false)
                scepterCategoryButton.setSelected(false)
                equipmentCategoryButton.setSelected(false)
                bookCategoryButton.setSelected(false)
                favoritesCategoryButton.setSelected(false)
                categories[newContainer]?.setSelected(true)
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f))
                currentStack = ItemStack.EMPTY
                currentRecipes = listOf()
                recipeIndex = -1
                updateContainer(newContainer)
                updateItemButtons()
                updateRecipe()
                updateRecipeButtons()
                return true
            } else if (mouseX >= x2 && mouseX < x3){
                if (amount < 0.0){
                    rightItemButtonAction.onPress(rightRecipeButton)
                } else {
                    leftItemButtonAction.onPress(leftRecipeButton)
                }
                return true
            } else if (mouseX >= x3 && mouseX < x4){
                if (amount < 0.0){
                    if (currentRecipes.size > 1) rightRecipeButtonAction.onPress(rightRecipeButton)
                } else {
                    if (currentRecipes.size > 1) leftRecipeButtonAction.onPress(leftRecipeButton)
                }
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
        drawTexture(matrices, i, j, 0, 0, 256, 179)
        //title
        DrawableHelper.drawCenteredText(matrices,textRenderer,AcText.translatable("imbuing_recipes_book.screen").formatted(Formatting.GOLD),i + 67,j+13,0x404040)
        //page number text
        val pageText = AcText.translatable("imbuing_recipes_book.page", (getCurrentPage() + 1).toString(), (maxPageIndex + 1).toString()).formatted(Formatting.DARK_GRAY)
        val width = textRenderer.getWidth(pageText)
        addText(matrices,pageText,(i + 67 - width/2).toFloat(),j+152f,0)
        //drawables are rendered here
        super.render(matrices, mouseX, mouseY, delta)
        if (recipeIndex != -1) {
            //recipe text
            val stackName = currentStack.name as MutableText
            DrawableHelper.drawCenteredText(matrices,textRenderer,stackName.formatted(
                Formatting.GOLD),i + 187,j+13,0x404040)
            //the recipe cost on the right page
            val costFormatting = if(imbuingCostTooHigh) Formatting.RED else Formatting.GREEN
            val imbueCost = AcText.translatable("lore_book.screen.cost",imbuingCost.toString()).formatted(costFormatting)
            DrawableHelper.drawCenteredText(matrices,textRenderer,imbueCost,i + 187,j+30,0x404040)
            val imbueWidth = textRenderer.getWidth(imbueCost)
            val hint = if (imbuingCostTooHigh) {
                mutableListOf(*KnowledgeBookScreen.COST_HINT.toTypedArray(),AcText.translatable("imbuing_recipes_book.too_high_hint").formatted(Formatting.DARK_RED,Formatting.ITALIC))
            } else {
                KnowledgeBookScreen.COST_HINT
            }
            tooltips.add(
                KnowledgeBookScreen.TooltipBox(
                    i + 187 - (imbueWidth / 2),
                    j + 30,
                    imbueWidth,
                    9,
                    hint
                )
            )
            //input background highlighting for missing items
            if (!recipeMatches.passed){
                val map = recipeMatches.matches
                for (k in 0..12){
                    if (map[k] == -1){
                        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
                        RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
                        drawTexture(matrices, i - 1 + rowIndices[k], j - 1 + colIndices[k], 48, 227, 18, 18)
                    }
                }
            }
            //input recipe items
            for (k in 0..12){
                renderItem(matrices, i + rowIndices[k], j + colIndices[k], mouseX, mouseY, recipeInputs[k].getStack())
            }
            //output recipe item
            renderItem(matrices,i+180,j+141,mouseX, mouseY,recipeOutputs.getStack())
        }
        for (it in tooltips) {
            if (renderBookTooltip(matrices,mouseX,mouseY,it.x,it.y,it.width,it.height,it.hint)) break
        }
        tooltips.clear()
    }

    private class AddFavoritesWidget(x: Int, y: Int, var toggled: Boolean, pressAction: PressAction, screen: ImbuingRecipeBookScreen):
        ButtonWidget(x,y,15,15,AcText.empty(),pressAction,
            FavoritesTooltipSupplier(x,y,screen, toggled)){

        fun setToggle(toggle: Boolean){
            toggled = toggle
            if (tooltipSupplier is FavoritesTooltipSupplier){
                tooltipSupplier.setToggle(toggle)
            }
        }

        fun toggle(){
            setToggle(!toggled)
        }

        fun setPos(x: Int, y: Int) {
            this.x = x
            this.y = y
            if (tooltipSupplier is FavoritesTooltipSupplier){
                tooltipSupplier.setPos(x,y)
            }
        }

        override fun onPress() {
            toggle()
            super.onPress()
        }

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
            var xOffset = 0
            var yOffset = 0
            if (toggled) {
                xOffset += 15
            }
            if (hovered){
                yOffset += 15
            }
            RenderSystem.enableDepthTest()
            drawTexture(matrices, x, y, 131 + xOffset, 226 + yOffset, width, height)
            if (hovered) {
                renderTooltip(matrices, mouseX, mouseY)
            }
        }

        class FavoritesTooltipSupplier(var x: Int, var y: Int, private val screen: ImbuingRecipeBookScreen, private var toggled: Boolean): TooltipSupplier{
            override fun onTooltip(button: ButtonWidget?, matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
                if (!toggled){
                    screen.tooltips.add(KnowledgeBookScreen.TooltipBox(x,y,15,15, listOf(AcText.translatable("imbuing_recipes_book.fave"))))
                } else {
                    screen.tooltips.add(KnowledgeBookScreen.TooltipBox(x,y,15,15, listOf(AcText.translatable("imbuing_recipes_book.un_fave"))))
                }
            }
            override fun supply(consumer: Consumer<Text>) {
                consumer.accept(AcText.translatable("imbuing_recipes_book.fave"))
            }
            fun setPos(x: Int, y: Int){
                this.x = x
                this.y = y
            }
            fun setToggle(toggle: Boolean){
                toggled = toggle
            }
        }
    }

    private class ItemCategoryWidget(x: Int, y: Int, private var selected: Boolean, item: Item, val container: AiItemSettings.AiItemGroup, pressAction: PressAction, tooltipText: Text, screen: ImbuingRecipeBookScreen):
        ButtonWidget(x,y,25,24,AcText.empty(),pressAction,
            CategoryTooltipSupplier(x,y,screen, tooltipText)){

        private val client = MinecraftClient.getInstance()
        private val stack = ItemStack(item)

        override fun onPress() {
            if (selected) return
            super.onPress()
            selected = true
        }

        fun setSelected(selected: Boolean){
            this.selected = selected
        }

        fun setPos(x: Int, y: Int) {
            this.x = x
            this.y = y
            if (tooltipSupplier is CategoryTooltipSupplier){
                tooltipSupplier.setPos(x,y)
            }
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (!active || !visible) {
                return false
            }
            if (isValidClickButton(button) && clicked(mouseX, mouseY)) {
                client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f))
                onClick(mouseX, mouseY)
                return true
            }
            return false
        }

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
            var xOffset = 0
            var yOffset = 0
            if (selected) {
                xOffset += 25
            }
            if (hovered){
                yOffset += 24
            }
            RenderSystem.enableDepthTest()
            drawTexture(matrices, x, y, 66 + xOffset, 208 + yOffset, width, height)
            if (hovered) {
                renderTooltip(matrices, mouseX, mouseY)
            }
            client.itemRenderer.renderInGuiWithOverrides(client?.player, stack, x + 7, y + 2, x + y * 256)
            //client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x + 4, y + 4, null)
        }

        class CategoryTooltipSupplier(var x: Int, var y: Int, private val screen: ImbuingRecipeBookScreen, private val text: Text): TooltipSupplier{
            override fun onTooltip(button: ButtonWidget?, matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
                screen.tooltips.add(KnowledgeBookScreen.TooltipBox(x,y,25,24, listOf(text)))
            }
            override fun supply(consumer: Consumer<Text>) {
                consumer.accept(text)
            }
            fun setPos(x: Int, y: Int){
                this.x = x
                this.y = y
            }
        }
    }

    private class ItemButtonWidget(x: Int, y: Int, var entry: ItemEntry, private val screen: ImbuingRecipeBookScreen):
        ButtonWidget(
            x,
            y,
            24,
            24,
            AcText.empty(),
            PressAction {  },
            ItemToolTipSupplier(x,y,entry.stack,screen)
        ){

        private val client = MinecraftClient.getInstance()

        init{
            this.visible = !entry.empty
        }

        fun updateEntry(newEntry: ItemEntry){
            entry = newEntry
            this.visible = !entry.empty
            if (tooltipSupplier is ItemToolTipSupplier){
                tooltipSupplier.updateStack(newEntry.stack)
            }
        }

        override fun onPress() {
            screen.recipeIndex = if (entry.list.isEmpty()) -1 else 0
            screen.currentRecipes = entry.list
            screen.currentStack = entry.stack
            screen.updateRecipeButtons()
            screen.updateRecipe()
            //screen.client?.soundManager?.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.2f))
        }

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, KnowledgeBookScreen.BOOK_TEXTURE)
            var xOffset = 0
            if (this.isHovered) {
                xOffset += 24
            }
            RenderSystem.enableDepthTest()
            drawTexture(matrices, x, y, xOffset, 227, width, height)
            if (hovered) {
                renderTooltip(matrices, mouseX, mouseY)
            }
            client.itemRenderer.renderInGuiWithOverrides(client?.player, entry.stack, x + 4, y + 4, x + y * 256)
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, entry.stack, x + 4, y + 4, null)
        }

        private class ItemToolTipSupplier(
            private var x:Int,
            private var y:Int,
            private var stack: ItemStack,
            private val screen: ImbuingRecipeBookScreen): TooltipSupplier{
            override fun onTooltip(button: ButtonWidget?, matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
                screen.tooltips.add(KnowledgeBookScreen.TooltipBox(x,y,24,24,stack.getTooltip(null, screen.context)))
            }
            override fun supply(consumer: Consumer<Text>) {
                consumer.accept(stack.name)
            }
            fun updateStack(newStack: ItemStack){
                this.stack = newStack
            }
            fun setPos(x: Int, y: Int){
                this.x = x
                this.y = y
            }
        }

    }

    companion object RecipeContainer{

        private val rowIndices = arrayOf(
            138, 222, 161, 180, 199, 161, 180,
            199, 161, 180, 199, 138, 222
        )
        private val colIndices = arrayOf(
            38, 38, 48, 48, 48, 67, 67,
            67, 86, 86, 86, 96, 96
        )

        //stored values for reopening the screen
        internal var storedCurrentRecipes: List<ImbuingRecipe> = listOf()
        internal var storedCurrentStack: ItemStack = ItemStack.EMPTY
        internal var storedRecipeIndex = -1
        private var storedRecipeMatches: RecipeUtil.Result = RecipeUtil.Result.EMPTY

        private var recipeList: List<ItemEntry> = listOf()
        private var pages = 0
        private var maxPageIndex = -1
        //private var currentItemPage = 0
        private var currentContainer = AiItemSettings.AiItemGroup.ALL
        private var hasFavorites = false

        private val recipeContainer: Map<AiItemSettings.AiItemGroup,RecipeEntries> = mapOf(
            AiItemSettings.AiItemGroup.ALL to RecipeEntries(),
            AiItemSettings.AiItemGroup.GEM to RecipeEntries(),
            AiItemSettings.AiItemGroup.SCEPTER to RecipeEntries(),
            AiItemSettings.AiItemGroup.EQUIPMENT to RecipeEntries(),
            AiItemSettings.AiItemGroup.BOOK to RecipeEntries(),
            AiItemSettings.AiItemGroup.FAVES to RecipeEntries()
        )

        fun updateContainer(newContainer: AiItemSettings.AiItemGroup){
            val container = recipeContainer[newContainer]?:throw IllegalArgumentException("Container not present in map!")
            currentContainer = newContainer
            recipeList = container.recipeList
            pages = container.pages
            maxPageIndex = container.maxPageIndex
        }

        fun getCurrentPage(): Int{
            val container = recipeContainer[currentContainer]?:throw IllegalArgumentException("Container not present in map!")
            return container.currentItemPage
        }

        fun updateCurrentContainer(up: Boolean): AiItemSettings.AiItemGroup{
            val keys = recipeContainer.keys.toList()
            val currentIndex = keys.indexOf(currentContainer)
            val newContainer = if (up){
                 if (currentIndex == keys.size - 1) keys[0] else keys[currentIndex + 1]
            } else {
                if (currentIndex == 0) keys[keys.size - 1] else keys[currentIndex - 1]
            }
            return if (newContainer == AiItemSettings.AiItemGroup.FAVES && !hasFavorites){
                if (currentContainer == AiItemSettings.AiItemGroup.ALL){
                    AiItemSettings.AiItemGroup.BOOK
                } else {
                    AiItemSettings.AiItemGroup.ALL
                }
            } else {
                newContainer
            }
        }

        fun updateCurrentPage(up: Boolean){
            val container = recipeContainer[currentContainer]?:throw IllegalArgumentException("Container not present in map!")
            if (up){
                container.currentItemPage =
                    if (container.currentItemPage == container.maxPageIndex) 0 else container.currentItemPage + 1
            } else {
                container.currentItemPage =
                    if (container.currentItemPage == 0) container.maxPageIndex else container.currentItemPage - 1
            }
        }

        private fun addFavorite(stack: ItemStack, recipes: List<ImbuingRecipe>){
            hasFavorites = true
            recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.addToList(stack, recipes)
        }

        private fun removeFavorite(stack: ItemStack){
            recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.removeFromList(stack)
            if (recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.recipeList?.isEmpty() == true){
                hasFavorites = false
            }
        }

        private fun checkFavorite(stack: ItemStack): Boolean{
            val list = recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.recipeList?:return false
            list.forEach {
                if (ItemStack.areEqual(it.stack,stack)) return true
            }
            return false
        }
        private fun sendFavorites(){
            val entries = recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.recipeList?:return
            val uuid = MinecraftClient.getInstance().player?.uuid?:return
            if (entries.isEmpty()) return
            val buf = PacketByteBufs.create()
            buf.writeUuid(uuid)
            buf.writeShort(entries.size)
            entries.forEach {
                buf.writeItemStack(it.stack)
            }
            ClientPlayNetworking.send(RecipeUtil.FAVORITES_SAVE,buf)
        }

        private fun getItemEntry(index: Int): ItemEntry{
            val newIndex = getCurrentPage() * 20 + index
            return if (newIndex >= recipeList.size) ItemEntry.EMPTY else recipeList[newIndex]
        }
        private fun getCurrentDefaultItemEntry(): ItemEntry{
            val container = recipeContainer[currentContainer]?:throw IllegalArgumentException("Container not present in map!")
            container.currentItemPage = 0
            return container.defaultEntry()
        }

        fun registerClientReceiver(){
            ClientPlayNetworking.registerGlobalReceiver(RecipeUtil.FAVORITES_SEND){_,_,buf,_->
                val hasFaves = buf.readBoolean()
                hasFavorites = hasFaves
                if (hasFaves){
                    val list: MutableList<ItemEntry> = mutableListOf()
                    val entries = recipeContainer[AiItemSettings.AiItemGroup.ALL]?.recipeList?:return@registerGlobalReceiver
                    val size = buf.readShort()
                    for (i in 1..size){
                        val stack = buf.readItemStack()
                        for (entry in entries){
                            if (stack.item is EnchantedBookItem){
                                if (ItemStack.areEqual(stack,entry.stack)){
                                    list.add(entry)
                                    break
                                }
                            } else {
                                if (stack.isItemEqual(entry.stack)){
                                    list.add(entry)
                                    break
                                }
                            }

                        }
                    }
                    recipeContainer[AiItemSettings.AiItemGroup.FAVES]?.setList(list)

                }
            }
        }

        @Suppress("LocalVariableName")
        fun registerClient(){
            if (ModCompatHelper.getScreenHandlerOffset() != 0) return
            val manager = MinecraftClient.getInstance()?.world?.recipeManager?:return
            val player = MinecraftClient.getInstance()?.player?:return
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
            val tempList_all: MutableList<ItemEntry> = mutableListOf()
            val tempList_misc: MutableList<ItemEntry> = mutableListOf()
            val tempList_gem: MutableList<ItemEntry> = mutableListOf()
            val tempList_book: MutableList<ItemEntry> = mutableListOf()
            val tempList_equipment: MutableList<ItemEntry> = mutableListOf()
            val tempList_scepter: MutableList<ItemEntry> = mutableListOf()

            tempList.forEach {
                val group = AiItemSettings.groupFromItem(it.first.item)
                if (group != null){
                    when(group){
                        AiItemSettings.AiItemGroup.GEM -> tempList_gem.add(ItemEntry(it.first,it.second))
                        AiItemSettings.AiItemGroup.EQUIPMENT -> tempList_equipment.add(ItemEntry(it.first,it.second))
                        AiItemSettings.AiItemGroup.SCEPTER -> tempList_scepter.add(ItemEntry(it.first,it.second))
                        else -> tempList_misc.add(ItemEntry(it.first,it.second))
                    }
                } else if(it.first.isOf(Items.ENCHANTED_BOOK)){
                    tempList_book.add(ItemEntry(it.first,it.second))
                } else {
                    tempList_misc.add(ItemEntry(it.first,it.second))
                }
            }

            tempList_all.addAll(tempList_gem)
            tempList_all.addAll(tempList_scepter)
            tempList_all.addAll(tempList_equipment)
            tempList_all.addAll(tempList_misc)
            tempList_all.addAll(tempList_book)
            recipeContainer[AiItemSettings.AiItemGroup.ALL]?.setList(tempList_all)
            recipeContainer[AiItemSettings.AiItemGroup.SCEPTER]?.setList(tempList_scepter)
            recipeContainer[AiItemSettings.AiItemGroup.EQUIPMENT]?.setList(tempList_equipment)
            recipeContainer[AiItemSettings.AiItemGroup.GEM]?.setList(tempList_gem)
            recipeContainer[AiItemSettings.AiItemGroup.BOOK]?.setList(tempList_book)
            updateContainer(AiItemSettings.AiItemGroup.ALL)
            val buf = PacketByteBufs.create()
            buf.writeUuid(player.uuid)
            ClientPlayNetworking.send(RecipeUtil.FAVORITES_CHECK,buf)
        }

        private class ItemEntry(val stack: ItemStack, val list: List<ImbuingRecipe>, val empty: Boolean = false){
            companion object{
                val EMPTY = ItemEntry(ItemStack.EMPTY,listOf(), true)
            }
            override fun toString(): String {
                return "{stack: $stack, # of recipes: ${list.size}}"
            }
            override fun equals(other: Any?): Boolean {
                if (other == null) return false
                if (other !is ItemEntry) return false
                return (ItemStack.areEqual(stack,other.stack) && list == other.list)
            }
            override fun hashCode(): Int {
                var result = stack.hashCode()
                result = 31 * result + list.hashCode()
                result = 31 * result + empty.hashCode()
                return result
            }
        }

        private class RecipeEntries{
            var recipeList: List<ItemEntry> = listOf()
            var pages = 0
            var maxPageIndex = -1
            var currentItemPage = 0
            private var defaultEntry = ItemEntry.EMPTY

            fun setList(list: List<ItemEntry>){
                recipeList = list
                pages = ceil(recipeList.size.toFloat()/20f).toInt()
                maxPageIndex = pages - 1
                if (list.isNotEmpty()) defaultEntry = recipeList[0]
            }
            fun addToList(stack: ItemStack,recipes: List<ImbuingRecipe>){
                val list: MutableList<ItemEntry> = recipeList.toMutableList()
                val entry = ItemEntry(stack,recipes)
                list.addIfDistinct(entry)
                setList(list)

            }
            fun removeFromList(stack: ItemStack){
                val list: MutableList<ItemEntry> = recipeList.toMutableList()
                recipeList.forEach {
                    if (ItemStack.areEqual(it.stack,stack)){
                        list.remove(it)
                    }
                }
                setList(list)
            }
            fun defaultEntry(): ItemEntry{
                return defaultEntry
            }

            override fun toString(): String {
                return "[pages: $pages | current page: $currentItemPage | recipes: $recipeList]"
            }
        }
    }
}