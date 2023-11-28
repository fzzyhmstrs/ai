package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterKeybind
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterNetworking
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper
import org.joml.Vector2i

object SpellRadialHud: Screen(AcText.empty()) {

    private var spellPositions: List<Entry> = listOf()
    private var activePosition: Entry? = null
    private val hudTexture = Identifier(AI.MOD_ID,"textures/gui/scepter_hud.png")
    private val fallbackSpellTextureId = Identifier(AI.MOD_ID,"textures/spell/missing_no.png")
    private val fallbackSpellName = AcText.translatable("scepter.hud.fallback_name")
    private var cachedEnchants: List<ScepterAugment> = listOf()
    private var cachedActiveSpell: Identifier? = null
    private var cachedWidth = 0
    private var cachedHeight = 0
    private var cachedDistance = 80
    private val fillColor = ColorHelper.Argb.getArgb(128,44, 44,44)

    private var dirty = false

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateActivePosition(mouseX.toInt(), mouseY.toInt())
        if (activePosition != null){
            val buf = PacketByteBufs.create()
            buf.writeString(activePosition?.spell ?: "fail:fail")
            ClientPlayNetworking.send(RegisterNetworking.UPDATE_ACTIVE_SPELL,buf)
            MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            this.close()
            return true
        }
        this.close()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldPause(): Boolean {
        return false
    }

