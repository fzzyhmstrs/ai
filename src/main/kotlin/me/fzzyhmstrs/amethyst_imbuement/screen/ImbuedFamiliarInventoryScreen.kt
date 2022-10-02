package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.screen.widget.FamiliarSettingsWidget
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
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
        return FamiliarSettingsWidget((width-113)/2,(height-52)/2-32,getCatFromMap(cat),name,followMode,attackMode,settingsListener)
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
            this@ImbuedFamiliarInventoryScreen.remove(settingsWidget)
            settingsActive = false
            sendUpdates()
        }
    }

    private fun createSettingsButtonWidget(): TexturedButtonWidget{
        return TexturedButtonWidget(i + 7,j + 53,18,18,18,220,texture) { button -> openSettingWindow(button) }
    }

    private fun openSettingWindow(button: ButtonWidget){
        addDrawableChild(settingsWidget)
        focused = settingsWidget
        settingsActive = true
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
        TODO("Not yet implemented")
    }

    override fun shouldCloseOnEsc(): Boolean {
        return !settingsActive
    }

    override fun close() {
        sendUpdates()
        super.close()
    }

    private fun sendUpdates(){
        val buf = PacketByteBufs.create()
        buf.writeString(name.string)
        buf.writeIdentifier(Registry.CAT_VARIANT.getId(getCatFromMap(cat)))
        buf.writeByte(followMode)
        buf.writeByte(attackMode)
        ClientPlayNetworking.send(ImbuedFamiliarInventoryScreenHandler.UPDATE_FAMILIAR,buf)
    }

}