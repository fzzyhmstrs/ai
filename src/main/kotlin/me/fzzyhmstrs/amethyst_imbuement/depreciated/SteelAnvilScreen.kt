package me.fzzyhmstrs.amethyst_imbuement.depreciated

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.screen.SteelAnvilScreenHandler2
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.ForgingScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class SteelAnvilScreen(handler: SteelAnvilScreenHandler2, inventory: PlayerInventory, title: Text): ForgingScreen<SteelAnvilScreenHandler2>(handler, inventory, title, Identifier("textures/gui/container/anvil.png")) {
    private var player: PlayerEntity? = null
    val wd = (width - backgroundWidth) / 2
    val ht = (height - backgroundHeight) / 2
    private var nameField: TextFieldWidget = TextFieldWidget(textRenderer, wd + 62, ht + 24, 103, 12, TranslatableText("container.repair"))

    override fun handledScreenTick() {
        super.handledScreenTick()
        nameField.tick()
    }

    override fun setup() {
        client!!.keyboard.setRepeatEvents(true)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        nameField = TextFieldWidget(textRenderer, i + 62, j + 24, 103, 12, TranslatableText("container.repair"))
        nameField.setFocusUnlocked(false)
        nameField.setEditableColor(-1)
        nameField.setUneditableColor(-1)
        nameField.setDrawsBackground(false)
        nameField.setMaxLength(50)
        nameField.setChangedListener(Consumer { name: String ->
            this.onRenamed(
                name
            )
        })
        nameField.text = ""
        addSelectableChild(nameField)
        setInitialFocus(nameField)
        nameField.setEditable(false)
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        val string = nameField.text
        this.init(client, width, height)
        nameField.text = string
    }

    override fun removed() {
        super.removed()
        client!!.keyboard.setRepeatEvents(false)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            client!!.player!!.closeHandledScreen()
        }
        return if (nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.isActive) {
            true
        } else super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun onRenamed(name: String) {
        if (name.isEmpty()) {
            return
        }
        var string = name
        val slot = (handler as SteelAnvilScreenHandler2).getSlot(0)
        if (slot != null && slot.hasStack() && !slot.stack.hasCustomName() && string == slot.stack.name.string) {
            string = ""
        }
        handler.setNewItemName(string)
        client?.player?.networkHandler?.sendPacket(RenameItemC2SPacket(string))
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
        val i = (handler as SteelAnvilScreenHandler2).getLevelCost()
        if (i > 0) {
            val text: Text?
            var j = 8453920
            if (!handler.getSlot(2).hasStack()) {
                text = null
            } else {
                text = TranslatableText("container.repair.cost", i)
                if (!handler.getSlot(2).canTakeItems(player)) {
                    j = 0xFF6060
                }
            }
            if (text != null) {
                val k = backgroundWidth - 8 - textRenderer.getWidth(text) - 2
                // val l = 69
                fill(matrices, k - 2, 67, backgroundWidth - 8, 79, 0x4F000000)
                textRenderer.drawWithShadow(matrices, text, k.toFloat(), 69.0f, j)
            }
        }
    }

    override fun renderForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        nameField.render(matrices, mouseX, mouseY, delta)
    }

    override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {
        if (slotId == 0) {
            nameField.text = if (stack.isEmpty) "" else stack.name.string
            nameField.setEditable(!stack.isEmpty)
            focused = nameField
        }
    }

    init {
        player = inventory.player
        titleX = 60
    }

}