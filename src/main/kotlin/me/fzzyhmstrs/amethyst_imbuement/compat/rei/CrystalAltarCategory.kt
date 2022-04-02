package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.AltarRecipe
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier


@Suppress("UnstableApiUsage")
class CrystalAltarCategory: DisplayCategory<CrystalAltarDisplay> {
    override fun getIcon(): Renderer {
        return getIconEntryStack()
    }

    fun getIconEntryStack(): EntryStack<ItemStack> {
        return EntryStacks.of(ItemStack(RegisterBlock.CRYSTAL_ALTAR.asItem()))
    }

    override fun getTitle(): Text {
        return TranslatableText("recipe.enhancing")
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<CrystalAltarDisplay> {
        return CategoryIdentifier.of(identifier)
    }

    override fun getIdentifier(): Identifier {
        return Identifier(AI.MOD_ID,AltarRecipe.Type.ID)
    }

    override fun setupDisplay(display: CrystalAltarDisplay, bounds: Rectangle): MutableList<Widget> {
        val startPoint = Point(bounds.centerX - 31, bounds.centerY - 13)
        val widgets: MutableList<Widget> = mutableListOf()
        widgets.add(Widgets.createRecipeBase(bounds))
        widgets.add(Widgets.createArrow(Point(startPoint.x + 27, startPoint.y + 4)))
        widgets.add(Widgets.createResultSlotBackground(Point(startPoint.x + 61, startPoint.y + 5)))
        widgets.add(
            Widgets.createSlot(Point(startPoint.x + 4 - 22, startPoint.y + 5)).entries(
                display.inputEntries[0]
            ).markInput()
        )
        widgets.add(
            Widgets.createSlot(Point(startPoint.x + 4, startPoint.y + 5)).entries(
                display.inputEntries[1]
            ).markInput()
        )
        widgets.add(
            Widgets.createSlot(Point(startPoint.x + 61, startPoint.y + 5)).entries(
                display.outputEntries[0]
            ).disableBackground().markOutput()
        )
        return widgets
    }

    override fun getDisplayHeight(): Int {
        return 36
    }


}