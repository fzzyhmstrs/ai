package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.client.book.BookPage
import vazkii.patchouli.client.book.gui.BookTextRenderer
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.template.TemplateComponent
import java.util.function.UnaryOperator

class ComponentBasicText: TemplateComponent() {

    var text: IVariable? = null

    @Transient
    var actualText: Text? = null
    @Transient
    var textRenderer: BookTextRenderer? = null

    override fun onVariablesAvailable(lookup: UnaryOperator<IVariable>) {
        super.onVariablesAvailable(lookup)
        text?.let { actualText = lookup.apply(it).`as`(Text::class.java) }
    }

    override fun onDisplayed(page: BookPage?, parent: GuiBookEntry?, left: Int, top: Int) {
        if (text == null){
            text = IVariable.wrap("")
        }
        text?.let { actualText = it.`as`(Text::class.java) }
        textRenderer = BookTextRenderer(parent, actualText, x, y)
    }

    override fun render(graphics: DrawContext?, page: BookPage?, mouseX: Int, mouseY: Int, pticks: Float) {
        textRenderer?.render(graphics, mouseX, mouseY)
    }

    override fun mouseClicked(page: BookPage?, mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return textRenderer?.click(mouseX, mouseY, mouseButton) ?: super.mouseClicked(page, mouseX, mouseY, mouseButton)
    }
}