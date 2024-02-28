package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.Blocks
import net.minecraft.client.gui.DrawContext
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.client.base.ClientAdvancements
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.page.PageCrafting


class CraftingLockablePage: PageCrafting(), AdvancementUpdatable {

    private var unlockDesc: IVariable? = null
    private var unlock: String? = null
    private var hintItem: IVariable? = null

    @Transient
    private var unlocked: Boolean = true
    @Transient
    private var unlockText: MutableList<OrderedText> = mutableListOf()
    @Transient
    private var unlockIngredient: Ingredient = Ingredient.EMPTY
    @Transient
    private var hintIngredient: Ingredient? = Ingredient.ofItems(Blocks.CRAFTING_TABLE)

    override fun build(world: World, entry: BookEntry, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(world, entry, builder, pageNum)
        unlockIngredient = Ingredient.ofStacks(getRecipeOutput(world,recipe1),getRecipeOutput(world,recipe2))
        hintItem?.let { hintIngredient = it.`as`(Ingredient::class.java) }
        if (unlock != null) unlocked = ClientAdvancements.hasDone(unlock) || !entry.book.advancementsEnabled()
    }

    override fun onDisplayed(parent: GuiBookEntry, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
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
                unlockDesc = IVariable.wrap(PatchouliCompat.defaultUnknownUnlockText)
            unlockText = mc.textRenderer.wrapLines(
                (unlockDesc?.`as`(Text::class.java)?.copy() ?: AcText.empty()).formatted(Formatting.ITALIC),
                GuiBook.PAGE_WIDTH
            )
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        //title
        //parent.drawCenteredStringNoShadow(context, titleText?.asOrderedText(), GuiBook.PAGE_WIDTH / 2, 0,this.book.headerColor)

        if (!unlocked) {
            PatchouliCompat.renderHint(context, mouseX, mouseY,hintIngredient,unlockIngredient,unlockText,mc,book, parent)
            return
        }
        super.render(context, mouseX, mouseY, delta)
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