package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterNetworking
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import org.joml.Vector2i

object SpellRadialHud: Screen(AcText.empty()) {

    private var spellPositions: List<Entry> = listOf()
    private var activePosition: Entry? = null
    private val hudTexture = Identifier(AI.MOD_ID,"textures/gui/scepter_hud.png")
    private val fallbackSpellTextureId = Identifier(AI.MOD_ID,"textures/spell/missing_no.png")
    private var cachedEnchants: List<Identifier> = listOf()

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateActivePosition(mouseX.toInt(), mouseY.toInt())
        if (activePosition != null){
            val buf = PacketByteBufs.create()
            buf.writeString(activePosition?.spell ?: "fail:fail")
            ClientPlayNetworking.send(RegisterNetworking.UPDATE_ACTIVE_SPELL,buf)
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        updateActivePosition(mouseX, mouseY)
        renderBackground(context)
        for (position in spellPositions){
            context.drawTexture(hudTexture,position.position.x - 15,position.position.y - 15, 144,72 + if (position.active) 33 else 0,33,33)
        }
    }
    override fun renderBackground(context: DrawContext) {
        context.fill(0, 0, width, height, -435154928)
    }

    private fun updateActivePosition(mouseX: Int, mouseY: Int){
        var closest = -1
        var distance = Long.MAX_VALUE
        for (i in spellPositions.indices){
            val distanceChk = spellPositions[i].position.distanceSquared(mouseX,mouseY)
            if (distanceChk < distance){
                closest = i
                distance = distanceChk
            }
            spellPositions[i].active = false
        }
        if (closest != -1){
            activePosition = spellPositions[closest]
            spellPositions[closest].active = true
        }
    }

    val distanceOffset = 95
    val distanceOffsetSquared = distanceOffset * distanceOffset

    fun update(enchants: List<Identifier>, activeAugmentId: Identifier, activeAugmentString: String){
        if (enchants == cachedEnchants){
            for (position in spellPositions){
                if (position.spell == activeAugmentString) {
                    position.current = true
                    break
                }
            }
            return
        }
        cachedEnchants = enchants
        val list: MutableList<Entry> = mutableListOf()
        val size = enchants.size
        val centerX = MinecraftClient.getInstance().window.scaledWidth/2
        val centerY = MinecraftClient.getInstance().window.scaledHeight/2
        if (size == 1){
            val textureIdTemp = Identifier(enchants[0].namespace,"textures/spell/${enchants[0].path}.png")
            val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
            list.add(Entry(Vector2i(centerX - distanceOffset,centerY),textureId,enchants[0].toString(),false,enchants[0] == activeAugmentId))
        } else if (size == 2){
            val textureIdTemp = Identifier(enchants[0].namespace,"textures/spell/${enchants[0].path}.png")
            val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
            list.add(Entry(Vector2i(centerX - distanceOffset,centerY),textureId,enchants[0].toString(),false,enchants[0] == activeAugmentId))
            val textureIdTemp2 = Identifier(enchants[1].namespace,"textures/spell/${enchants[1].path}.png")
            val textureId2 = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp2).isPresent) textureIdTemp2 else fallbackSpellTextureId
            list.add(Entry(Vector2i(centerX + distanceOffset,centerY),textureId2,enchants[1].toString(),false,enchants[1] == activeAugmentId))
        } else {
            var i = 0
            val rightSize = size/2
            val leftSize = size - rightSize

        }
    }

    private val angles = floatArrayOf(0f,0f,0.87266f,0.69813f,0.52359f,0.43633f,0.34907f)

    private fun getAngle(size: Int, centerY: Int): Float{
        if (size == 1) return 0f
        val angle = if (size >= angles.size) angles[6] else angles[size]
        val startAngle = (angle * (size - 1))/2f
        val y = MathHelper.sin(startAngle) * distanceOffset
        if (y > centerY - 25){
            val y2 = centerY - 25
            val x2 = MathHelper.sqrt((distanceOffsetSquared - y2 * y2).toFloat())
            return (MathHelper.atan2(y2.toDouble(),x2.toDouble()).toFloat() * 2f)/(size-1)
        }
        return angle
    }

    private data class Entry(val position: Vector2i, val texture: Identifier, val spell: String, var active: Boolean = false, var current: Boolean = false)

}