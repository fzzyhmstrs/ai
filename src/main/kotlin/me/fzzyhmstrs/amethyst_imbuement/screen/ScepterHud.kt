package me.fzzyhmstrs.amethyst_imbuement.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.amethyst_core.item_util.ScepterLike
import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import org.joml.Matrix4f
import org.joml.Vector2i
import kotlin.math.abs
import kotlin.math.min

object ScepterHud {
    private val fallbackV2i = Vector2i(110,180)
    private val hudTexture = Identifier(AI.MOD_ID,"textures/gui/scepter_hud.png")
    private val fallbackSpellTextureId = Identifier(AI.MOD_ID,"textures/spell/missing_no.png")
    private val fallbackSpellName = AcText.translatable("scepter.hud.fallback_name")

    private var tick: Long = 0L
    private var onTick = false

    private var activeStack: ItemStack = ItemStack.EMPTY
    private var activeStackSlot: Int = -1000
    private var activeSpellTextureId: Identifier = fallbackSpellTextureId
    private var activeSpellStyle = RegisterTag.TagStyle.EMPTY
    private var activeSpell: ScepterAugment? = null

    private var manaFraction = 54
    private var manaFractionDirty = 54
    private val manaChangeAnimator = ElementAnimator(mapOf(0 to Vector2i(55,4),1 to Vector2i(55,4), 2 to Vector2i(55,8), 3 to Vector2i(55,12), 4 to Vector2i(55,16)),4)

    private var cooldownReadyAnimator = ElementAnimator(mapOf(0 to Vector2i(0,8), 1 to Vector2i(0,12), 2 to Vector2i(0,8), 3 to Vector2i(0,12), 4 to Vector2i(0,8)),4)
    private var coolingDownPips = 0
    private var cooldownInProgress = false

    private var furyFraction = 0
    private var furyFractionDirty = 0
    private var furyLevel = 1
    private var furyLevelDirty = false
    private val furyChangeAnimator = ElementAnimator(mapOf(0 to Vector2i(55,23),1 to Vector2i(55,23), 2 to Vector2i(55,24), 3 to Vector2i(55,25), 4 to Vector2i(55,26)),4)

    private var graceFraction = 0
    private var graceFractionDirty = 0
    private var graceLevel = 1
    private var graceLevelDirty = false
    private val graceChangeAnimator = ElementAnimator(mapOf(0 to Vector2i(55,23),1 to Vector2i(55,23), 2 to Vector2i(55,24), 3 to Vector2i(55,25), 4 to Vector2i(55,26)),4)

    private var witFraction = 0
    private var witFractionDirty = 0
    private var witLevel = 1
    private var witLevelDirty = false
    private val witChangeAnimator = ElementAnimator(mapOf(0 to Vector2i(55,23),1 to Vector2i(55,23), 2 to Vector2i(55,24), 3 to Vector2i(55,25), 4 to Vector2i(55,26)),4)

    private fun refreshHudCache(stack: ItemStack, activeSpell: String, playerEntity: PlayerEntity) {
        activeStack = stack
        val slotTest = playerEntity.inventory.selectedSlot
        val bl = if (slotTest != activeStackSlot){
            activeStackSlot = slotTest
            false
        } else {
            true
        }
        val activeSpellIdTemp = Identifier(activeSpell)
        val activeAugment = Registries.ENCHANTMENT.get(activeSpellIdTemp)
        activeSpellStyle = if(activeAugment is ScepterAugment) {
            if (activeAugment != this.activeSpell) {
                cooldownInProgress = false
                coolingDownPips = 0
                cooldownReadyAnimator.kill()
            }
            updateSpellRadialHudMap(stack, activeSpellIdTemp, activeSpell)
            this.activeSpell = activeAugment
            RegisterTag.getStyleFromSpell(activeAugment)
        } else {
            this.activeSpell = null
            RegisterTag.TagStyle.EMPTY
        }
        if (!AiConfig.hud.showHud.get()) return
        val activeSpellTextureIdTemp = Identifier(activeSpellIdTemp.namespace,"textures/spell/${activeSpellIdTemp.path}.png")
        activeSpellTextureId = if (MinecraftClient.getInstance().resourceManager.getResource(activeSpellTextureIdTemp).isPresent) activeSpellTextureIdTemp else fallbackSpellTextureId
        val manaFractionTemp = (((activeStack.maxDamage - activeStack.damage).toFloat() / activeStack.maxDamage) * 54).toInt()
        if (manaFractionTemp != manaFraction){
            manaFractionDirty = manaFraction
            manaFraction = manaFractionTemp
            if (bl) manaChangeAnimator.reset()
        }
        val stats = ScepterHelper.getScepterStats(stack)
        //xpToNext - prevXpToNext = total bar
        //-prevXpToNext / total bar = fract
        val furyPrevToNext = ScepterHelper.xpToNextLevel(stats[3],stats[0]-1)
        val furyFractionTemp = (((-1 * furyPrevToNext).toFloat() / (ScepterHelper.xpToNextLevel(stats[3],stats[0]) - furyPrevToNext))* 56).toInt()
        if (furyFractionTemp != furyFraction){
            furyFractionDirty = furyFraction
            furyFraction = furyFractionTemp
            if (bl) furyChangeAnimator.reset()
            if (stats[0] != furyLevel) {
                furyLevelDirty = true
                furyLevel = stats[0]
            } else {
                furyLevelDirty = false
            }
        }

        val gracePrevToNext = ScepterHelper.xpToNextLevel(stats[4],stats[1]-1)
        val graceFractionTemp = (((-1 * gracePrevToNext).toFloat() / (ScepterHelper.xpToNextLevel(stats[4],stats[1]) - gracePrevToNext))* 56).toInt()
        if (gracePrevToNext != graceFraction){
            graceFractionDirty = graceFraction
            graceFraction = graceFractionTemp
            if (bl) graceChangeAnimator.reset()
            if (stats[1] != graceLevel) {
                graceLevelDirty = true
                graceLevel = stats[1]
            } else {
                graceLevelDirty = false
            }
        }

        val witPrevToNext = ScepterHelper.xpToNextLevel(stats[5],stats[2]-1)
        val witFractionTemp = (((-1 * witPrevToNext).toFloat() / (ScepterHelper.xpToNextLevel(stats[5],stats[2]) - witPrevToNext))* 56).toInt()
        if (witPrevToNext != witFraction){
            witFractionDirty = witFraction
            witFraction = witFractionTemp
            if (bl) witChangeAnimator.reset()
            if (stats[2] != witLevel) {
                witLevelDirty = true
                witLevel = stats[2]
            } else {
                witLevelDirty = false
            }
        }
    }

