package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.shedaniel.rei.api.common.transfer.RecipeFinder
import me.shedaniel.rei.api.common.transfer.RecipeFinderPopulator
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class ImbuingTableMenuInfo(private val display: ImbuingTableDisplay): SimplePlayerInventoryMenuInfo<ImbuingTableScreenHandler, ImbuingTableDisplay> {

    override fun getInputSlots(context: MenuInfoContext<ImbuingTableScreenHandler, *, ImbuingTableDisplay>): Iterable<SlotAccessor> {
        val slots = context.menu.slots
        val returnList: MutableList<SlotAccessor> = mutableListOf()
        for (i in 0..12) {
            val slot = slots[i]
            if (slot is ImbuingTableScreenHandler.ImbuingSlot){
                slot.setLocked(true)
            }
            returnList.add(SlotAccessor.fromSlot(slot))
        }

        return returnList.asIterable()
    }

    override fun markDirty(context: MenuInfoContext<ImbuingTableScreenHandler, out ServerPlayerEntity, ImbuingTableDisplay>) {
        val slots = context.menu.slots
        for (i in 0..12){
            val slot = slots[i]
            if (slot is ImbuingTableScreenHandler.ImbuingSlot){
                slot.setLocked(false)
            }
        }
        super.markDirty(context)
    }

    override fun getDisplay(): ImbuingTableDisplay {
        return display
    }

    override fun populateRecipeFinder(menu: ImbuingTableScreenHandler, finder: RecipeFinder) {
        menu.populateRecipeFinder(finder)
    }

    override fun getRecipeFinderPopulator(): RecipeFinderPopulator<ImbuingTableScreenHandler, ImbuingTableDisplay> {
        return RecipeFinderPopulator {
                context: MenuInfoContext<ImbuingTableScreenHandler, *, ImbuingTableDisplay>, finder: RecipeFinder ->
            for (inventoryStack in getInventorySlots(context)) {
                finder.addItem(inventoryStack.itemStack)
            }
            populateRecipeFinder(context.menu, finder)
        }
    }
}