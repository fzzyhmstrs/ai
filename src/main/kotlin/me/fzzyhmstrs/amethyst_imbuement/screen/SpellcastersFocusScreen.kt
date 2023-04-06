package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.Util


@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER", "SpellCheckingInspection")
class SpellcastersFocusScreen(handler: SpellcastersFocusScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<SpellcastersFocusScreenHandler>(handler, playerInventory, title) {

    private val texture = Identifier(AI.MOD_ID,"textures/gui/container/spellcasters_focus_gui.png")
    private val flavor = AcText.translatable("container.spellcasters_focus.hint").formatted(Formatting.ITALIC)
    private lateinit var button1: OptionButtonWidget
    private lateinit var button2: OptionButtonWidget
    private lateinit var button3: OptionButtonWidget

    init {
        backgroundWidth = 200
        backgroundHeight = 128
    }

    override fun init() {
        super.init()
        button1 = OptionButtonWidget(((width - backgroundWidth) / 2) + 8, ((height - backgroundHeight) / 2) + 34,AcText.translatable("container.spellcasters_focus.option1"),0,handler.options[0],handler)
        button2 = OptionButtonWidget(((width - backgroundWidth) / 2) + 71, ((height - backgroundHeight) / 2) + 34,AcText.translatable("container.spellcasters_focus.option2"),1,handler.options[1],handler)
        button3 = OptionButtonWidget(((width - backgroundWidth) / 2) + 134, ((height - backgroundHeight) / 2) + 34,AcText.translatable("container.spellcasters_focus.option3"),2,handler.options[2],handler)
        addDrawableChild(button1)
        addDrawableChild(button2)
        addDrawableChild(button3)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, texture)
        this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)
        DrawableHelper.drawCenteredTextWithShadow(matrices,MinecraftClient.getInstance().textRenderer,title.asOrderedText(),i + backgroundWidth/2,j + 7,0xFFFFFF)
        DrawableHelper.drawCenteredTextWithShadow(matrices,MinecraftClient.getInstance().textRenderer,flavor.asOrderedText(),i + backgroundWidth/2,j + 20,0xFFFFFF)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        for (j in 0..2) {
            if (!isPointWithinBounds(8 + 63 * j, 34, 58, 86, mouseX.toDouble(), mouseY.toDouble())) continue
            val mods = handler.options[j]

            val tooltipText: MutableList<Text> = mutableListOf(AcText.translatable("container.spellcasters_focus.option${j+1}"))
            tooltipText.add(AcText.empty())
            val context = if (client?.options?.advancedItemTooltips == true){
                TooltipContext.Default.ADVANCED
            } else {
                TooltipContext.Default.BASIC
            }
            ModifierHelper.addModifierTooltip(mods,tooltipText,context)
            this.renderTooltip(matrices, tooltipText, mouseX, mouseY)
            break
        }
    }

    private class OptionButtonWidget(
        x: Int,
        y: Int,
        message: Text,
        private val id: Int,
        private val mods: List<Identifier>,
        private val handler: SpellcastersFocusScreenHandler)
        :
        PressableWidget(x,y,58,86,message)
    {
        private val texture = Identifier(AI.MOD_ID,"textures/gui/container/spellcasters_focus_gui.png")
        private var selected = false
        private var finalSelected = false

        override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
            appendDefaultNarrations(builder)
            for (mod in mods){
                builder.put(NarrationPart.HINT,AcText.translatable(Util.createTranslationKey("enchantment",mod)))
            }
        }

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            val minecraftClient = MinecraftClient.getInstance()
            val textRenderer = minecraftClient.textRenderer
            RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
            RenderSystem.setShaderTexture(0, texture)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()
            val i = if (hovered || selected) 1 else 0
            this.drawTexture(matrices, x, y, 0 + 58 * i, 128, width , height)
            ClickableWidget.drawCenteredTextWithShadow(matrices,textRenderer,message.asOrderedText(),x + (width / 2),y + 5,0xFFFFFF)
            val drawDots: Boolean
            val range = if (mods.size <= 3) {
                drawDots = false
                mods.indices
            }else {
                drawDots = true
                0..1
            }
            RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
            RenderSystem.setShaderTexture(0, texture)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            for (j in range){
                this.drawTexture(matrices, x + 9, y + 18 + 21 * j, 116, 128, 40 , 18)
            }
            if (drawDots){
                this.drawTexture(matrices, x + 9, y + 18 + 21 * 2, 116, 146, 40 , 18)
            }
            for (j in range){
                val mod = mods[j]
                RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
                RenderSystem.setShaderTexture(0, Identifier(mod.namespace,"textures/gui/patchouli/mod_spotlights/${mod.path}.png"))
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
                DrawableHelper.drawTexture(matrices, x + 13, y + 19 + 21 * j,0, 0f, 0f, 32 , 16,32,16)
            }

        }

        override fun onPress() {
            if (!selected) {
                selected = true
            } else {
                if (!finalSelected) {
                    finalSelected = true
                    MinecraftClient.getInstance().interactionManager?.clickButton(handler.syncId, id)
                }
            }
        }
    }
}
