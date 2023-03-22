package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
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


@Suppress("UnstableApiUsage")
class CrystalAltarCategory: DisplayCategory<CrystalAltarDisplay> {
    override fun getIcon(): Renderer {
        return getIconEntryStack()
    }

    fun getIconEntryStack(): EntryStack<ItemStack> {
        return EntryStacks.of(ItemStack(RegisterBlock.CRYSTAL_ALTAR.asItem()))
    }

    override fun getTitle(): Text {
        return AcText.translatable("recipe.enhancing")
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<CrystalAltarDisplay> {
        return ReiCategoryIds.CRYSTAL_ALTAR_CATEGORY_ID
    }

    override fun setupDisplay(display: CrystalAltarDisplay, bounds: Rectangle): MutableList<Widget> {
        val baseX = bounds.x + 5
        val baseY = bounds.y + 5
        val widgets: MutableList<Widget> = mutableListOf()
        widgets.add(Widgets.createRecipeBase(bounds))
        widgets.add(Widgets.createArrow(Point(baseX + 62, baseY)))
        //widgets.add(Widgets.createResultSlotBackground(Point(startPoint.x + 61, startPoint.y + 5)))
        widgets.add(
            Widgets.createSlot(Point(baseX, baseY)).entries(
                display.inputEntries[0]
            ).markInput()
        )
        widgets.add(
            Widgets.createSlot(Point(baseX, baseY+18)).entries(
                display.inputEntries[1]
            ).markInput()
        )
        widgets.add(
            Widgets.createSlot(Point(baseX, baseY+36)).entries(
                display.inputEntries[2]
            ).markInput()
        )
        widgets.add(
            Widgets.createSlot(Point(baseX + 94, baseY)).entries(
                display.outputEntries[0]
            ).markOutput()
        )
        return widgets
    }

    override fun getDisplayHeight(): Int {
        return 28
    }


}
