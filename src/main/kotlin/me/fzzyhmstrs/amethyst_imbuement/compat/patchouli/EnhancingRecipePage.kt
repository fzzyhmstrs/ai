package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat.ENHANCING_BACKGROUND
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.BookTextRenderer
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry


class EnhancingRecipePage: BookPage() {

    private var recipe: Identifier? = null
    private var text: IVariable? = null

    @Transient
    private var textRender: BookTextRenderer? = null
    @Transient
    private var enhancingRecipe: AltarRecipe? = null
    @Transient
    private var titleText: Text? = null


    override fun build(world: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(world, entry, builder, pageNum)
        enhancingRecipe = loadRecipe(world,entry,builder,pageNum,recipe)
    }

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        if (text == null)
            text = IVariable.wrap("")
        textRender = BookTextRenderer(parent, text?.`as`(Text::class.java), 0, 60)
        if (titleText == null)
            titleText = enhancingRecipe?.getOutput()?.name ?: AcText.translatable("patchouli.generic_title_text")
    }

    private fun loadRecipe(world: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int, recipeId: Identifier?): AltarRecipe?{
        if (world == null || recipeId == null) {
            return null;
        }
        val recipeChk = world.recipeManager.get(recipeId).filter { it.type == AltarRecipe.Type }.takeIf { it.isPresent }?.get() as? AltarRecipe
        if (recipeChk == null){
            PatchouliAPI.LOGGER.warn("Altar Enhancing Recipe {} not found",recipeId)
            return null
        }
        entry?.addRelevantStack(builder, recipeChk.getOutput(), pageNum)
        return recipeChk
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.enableBlend()
        //abort if blank
        if (enhancingRecipe == null) return

        //background
        context.drawTexture(ENHANCING_BACKGROUND, 9, 14, 0f, 0f, 98, 22, 128, 32)

        //texts
        parent.drawCenteredStringNoShadow(context, titleText?.asOrderedText(), GuiBook.PAGE_WIDTH / 2, 0,this.book.headerColor)
        textRender?.render(context, mouseX, mouseY)


        //output
        parent.renderItemStack(context, 88, 17, mouseX, mouseY, enhancingRecipe?.getOutput())

        //inputs
        parent.renderIngredient(
            context,
            12,
            17,
            mouseX,
            mouseY,
            enhancingRecipe?.dust
        )
        parent.renderIngredient(
            context,
            31,
            17,
            mouseX,
            mouseY,
            enhancingRecipe?.base
        )
        parent.renderIngredient(
            context,
            50,
            17,
            mouseX,
            mouseY,
            enhancingRecipe?.addition
        )

        if (enhancingRecipe?.react == true){
            val text = AcText.translatable(enhancingRecipe?.reactMessage ?: "").formatted(Formatting.DARK_PURPLE)
            parent.drawCenteredStringNoShadow(context, text.asOrderedText(), 0, 38,this.book.headerColor)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return textRender?.click(mouseX, mouseY, mouseButton) ?: super.mouseClicked(mouseX, mouseY, mouseButton)
    }

}