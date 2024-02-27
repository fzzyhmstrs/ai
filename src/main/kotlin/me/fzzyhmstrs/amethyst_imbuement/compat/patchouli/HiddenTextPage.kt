package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.resource.language.I18n
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.client.base.ClientAdvancements
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.page.PageText

class HiddenTextPage: PageText(), AdvancementUpdatable {

    private var text2: String? = null
    private var advancement2: String? = null
    private var unlockItem: Identifier? = null
    private var unlockDesc: IVariable? = null
    private var unlock: String? = null
    private var hintItem: IVariable? = null

    @Transient
    private var unlockStack: ItemStack = ItemStack.EMPTY
    @Transient
    private var hasLockedStack = false
    @Transient
    private var unlocked: Boolean = true
    @Transient
    private var unlockText: MutableList<OrderedText> = mutableListOf()
    @Transient
    private var hintIngredient: Ingredient? = null

    override fun build(level: World, entry: BookEntry, builder: BookContentsBuilder, pageNum: Int) {
        super.build(level, entry, builder, pageNum)
        unlockStack = FzzyPort.ITEM.get(unlockItem).defaultStack
        hasLockedStack = !unlockStack.isEmpty
        hintItem?.let { hintIngredient = it.`as`(Ingredient::class.java) }
        if (unlock != null) unlocked = ClientAdvancements.hasDone(unlock) || !entry.book.advancementsEnabled()
    }

    override fun onDisplayed(parent: GuiBookEntry, left: Int, top: Int) {
        mc = parent.minecraft
        if (isHiddenTextUnlocked()) {
            if (text2 != null)
                setText(text2)
        }
        if (unlock != null) {
            if (unlocked) {
                if (!ClientAdvancements.hasDone(unlock))
                    unlocked = false
            } else {
                if (ClientAdvancements.hasDone(unlock)|| !entry.book.advancementsEnabled())
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
        super.onDisplayed(parent, left, top)
    }

    private fun isHiddenTextUnlocked(): Boolean {
        return advancement2 == null || advancement2?.isEmpty() == true || ClientAdvancements.hasDone(advancement2)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, pticks: Float) {
        if (!unlocked) {
            PatchouliCompat.renderHint(context, mouseX, mouseY,hintIngredient,unlockStack,unlockText,mc,book, parent)
            if (pageNum == 0) {
                var renderedSmol = false
                var smolText = ""
                if (mc.options.advancedItemTooltips) {
                    val res = parent.entry.id
                    smolText = res.toString()
                } else if (entry.addedBy != null) {
                    smolText = I18n.translate("patchouli.gui.lexicon.added_by", entry.addedBy)
                }
                if (!smolText.isEmpty()) {
                    context.matrices.scale(0.5f, 0.5f, 1f)
                    parent.drawCenteredStringNoShadow(context, smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor)
                    context.matrices.scale(2f, 2f, 1f)
                    renderedSmol = true
                }
                parent.drawCenteredStringNoShadow(
                    context,
                    parent.entry.name.asOrderedText(),
                    GuiBook.PAGE_WIDTH / 2,
                    if (renderedSmol) -3 else 0,
                    book.headerColor
                )
                GuiBook.drawSeparator(context, book, 0, 12)
            }
            return
        }
        super.render(context, mouseX, mouseY, pticks)
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