    private fun updateSpellRadialHudMap(stack: ItemStack, activeSpellIdentifier: Identifier, activeAugmentString: String){
        val enchants = stack.enchantments.mapNotNull { Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(it as NbtCompound)) as? ScepterAugment }
        SpellRadialHud.update(enchants, activeSpellIdentifier, activeAugmentString)
    }

    private fun tick(){
        tick++
        manaChangeAnimator.tick()
        cooldownReadyAnimator.tick()
        furyChangeAnimator.tick()
        graceChangeAnimator.tick()
        witChangeAnimator.tick()
        if (MinecraftClient.getInstance().player?.itemCooldownManager?.isCoolingDown(activeStack.item) == true){
            val f = MinecraftClient.getInstance().player?.itemCooldownManager?.getCooldownProgress(activeStack.item,1f) ?: 0f
            coolingDownPips = MathHelper.ceil(f * 9f)
            cooldownReadyAnimator.kill()
            cooldownInProgress = true
        } else if (cooldownInProgress) {
            coolingDownPips = 0
            cooldownInProgress = false
            cooldownReadyAnimator.reset()
        }
    }

    private fun draw(drawContext: DrawContext, tickDelta: Float){
        val x = AiConfig.hud.getX(drawContext.scaledWindowWidth)
        val y = AiConfig.hud.getY(drawContext.scaledWindowHeight)
        //the hud base
        drawContext.drawTexture(hudTexture,x,y,84,40,89,31)
        //spell name
        drawContext.drawText(MinecraftClient.getInstance().textRenderer, (activeSpell?.getName(1) ?: fallbackSpellName).copyContentOnly().formatted(activeSpellStyle.color),x + 29,y + 2,0xFFFFFF,true)
        //mana bar
        if (manaFraction > 0)
            drawContext.drawTexture(hudTexture,x + 26,y + 19, 55,0, manaFraction,4)
        //mana change indicator
        if (manaChangeAnimator.isReady()) {
            val mDUV = manaChangeAnimator.provide()
            val minX = min(manaFraction, manaFractionDirty)
            drawContext.drawTexture(hudTexture,x + 26 + minX,y + 19, mDUV.x + minX ,mDUV.y, abs(manaFraction - manaFractionDirty),4)
        }
        //fury bar
        if (furyFraction > 0)
            drawContext.drawTexture(hudTexture,x + 23,y + 25, 55,20, furyFraction,1)
        //fury bar change
        if (furyChangeAnimator.isReady()){
            val fDUV = furyChangeAnimator.provide()
            var length = furyFraction - furyFractionDirty
            val minX = if (furyLevelDirty) {
                length = furyLevel
                0
            }else {
                furyFractionDirty
            }
            drawContext.drawTexture(hudTexture,x + 23 + minX,y + 25, fDUV.x + minX ,fDUV.y, length,1)
        }
        //grace bar
        if (graceFraction > 0)
            drawContext.drawTexture(hudTexture,x + 21,y + 27, 55,21, graceFraction,1)
        //grace bar change
        if (graceChangeAnimator.isReady()){
            val gDUV = graceChangeAnimator.provide()
            var length = graceFraction - graceFractionDirty
            val minX = if (graceLevelDirty) {
                length = graceLevel
                0
            }else {
                graceFractionDirty
            }
            drawContext.drawTexture(hudTexture,x + 21 + minX,y + 27, gDUV.x + minX ,gDUV.y, length,1)
        }
        //wit bar
        if (witFraction > 0)
            drawContext.drawTexture(hudTexture,x + 19,y + 29, 55,22, witFraction,1)
        //wit bar change
        if (witChangeAnimator.isReady()){
            val wDUV = witChangeAnimator.provide()
            var length = witFraction - witFractionDirty
            val minX = if (witLevelDirty) {
                length = witLevel
                0
            }else {
                witFractionDirty
            }
            drawContext.drawTexture(hudTexture,x + 19 + minX,y + 29, wDUV.x + minX ,wDUV.y, length,1)
        }
        //cooldown bar
        if (coolingDownPips > 0) {
            val v = (9 - coolingDownPips) * 24
            drawContext.drawTexture(hudTexture, x + 31, y + 13, 0, v, 55, 4)
        }
        //cooldown over indicator
        else if (cooldownReadyAnimator.isReady()){
            val cDUV = cooldownReadyAnimator.provide()
            drawContext.drawTexture(hudTexture,x + 31,y + 13, cDUV.x,cDUV.y, 55,4)
        }
        //the hud style
        drawContext.drawTexture(hudTexture,x-7,y, activeSpellStyle.x, activeSpellStyle.y,38,38)
        //spell icon
        //drawContext.drawTexture(activeSpellTextureId,x,y,32,32,32f,32f,32,32,32,32)
        drawIcon(drawContext, activeSpellTextureId,x,y)
    }

    private open class ElementAnimator(val elementMap: Map<Int, Vector2i>, val length: Int){
        private var tick = length + 1
        fun tick(){
            tick++
        }
        fun provide(): Vector2i {
            if (isFinished()) return fallbackV2i
            return elementMap[tick] ?: fallbackV2i
        }
        fun isFinished(): Boolean {
            return tick > length
        }
        fun isReady(): Boolean{
            return tick <= length
        }
        fun reset(){
            tick = 0
        }
        fun kill(){
            tick = length + 1
        }
    }

    fun drawIcon(
        context: DrawContext,
        texture: Identifier,
        x1: Int,
        y1: Int
    ) {
        val x2 = x1 + 32
        val y2 = y1 + 32
        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShader { GameRenderer.getPositionColorTexProgram() }
        RenderSystem.enableBlend()
        val matrix4f: Matrix4f = context.matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE)
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0f).color(1f, 1f, 1f, 1f)
            .texture(0f, 0f).next()
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0f).color(1f, 1f, 1f, 1f)
            .texture(0f, 1f).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0f).color(1f, 1f, 1f, 1f)
            .texture(1f, 1f).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0f).color(1f, 1f, 1f, 1f)
            .texture(1f, 0f).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.disableBlend()
    }

    fun registerClient(){

        /*println("Above")
        println(MathHelper.atan2(50.0-100.0,0.0))
        println("Below")
        println(MathHelper.atan2(150.0-100.0,0.0))
        println("Left")
        println(MathHelper.atan2(0.0,50.0-100.0))
        println("Right")
        println(MathHelper.atan2(0.0,150.0-100.0))
        println("Above-Right")
        println(MathHelper.atan2(50.0-100.0,150.0-100.0))
        println("Below-Right")
        println(MathHelper.atan2(150.0-100.0,150.0-100.0))
        println("Below-Left")
        println(MathHelper.atan2(150.0-100.0,50.0-100.0))
        println("Above-Left")
        println(MathHelper.atan2(50.0-100.0,50.0-100.0))*/


        ClientTickEvents.START_CLIENT_TICK.register{_ ->
            tick()
        }

        HudRenderCallback.EVENT.register{ drawContext, tickDelta ->
            val player = MinecraftClient.getInstance().player ?: return@register
            val stack = player.mainHandStack
            val item = stack.item
            if (item !is ScepterLike) return@register
            val activeSpell = item.getActiveEnchant(stack)
            if (SpellRadialHud.isDirty()){
                if (!stack.hasEnchantments()) return@register
                refreshHudCache(stack, activeSpell, player)
            }
            if (tick % 20L == 0L && !onTick){
                if (!stack.hasEnchantments()) return@register
                onTick = true
                refreshHudCache(stack, activeSpell, player)
            } else {
                onTick = false
            }
            if (!ItemStack.areEqual(stack, activeStack)){
                if (!stack.hasEnchantments()) return@register
                refreshHudCache(stack, activeSpell, player)
            }
            if (AiConfig.hud.showHud.get())
                draw(drawContext, tickDelta)
        }
    }
}