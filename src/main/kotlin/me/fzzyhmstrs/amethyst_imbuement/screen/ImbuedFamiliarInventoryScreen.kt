package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.screen.widget.FamiliarSettingsWidget
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.passive.CatVariant
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class ImbuedFamiliarInventoryScreen(handler: ImbuedFamiliarInventoryScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<ImbuedFamiliarInventoryScreenHandler>(handler,playerInventory,title) {

    companion object{
        val texture = Identifier(AI.MOD_ID,"textures/gui/container/familiar_inventory_gui.png")
    }

    private var i = (width - backgroundWidth)/2
    private var j = (height - backgroundHeight)/2
    var cat = handler.cat.get()
    var name = title
    var followMode = handler.followMode.get()
    var attackMode = handler.attackMode.get()
    var familiarToRender: ImbuedFamiliarEntity? = null

    private val settingsWidget: FamiliarSettingsWidget by lazy{
        createFamiliarSettingsWidget()
    }
    private var settingsActive = false
    private val settingsButtonWidget: TexturedButtonWidget by lazy{
        createSettingsButtonWidget()
    }

    /////////////////////////////

    override fun init() {
        super.init()
        i = (width - backgroundWidth)/2
        j = (height - backgroundHeight)/2
        addDrawableChild(settingsButtonWidget)
        val entity = client?.world?.getEntityById(handler.familiarId)
        if (entity is ImbuedFamiliarEntity) {
            familiarToRender = entity
        }
    }

    private fun createFamiliarSettingsWidget(): FamiliarSettingsWidget{
        return FamiliarSettingsWidget((width-113)/2,(height-52)/2-40,getCatFromMap(cat),name,followMode,attackMode,settingsListener)
    }

    private val settingsListener = object: FamiliarSettingsWidget.SettingsUpdateListener{
        override fun onVariant(variant: CatVariant) {
            cat = ImbuedFamiliarInventoryScreenHandler.catMap.getOrDefault(variant,0)
            sendUpdates()
        }
        override fun onFollowMode(follow: Int) {
            followMode = follow
            sendUpdates()
        }
        override fun onAttackMode(attack: Int) {
            attackMode = attack
            sendUpdates()
        }
        override fun onNaming(name: Text) {
            this@ImbuedFamiliarInventoryScreen.name = name
            sendUpdates()
        }
        override fun onClose() {
            println("closing")
            this@ImbuedFamiliarInventoryScreen.remove(settingsWidget)
            this@ImbuedFamiliarInventoryScreen.addDrawableChild(settingsButtonWidget)
            settingsActive = false
            handler.enableFamiliarSlots()
            sendUpdates()
        }
    }

    private fun createSettingsButtonWidget(): TexturedButtonWidget{
        return TexturedButtonWidget(i + 7,j + 53,18,18,18,220,texture) { button -> openSettingWindow(button) }
    }

    private fun openSettingWindow(button: ButtonWidget){
        addDrawableChild(settingsWidget)
        remove(settingsButtonWidget)
        focused = settingsWidget
        settingsActive = true
        handler.disableFamiliarSlots()
    }


    fun getCatFromMap(index: Int): CatVariant{
        for (catEntry in ImbuedFamiliarInventoryScreenHandler.catMap){
            if (catEntry.value == index) return catEntry.key
        }
        return CatVariant.ALL_BLACK
    }

    ///////////////////////////////


    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        textRenderer.draw(matrices, name, titleX.toFloat(), titleY.toFloat(), 0x404040)
        textRenderer.draw(
            matrices,
            playerInventoryTitle, playerInventoryTitleX.toFloat(), playerInventoryTitleY.toFloat(), 0x404040
        )
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, texture)
        this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)
        this.drawTexture(matrices,i+7,j+17,0,220,18,18)
        if (!settingsActive) {
            for (k in 0 until handler.getChestSlots()) {
                val xOffset = k / 3
                val yOffset = k % 3
                this.drawTexture(matrices, i + 79 + xOffset * 18, j + 17 + yOffset * 18, 0, 166, 18, 18)
            }
            if (familiarToRender != null) {
                InventoryScreen.drawEntity(
                    i + 51,
                    j + 60,
                    17,
                    (i + 51).toFloat() - mouseX,
                    (j + 75 - 50).toFloat() - mouseY,
                    familiarToRender
                )
            }
        }
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        i = (width-backgroundWidth)/2
        j = (height-backgroundHeight)/2
        settingsButtonWidget.setPos(i + 7,j + 53)
        remove(settingsWidget)
        settingsActive = false
        handler.enableFamiliarSlots()
        super.resize(client, width, height)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return !settingsActive
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(settingsActive) return settingsWidget.keyPressed(keyCode, scanCode, modifiers)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun sendUpdates(){
        val buf = PacketByteBufs.create()
        buf.writeString(name.string)
        val id = Registry.CAT_VARIANT.getId(getCatFromMap(cat))?: Identifier("empty")
        buf.writeIdentifier(id)
        buf.writeByte(followMode)
        buf.writeByte(attackMode)
        handler.processUpdate(name.string,id,ImbuedFamiliarEntity.FollowMode.FOLLOW.fromIndex(followMode), ImbuedFamiliarEntity.AttackMode.ATTACK.fromIndex(attackMode))
        println("sent update!")
        ClientPlayNetworking.send(ImbuedFamiliarInventoryScreenHandler.UPDATE_FAMILIAR,buf)
    }

}