package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat.IMBUING_BACKGROUND
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat.IMBUING_BACKGROUND_AUGMENT
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.DrawContext
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.base.ClientAdvancements
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.BookTextRenderer
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry


class ImbuingRecipePage: BookPage(), AdvancementUpdatable {

    private var recipe: Identifier? = null
    private var unlockDesc: IVariable? = null
    private var unlock: String? = null
    private var hintItem: IVariable? = null

    @Transient
    private var costRender: BookTextRenderer? = null
    @Transient
    private var imbuingRecipe: ImbuingRecipe? = null
    @Transient
    private var costText: Text? = null
    @Transient
    private var titleText: Text? = null
    @Transient
    private var unlocked: Boolean = true
    @Transient
    private var unlockText: MutableList<OrderedText> = mutableListOf()
    @Transient
    private var hintIngredient: Ingredient? = Ingredient.ofItems(RegisterBlock.IMBUING_TABLE)

    override fun build(world: World, entry: BookEntry, builder: BookContentsBuilder, pageNum: Int) {
        super.build(world, entry, builder, pageNum)
        imbuingRecipe = loadRecipe(world,entry,builder,pageNum,recipe)
        hintItem?.let { hintIngredient = it.`as`(Ingredient::class.java) }
        if (unlock != null) unlocked = ClientAdvancements.hasDone(unlock) || !entry.book.advancementsEnabled()
    }

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        if (costText == null)
            costText = AcText.translatable("patchouli.imbuing_cost_text",AcText.literal((imbuingRecipe?.getCost() ?: 1).toString()).formatted(Formatting.DARK_GREEN))
        costRender = BookTextRenderer(parent, costText, 32, 18, 58, GuiBook.TEXT_LINE_HEIGHT, this.book.textColor)
        if (titleText == null)
            titleText = imbuingRecipe?.getName()?.copyContentOnly() ?: AcText.translatable("patchouli.generic_imbuing_title")
        if (unlock == null) return
        if(unlocked) {
            if (!ClientAdvancements.hasDone(unlock))
                unlocked = false
        } else {
            if (ClientAdvancements.hasDone(unlock) || !entry.book.advancementsEnabled())
                unlocked = true
        }
        if (!unlocked) {
            if (unlockDesc == null)
                unlockDesc = IVariable.wrap(PatchouliCompat.defaultImbuingUnlockText)
            unlockText = mc.textRenderer.wrapLines(
                (unlockDesc?.`as`(Text::class.java)?.copy() ?: AcText.empty()).formatted(Formatting.ITALIC),
                GuiBook.PAGE_WIDTH
            )
        }
    }

    private fun loadRecipe(world: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int, recipeId: Identifier?): ImbuingRecipe?{
        if (world == null || recipeId == null) {
            return null;
        }
        val recipeChk = world.recipeManager.get(recipeId).filter { it.type == ImbuingRecipe.Type }.takeIf { it.isPresent }?.get() as? ImbuingRecipe
        if (recipeChk == null){
            PatchouliAPI.LOGGER.warn("Imbuing Recipe {} not found",recipeId)
            return null
        }
        entry?.addRelevantStack(builder, recipeChk.getOutput(), pageNum)
        return recipeChk
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.enableBlend()
        //abort if blank
        if (imbuingRecipe == null) return

        //title
        parent.drawCenteredStringNoShadow(context, titleText?.asOrderedText(), GuiBook.PAGE_WIDTH / 2, 0,this.book.headerColor)

        if (!unlocked) {
            PatchouliCompat.renderHint(context, mouseX, mouseY,hintIngredient,imbuingRecipe?.getOutput(),unlockText,mc,book, parent)
            return
        }

        val augment = imbuingRecipe?.getAugment() != ""

        //background
        if (augment) {
            RenderSystem.enableBlend()
            context.drawTexture(IMBUING_BACKGROUND_AUGMENT, 5, 14, 0f, 0f, 106, 121, 128, 128)
            if (parent.isMouseInRelativeRange(mouseX.toDouble(), mouseY.toDouble(), 53, 118, 11, 11)) {
                parent.setTooltip(AcText.translatable("patchouli.imbuing_plus_tip_1"),AcText.translatable("patchouli.imbuing_plus_tip_2"))
            }
        } else {
            RenderSystem.enableBlend()
            context.drawTexture(IMBUING_BACKGROUND, 5, 14, 0f, 0f, 106, 121, 128, 128)
        }

        //texts
        costRender?.render(context, mouseX, mouseY)
        if (parent.isMouseInRelativeRange(mouseX.toDouble(), mouseY.toDouble(), 31, 17, 60, 11)) {
            parent.setTooltip(AcText.translatable("patchouli.imbuing_cost_tip"))
        }


        //output
        parent.renderItemStack(context, if(augment) 69 else 50, 116, mouseX, mouseY, imbuingRecipe?.getOutput())

        //input again if it's an augment, to show the +
        if (augment)
            parent.renderIngredient(
                context,
                31,
                116,
                mouseX,
                mouseY,
                imbuingRecipe?.getCenterIngredient()
            )

        //inputs
        imbuingRecipe?.getInputs()?.forEachIndexed { index, ingredient ->
            if (index == 6){
                parent.renderIngredient(
                    context,
                    PatchouliCompat.xOffsets[index],
                    PatchouliCompat.yOffsets[index],
                    mouseX,
                    mouseY,
                    imbuingRecipe?.getCenterIngredient()
                )
            } else {
                parent.renderIngredient(
                    context,
                    PatchouliCompat.xOffsets[index],
                    PatchouliCompat.yOffsets[index],
                    mouseX,
                    mouseY,
                    ingredient
                )
            }
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return costRender?.click(mouseX, mouseY, mouseButton) ?: super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateLockStatus(entry: BookEntry): Boolean {
        if (unlock != null) {
            val prevUnlocked = unlocked
            unlocked = ClientAdvancements.hasDone(unlock) || !entry.book.advancementsEnabled()
            return !prevUnlocked && unlocked
        }
        return false
    }

}