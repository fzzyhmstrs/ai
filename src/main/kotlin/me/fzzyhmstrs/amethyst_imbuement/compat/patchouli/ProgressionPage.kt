package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.world.World
import vazkii.patchouli.client.base.ClientAdvancements
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry

class ProgressionPage: BookPage() {

    private var progression: Map<String,ComponentBasicText> = mapOf()
    private var offsets: Map<Int,Int> = mapOf()

    @Transient
    private val completionStatus: MutableMap<String, ProgressionChunk> = mutableMapOf()

    override fun build(level: World, entry: BookEntry, builder: BookContentsBuilder, pageNum: Int) {
        super.build(level, entry, builder, pageNum)
        var y = -4
        val yIncrement = (GuiBook.PAGE_HEIGHT + 4)/progression.size
        var index = 0
        for ((adv,text) in progression){
            text.build(builder,this, entry, pageNum)
            text.x = 20
            text.y = y + offsets.getOrDefault(index,0)
            y += yIncrement
            index++
            completionStatus[adv] = ProgressionChunk(text, ClientAdvancements.hasDone(adv))
        }
    }

    override fun onDisplayed(parent: GuiBookEntry, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        for ((adv,chunk) in completionStatus){
            chunk.completed = ClientAdvancements.hasDone(adv)
            chunk.text.onDisplayed(this,parent, left, top)
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, pticks: Float) {
        var y = -4
        val yIncrement = (GuiBook.PAGE_HEIGHT + 4)/progression.size
        for ((index, chunk) in completionStatus.values.withIndex()) {
            val y2 = y + offsets.getOrDefault(index,0)
            RenderSystem.enableBlend()
            if (chunk.completed)
                context.drawTexture(PatchouliCompat.CHECKLIST_BOX, 0, y2, 16f, 0f, 16, 16, 32, 16)
            else
                context.drawTexture(PatchouliCompat.CHECKLIST_BOX, 0, y2, 0f, 0f, 16, 16, 32, 16)
            chunk.text.render(context,this, mouseX, mouseY, pticks)
            y += yIncrement
        }
    }

    private class ProgressionChunk(val text: ComponentBasicText, var completed: Boolean)
}