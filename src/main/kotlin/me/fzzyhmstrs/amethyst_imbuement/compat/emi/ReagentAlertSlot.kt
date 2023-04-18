package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import com.mojang.blaze3d.systems.RenderSystem
import dev.emi.emi.EmiPort
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.widget.SlotWidget
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.apache.commons.compress.utils.Lists

class ReagentAlertSlot(stack:EmiIngredient, output: EmiIngredient, x: Int, y: Int): SlotWidget(stack, x, y) {

    private val containsReagent: Boolean

    init{
        var bl = false
        if (stack.emiStacks.size > 1) {
            for (emiStack in output.emiStacks) {
                if (emiStack.itemStack.item is Reactant) {
                    bl = true
                    break
                }
            }
        }
        if (!bl){
            containsReagent = false
        } else {
            var bl1 = false
            for (emiStack in stack.emiStacks) {
                if (emiStack.itemStack.item is Reagent) {
                    bl1 = true
                    break
                }
            }
            containsReagent = bl1
        }
    }

    override fun getTooltip(mouseX: Int, mouseY: Int): MutableList<TooltipComponent> {
        val list: ArrayList<TooltipComponent> = Lists.newArrayList()
        if (containsReagent){
            list.add(TooltipComponent.of(EmiPort.ordered(alertText)))
            list.add(TooltipComponent.of(EmiPort.ordered(alertTextDesc1)))
            list.add(TooltipComponent.of(EmiPort.ordered(alertTextDesc2)))
            list.add(TooltipComponent.of(EmiPort.ordered(AcText.empty())))
        }
        list.addAll(super.getTooltip(mouseX, mouseY))
        return list
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        if (containsReagent) {
            val bounds = getBounds()
            EmiPort.setPositionTexShader()
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.setShaderTexture(0, alertTexture)
            RenderSystem.disableDepthTest()
            DrawableHelper.drawTexture(matrices, bounds.x(), bounds.y() - 1, 19, 19, 121f, 231f, 19, 19, 256, 256)
            RenderSystem.enableDepthTest()
        }
    }

    companion object{
        private val alertText = AcText.translatable("emi.alert_slot.alert").formatted(Formatting.BOLD,Formatting.ITALIC,Formatting.GOLD)
        private val alertTextDesc1 = AcText.translatable("emi.alert_slot.alert.desc1").formatted(Formatting.GOLD)
        private val alertTextDesc2 = AcText.translatable("emi.alert_slot.alert.desc2").formatted(Formatting.GOLD)
        private val alertTexture = Identifier(AI.MOD_ID,"textures/gui/container/imbuing_table_gui.png")
    }

}