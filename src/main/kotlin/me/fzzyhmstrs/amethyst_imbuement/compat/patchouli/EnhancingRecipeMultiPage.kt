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
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry


class EnhancingRecipeMultiPage: BookPage() {

    private var recipes: Array<Identifier?>? = null

    @Transient
    private var enhancingRecipes: Array<RecipeAndTitle>? = null

    override fun build(world: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(world, entry, builder, pageNum)
        if ((recipes?.size ?: 0) > 3) throw IllegalStateException("recipes array in EnhancingRecipeMultiPage >3 long")
        enhancingRecipes = recipes?.map { RecipeAndTitle(null, loadRecipe(world,entry,builder,pageNum,it)) }?.toTypedArray()
    }

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        enhancingRecipes?.let {
            for (titleAndText in it)
                titleAndText.title = titleAndText.recipe?.getOutput()?.name ?: AcText.translatable("patchouli.generic_title_text")
        }
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
        //abort if blank
        if (enhancingRecipes == null) return

        var yOffset = 0
        enhancingRecipes?.let {
            for (recipeAndTitle in it) {

                val enhancingRecipe = recipeAndTitle.recipe
                val titleText = recipeAndTitle.title

                RenderSystem.enableBlend()
                //background
                context.drawTexture(ENHANCING_BACKGROUND, 9, 12 + yOffset, 0f, 0f, 98, 22, 128, 32)

                //texts
                parent.drawCenteredStringNoShadow(
                    context,
                    titleText?.asOrderedText(),
                    GuiBook.PAGE_WIDTH / 2,
                    0 + yOffset,
                    this.book.headerColor
                )


                //output
                parent.renderItemStack(context, 88, 15 + yOffset, mouseX, mouseY, enhancingRecipe?.getOutput())

                //inputs
                parent.renderIngredient(
                    context,
                    12,
                    15 + yOffset,
                    mouseX,
                    mouseY,
                    enhancingRecipe?.dust
                )
                parent.renderIngredient(
                    context,
                    31,
                    15 + yOffset,
                    mouseX,
                    mouseY,
                    enhancingRecipe?.base
                )
                parent.renderIngredient(
                    context,
                    50,
                    15 + yOffset,
                    mouseX,
                    mouseY,
                    enhancingRecipe?.addition
                )

                if (enhancingRecipe?.react == true) {
                    val text =
                        AcText.translatable(enhancingRecipe.reactMessage).formatted(Formatting.DARK_PURPLE)
                    parent.drawCenteredStringNoShadow(context, text.asOrderedText(), 0, 36 + yOffset, this.book.headerColor)
                }
                yOffset += 50
            }
        }
        super.render(context, mouseX, mouseY, delta)
    }

    private class RecipeAndTitle(var title: Text? = null, var recipe: AltarRecipe? = null)

}