    fun markDirty(){
        dirty = true
    }
    fun isDirty(): Boolean{
        return dirty
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (RegisterKeybind.SCEPTER_RADIAL_MENU.matchesKey(keyCode, scanCode))
            this.close()
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        super.resize(client, width, height)
        dirty = true
        cachedEnchants = listOf()
        cachedActiveSpell = null
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        updateActivePosition(mouseX, mouseY)
        renderBackground(context)
        for (position in spellPositions){
            context.drawTexture(hudTexture,position.position.x - 16,position.position.y - 16, 143,72 + if (position.active) { if(position.current) 99 else 33 } else { if (position.current) 66 else 0 },33,33)
            if (position.active){
                if (position.left){
                    context.drawTexture(hudTexture,position.position.x + 18,position.position.y - 6, 128,72 ,15,12)
                } else {
                    context.drawTexture(hudTexture,position.position.x - 32,position.position.y - 6, 128,84 ,15,12)
                }
            }
            ScepterHud.drawIcon(context,position.texture,position.position.x - 15,position.position.y - 15)
            //context.drawTexture(position.texture,position.position.x - 15,position.position.y - 15, 32,32,0f,0f,32,32,32,32)
            if (position.left){
                context.drawText(textRenderer,position.name,position.position.x - textRenderer.getWidth(position.name) - 18,position.position.y - 4 + position.offset,0xFFFFFF,true)
            } else {
                context.drawText(textRenderer,position.name,position.position.x + 19,position.position.y - 4 + position.offset,0xFFFFFF,true)
            }
        }
    }
    override fun renderBackground(context: DrawContext) {
        context.fill(0, 0, width, height, fillColor)
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

    fun distanceOffset(): Int {
        return AiConfig.hud.spellHudSpacing.get()
    }
    fun distanceOffsetSquared(): Int {
        return distanceOffset() * distanceOffset()
    }

    fun update(enchants: List<ScepterAugment>, activeAugmentId: Identifier, activeAugmentString: String){
        val bl = MinecraftClient.getInstance().window.width == cachedWidth && MinecraftClient.getInstance().window.height == cachedHeight
        val bl2 = distanceOffset() == cachedDistance
        if (enchants == cachedEnchants && bl && bl2){
            if (activeAugmentId == cachedActiveSpell) return
            for (position in spellPositions){
                if (position.spell == activeAugmentString) {
                    position.current = true
                    cachedActiveSpell = activeAugmentId
                } else {
                    position.current = false
                }
            }
            return
        }
        dirty = false
        cachedDistance = distanceOffset()
        cachedEnchants = enchants
        cachedWidth = MinecraftClient.getInstance().window.width
        cachedHeight = MinecraftClient.getInstance().window.height
        val list: MutableList<Entry> = mutableListOf()
        val size = enchants.size
        val centerX = MinecraftClient.getInstance().window.scaledWidth/2
        val centerY = MinecraftClient.getInstance().window.scaledHeight/2
        when (size) {
            1 -> {
                val textureIdTemp = Identifier(enchants[0].id.namespace,"textures/spell/${enchants[0].id.path}.png")
                val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
                val name = enchants[0].getName(1) ?: fallbackSpellName
                val style = RegisterTag.getStyleFromSpell(enchants[0])
                list.add(Entry(Vector2i(centerX - distanceOffset(),centerY),textureId,name.copyContentOnly().formatted(style.color),0,enchants[0].id.toString(),false,enchants[0].id == activeAugmentId))
            }
            2 -> {
                val textureIdTemp = Identifier(enchants[0].id.namespace,"textures/spell/${enchants[0].id.path}.png")
                val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
                val name = enchants[0].getName(1) ?: fallbackSpellName
                val style = RegisterTag.getStyleFromSpell(enchants[0])
                list.add(Entry(Vector2i(centerX - distanceOffset(),centerY),textureId,name.copyContentOnly().formatted(style.color),0,enchants[0].id.toString(),true,false,enchants[0].id == activeAugmentId))
                val textureIdTemp2 = Identifier(enchants[1].id.namespace,"textures/spell/${enchants[1].id.path}.png")
                val textureId2 = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp2).isPresent) textureIdTemp2 else fallbackSpellTextureId
                val name2 = enchants[1].getName(1) ?: fallbackSpellName
                val style2 = RegisterTag.getStyleFromSpell(enchants[1])
                list.add(Entry(Vector2i(centerX + distanceOffset(),centerY),textureId2,name2.copyContentOnly().formatted(style2.color),0,enchants[1].id.toString(),false,false,enchants[1].id == activeAugmentId))
            }
            else -> {
                var i = 0
                val rightSize = size/2
                val leftSize = size - rightSize
                val rightAngle = getAngle(rightSize,centerY)
                var rightStartAngle = (rightAngle * (rightSize - 1))/2f
                var rightOffset = -rightSize / 2
                for (j in 1..rightSize){
                    val rightX = (centerX + (MathHelper.cos(rightStartAngle) * distanceOffset())).toInt()
                    val rightY = (centerY - (MathHelper.sin(rightStartAngle) * distanceOffset())).toInt()
                    rightStartAngle -= rightAngle
                    val textureIdTemp = Identifier(enchants[i].id.namespace,"textures/spell/${enchants[i].id.path}.png")
                    val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
                    val name = enchants[i].getName(1) ?: fallbackSpellName
                    val style = RegisterTag.getStyleFromSpell(enchants[i])
                    list.add(Entry(Vector2i(rightX,rightY),textureId,name.copyContentOnly().formatted(style.color),rightOffset * 2,enchants[i].id.toString(),false,false,enchants[i].id == activeAugmentId))
                    rightOffset++
                    i++
                }
                val leftAngle = getAngle(leftSize,centerY)
                var leftStartAngle = (leftAngle * (leftSize - 1))/2f
                var leftOffset = -leftSize/2
                for (j in 1..leftSize){
                    val leftX = (centerX - (MathHelper.cos(leftStartAngle) * distanceOffset())).toInt()
                    val leftY = (centerY - (MathHelper.sin(leftStartAngle) * distanceOffset())).toInt()
                    leftStartAngle -= leftAngle
                    val textureIdTemp = Identifier(enchants[i].id.namespace,"textures/spell/${enchants[i].id.path}.png")
                    val textureId = if (MinecraftClient.getInstance().resourceManager.getResource(textureIdTemp).isPresent) textureIdTemp else fallbackSpellTextureId
                    val name = enchants[i].getName(1) ?: fallbackSpellName
                    val style = RegisterTag.getStyleFromSpell(enchants[i])
                    list.add(Entry(Vector2i(leftX,leftY),textureId,name.copyContentOnly().formatted(style.color),leftOffset * 2,enchants[i].id.toString(),true,false,enchants[i].id == activeAugmentId))
                    leftOffset++
                    i++
                }
            }
        }
        spellPositions = list
    }
    //                                0  1  2        3        4        5        6        7        8
    private val angles = floatArrayOf(0f,0f,0.87266f,0.69813f,0.61086f,0.52359f,0.45378f,0.40143f,0.36652f)

    private fun getAngle(size: Int, centerY: Int): Float{
        if (size == 1) return 0f
        var angle = if (size >= angles.size) angles[6] else angles[size]
        var startAngle = (angle * (size - 1))/2f
        if (startAngle > 1.39626f) {
            angle = 2.7925267f / (size - 1)
            startAngle = 1.39626f
        }
        val y = MathHelper.sin(startAngle) * distanceOffset()
        if (y > centerY - 25){
            val y2 = centerY - 25
            val x2 = MathHelper.sqrt((distanceOffsetSquared() - y2 * y2).toFloat())
            return (MathHelper.atan2(y2.toDouble(),x2.toDouble()).toFloat() * 2f)/(size-1)
        }
        return angle
    }

    private data class Entry(val position: Vector2i, val texture: Identifier, val name: Text,val offset: Int, val spell: String, val left: Boolean, var active: Boolean = false, var current: Boolean = false)

}