package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.EmiRecipeHandler
import dev.emi.emi.api.recipe.EmiRecipe
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import net.minecraft.screen.slot.Slot

class AltarRecipeHandler: EmiRecipeHandler<CrystalAltarScreenHandler> {
    override fun getInputSources(handler: CrystalAltarScreenHandler): MutableList<Slot> {
        val list: MutableList<Slot> = mutableListOf(handler.slots[0],handler.slots[1])
        for (i in 3 until 39){
            list.add(handler.slots[i])
        }
        return list
    }

    override fun getCraftingSlots(handler: CrystalAltarScreenHandler): MutableList<Slot> {
        return mutableListOf(handler.slots[0],handler.slots[1])
    }

    override fun getOutputSlot(handler: CrystalAltarScreenHandler): Slot? {
        return handler.getSlot(2)
    }

    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        val category = EmiClientPlugin.ALTAR_CATEGORY
        return recipe.category === category && recipe.supportsRecipeTree()
    }
}