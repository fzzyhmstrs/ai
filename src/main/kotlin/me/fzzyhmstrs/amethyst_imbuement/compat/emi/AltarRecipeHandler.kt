package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import net.minecraft.screen.slot.Slot

class AltarRecipeHandler: StandardRecipeHandler<CrystalAltarScreenHandler> {
    override fun getInputSources(handler: CrystalAltarScreenHandler): MutableList<Slot> {
        val list: MutableList<Slot> = mutableListOf(handler.slots[0],handler.slots[1],handler.slots[2])
        for (i in 4 until 40){
            list.add(handler.slots[i])
        }
        return list
    }

    override fun getCraftingSlots(handler: CrystalAltarScreenHandler): MutableList<Slot> {
        return mutableListOf(handler.slots[0],handler.slots[1],handler.slots[2])
    }

    override fun getOutputSlot(handler: CrystalAltarScreenHandler): Slot? {
        return handler.getSlot(3)
    }

    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        val category = EmiClientPlugin.ALTAR_CATEGORY
        return recipe.category === category && recipe.supportsRecipeTree()
    }
}
