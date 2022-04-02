package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor

class ImbuingTableMenuInfo(private val display: ImbuingTableDisplay): SimplePlayerInventoryMenuInfo<ImbuingTableScreenHandler, ImbuingTableDisplay> {

    override fun getInputSlots(context: MenuInfoContext<ImbuingTableScreenHandler, *, ImbuingTableDisplay>): Iterable<SlotAccessor> {
        val slots = context.menu.slots
        val returnList: MutableList<SlotAccessor> = mutableListOf()
        for (i in 0..12) {
            returnList.add(SlotAccessor.fromSlot(slots[i]))
        }

        return returnList.asIterable()
    }

    override fun getDisplay(): ImbuingTableDisplay {
        return display
    }

    override fun populateRecipeFinder(menu: ImbuingTableScreenHandler, finder: RecipeFinder) {
        menu.populateRecipeFinder(finder)
    }
}