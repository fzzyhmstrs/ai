package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat.IMBUING_BACKGROUND
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat.IMBUING_BACKGROUND_AUGMENT
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.BookTextRenderer
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry


class ImbuingRecipePage: BookPage() {

    private var recipe: Identifier? = null

    @Transient
    private var costRender: BookTextRenderer? = null
    @Transient
    private var imbuingRecipe: ImbuingRecipe? = null
    @Transient
    private var costText: Text? = null
    @Transient
    private var titleText: Text? = null


    override fun build(world: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(world, entry, builder, pageNum)
        imbuingRecipe = loadRecipe(world,entry,builder,pageNum,recipe)
    }

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        if (costText == null)
            costText = AcText.translatable("patchouli.cost_text",AcText.literal((imbuingRecipe?.getCost() ?: 1).toString()).formatted(Formatting.DARK_GREEN))
        costRender = BookTextRenderer(parent, costText, 32, 18, 58, GuiBook.TEXT_LINE_HEIGHT, this.book.textColor)
        if (titleText == null)
            titleText = imbuingRecipe?.getName()?.copyContentOnly() ?: AcText.translatable("patchouli.generic_title_text")
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

        val augment = imbuingRecipe?.getAugment() != ""

        //background
        if (augment)
            context.drawTexture(IMBUING_BACKGROUND_AUGMENT, 5, 14, 0f, 0f, 106, 121, 128, 128)
        else
            context.drawTexture(IMBUING_BACKGROUND, 5, 14, 0f, 0f, 106, 121, 128, 128)

        //texts
        parent.drawCenteredStringNoShadow(context, titleText?.asOrderedText(), GuiBook.PAGE_WIDTH / 2, 0,this.book.headerColor)
        costRender?.render(context, mouseX, mouseY)


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

}