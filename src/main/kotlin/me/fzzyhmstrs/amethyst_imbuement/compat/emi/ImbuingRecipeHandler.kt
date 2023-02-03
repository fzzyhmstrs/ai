package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.EmiRecipeHandler
import dev.emi.emi.api.recipe.EmiRecipe
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import net.minecraft.screen.slot.Slot

class ImbuingRecipeHandler: EmiRecipeHandler<ImbuingTableScreenHandler> {
    override fun getInputSources(handler: ImbuingTableScreenHandler): MutableList<Slot> {
        val list: MutableList<Slot> = mutableListOf()
        list.addAll(handler.slots)
        return list
    }

    override fun getCraftingSlots(handler: ImbuingTableScreenHandler): MutableList<Slot> {
        val list: MutableList<Slot> = mutableListOf()
        for (i in 0..12){
            list.add(handler.slots[i])
        }
        return list
    }

    override fun getOutputSlot(handler: ImbuingTableScreenHandler): Slot? {
        return handler.getSlot(6)
    }

    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        val category = EmiClientPlugin.IMBUING_CATEGORY
        return recipe.category === category
    }
}