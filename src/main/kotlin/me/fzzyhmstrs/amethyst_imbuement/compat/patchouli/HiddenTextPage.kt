package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import vazkii.patchouli.client.base.ClientAdvancements
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.page.PageText

class HiddenTextPage: PageText() {

    private var text2: String? = null

    private var advancement2: String? = null

    override fun onDisplayed(parent: GuiBookEntry?, left: Int, top: Int) {
        if (isHiddenTextUnlocked())
            setText(text2)
        super.onDisplayed(parent, left, top)
    }

    private fun isHiddenTextUnlocked(): Boolean {
        return advancement2 == null || advancement2?.isEmpty() == true || ClientAdvancements.hasDone(advancement2)
    }


}