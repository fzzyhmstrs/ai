package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor

class CrystalAltarMenuInfo(private val display: CrystalAltarDisplay): SimplePlayerInventoryMenuInfo<CrystalAltarScreenHandler, CrystalAltarDisplay> {

    override fun getInputSlots(context: MenuInfoContext<CrystalAltarScreenHandler, *, CrystalAltarDisplay>): Iterable<SlotAccessor> {
        val slots = context.menu.slots
        val returnList: MutableList<SlotAccessor> = mutableListOf()
        for (i in 0..1){
            returnList.add(SlotAccessor.fromSlot(slots[i]))
        }

        return returnList.asIterable()
    }

    override fun getDisplay(): CrystalAltarDisplay {
        return display
    }

    override fun populateRecipeFinder(menu: CrystalAltarScreenHandler, finder: RecipeFinder) {
        menu.populateRecipeFinder(finder)
    }
}