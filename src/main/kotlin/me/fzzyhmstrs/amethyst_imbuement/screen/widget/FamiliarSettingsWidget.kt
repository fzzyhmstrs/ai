package me.fzzyhmstrs.amethyst_imbuement.screen.widget

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuedFamiliarInventoryScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuedFamiliarInventoryScreenHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.passive.CatVariant
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class FamiliarSettingsWidget(x: Int, y: Int,
                             private var variant: CatVariant,
                             private var name: Text,
                             follow: Int,
                             attack: Int,
                             private val listener: SettingsUpdateListener):
    ClickableWidget(x,y,113,52, AcText.empty()) {

    private var followMode = ImbuedFamiliarEntity.FollowMode.FOLLOW.fromIndex(follow)
    private var attackMode = ImbuedFamiliarEntity.AttackMode.ATTACK.fromIndex(attack)
    private var textIsEmpty = name == AcText.empty()
    private val client = MinecraftClient.getInstance()

    private val textField: TextFieldWidget by lazy{
        createNameFieldWidget()
    }
    private val catWidgets: Map<Int, CatHeadWidget> by lazy{
        createCatHeads()
    }
    private val followButton: TexturedButtonWidget by lazy {
        createFollowButton()
    }
    private val attackButton: TexturedButtonWidget by lazy {
        createAttackButton()
    }

    ////////////////////////

    private fun createNameFieldWidget(): TextFieldWidget {
        val client = MinecraftClient.getInstance()
        val widget = TextFieldWidget(client.textRenderer, x + 7, y + 9, 99, 12, name)
        widget.setFocusUnlocked(true)
        widget.setEditableColor(-1)
        widget.setUneditableColor(-1)
        widget.setDrawsBackground(false)
        widget.setMaxLength(50)
        widget.text = name.string
        widget.setChangedListener { string -> onNameChanged(string) }
        return widget
    }

    private fun onNameChanged(customName: String){
        name = AcText.literal(customName)
        textIsEmpty = name == AcText.empty()
        listener.onNaming(name)
    }

    private fun createCatHeads(): Map<Int, CatHeadWidget>{
        val map: MutableMap<Int, CatHeadWidget> = mutableMapOf()
        ImbuedFamiliarInventoryScreenHandler.catMap.forEach{ (cat, index) ->
            map[index] = CatHeadWidget(x + 13 + 8*index,y + 39,cat == variant,cat,index) {newCat: Pair<Int,CatVariant> -> onCat(newCat)}
        }
        return map
    }

    private fun onCat(cat: Pair<Int,CatVariant>){
        println(cat)
        variant = cat.second
        catWidgets.forEach { (_, catHead) ->
            catHead.isToggled = false
        }
        catWidgets[cat.first]?.isToggled = true
        listener.onVariant(variant)
    }

    private fun createFollowButton(): TexturedButtonWidget{
        val widget = object: TexturedButtonWidget(x+5, y+23,50,14,36,220,ImbuedFamiliarInventoryScreen.texture, { button -> onFollow(button) }){
            override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                super.renderButton(matrices, mouseX, mouseY, delta)
                drawCenteredText(matrices, client.textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFFFFFF)
            }
        }
        widget.message = AcText.translatable(followMode.key)
        return widget
    }

    private fun onFollow(button: ButtonWidget){
        followMode = followMode.cycle()
        followButton.message = AcText.translatable(followMode.key)
        listener.onFollowMode(followMode.index)
    }

    private fun createAttackButton(): TexturedButtonWidget{
        val widget = object: TexturedButtonWidget(x+58, y+23,50,14,36,220,ImbuedFamiliarInventoryScreen.texture, { button -> onAttack(button) }){
            override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                super.renderButton(matrices, mouseX, mouseY, delta)
                drawCenteredText(matrices, client.textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFFFFFF)
            }
        }
        widget.message = AcText.translatable(attackMode.key)
        return widget
    }

    private fun onAttack(button: ButtonWidget){
        attackMode = attackMode.cycle()
        attackButton.message = AcText.translatable(attackMode.key)
        listener.onFollowMode(attackMode.index)
    }


    //////////////////////////

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!active || !visible) {
            return false
        }
        return if (textField.mouseClicked(mouseX, mouseY, button)){
            true
        } else if (clickCatHeads(mouseX, mouseY, button)){
            true
        } else if (followButton.mouseClicked(mouseX, mouseY, button)){
            true
        } else {
            attackButton.mouseClicked(mouseX, mouseY, button) || this.clicked(mouseX, mouseY)
        }
    }

    private fun clickCatHeads(mouseX: Double, mouseY: Double, button: Int): Boolean{
        for (catHead in catWidgets){
            if (catHead.value.mouseClicked(mouseX, mouseY, button)) return true
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE){
            listener.onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices, client, mouseX, mouseY)
        textField.render(matrices, mouseX, mouseY, delta)
        catWidgets.forEach { (_, catWidget) ->
            catWidget.render(matrices, mouseX, mouseY, delta)
        }
        followButton.render(matrices, mouseX, mouseY, delta)
        attackButton.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderBackground(matrices: MatrixStack?, client: MinecraftClient, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, ImbuedFamiliarInventoryScreen.texture)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        this.drawTexture(matrices,x,y,90,166,width,height)
        if (textIsEmpty){
            this.drawTexture(matrices,x+5,y+5,90,234,103,16)
        } else {
            this.drawTexture(matrices,x+5,y+5,90,218,103,16)
        }
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
    }

    /////////////////////////

    interface SettingsUpdateListener{
        fun onVariant(variant: CatVariant)
        fun onFollowMode(follow: Int)
        fun onAttackMode(attack: Int)
        fun onNaming(name: Text)
        fun onClose()
    }
}