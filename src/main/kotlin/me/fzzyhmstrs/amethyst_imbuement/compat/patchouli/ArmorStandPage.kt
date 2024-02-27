package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.world.World
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.base.ClientTicker
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.client.book.page.PageEntity
import vazkii.patchouli.client.book.page.PageText
import vazkii.patchouli.common.util.EntityUtil
import java.util.function.Function
import kotlin.math.max

class ArmorStandPage: PageText() {

    private var helmet: Identifier? = null
    private var chestplate: Identifier? = null
    private var leggings: Identifier? = null
    private var boots: Identifier? = null

    @Transient
    var entity: Entity? = null
    @Transient
    var creator: Function<World, Entity>? = null
    @Transient
    private var errored = false
    @Transient
    private var renderScale: Float = 1f
    @Transient
    private var offset: Float = 1f
    @Transient
    private val stacks: Array<ItemStack> = arrayOf(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)
    override fun build(level: World?, entry: BookEntry?, builder: BookContentsBuilder?, pageNum: Int) {
        super.build(level, entry, builder, pageNum)
        val helmetString: String = if((FzzyPort.ITEM.get(helmet) as? ArmorItem)?.also { stacks[0] = it.defaultStack }?.slotType == EquipmentSlot.HEAD) "{Count:1,id:\"" + helmet?.toString() + "\"}," else ""
        val chestString: String = if((FzzyPort.ITEM.get(chestplate) as? ArmorItem)?.also { stacks[1] = it.defaultStack }?.slotType == EquipmentSlot.CHEST) "{Count:1,id:\"" + chestplate?.toString() + "\"}," else ""
        val legsString: String = if((FzzyPort.ITEM.get(leggings) as? ArmorItem)?.also { stacks[2] = it.defaultStack }?.slotType == EquipmentSlot.LEGS) "{Count:1,id:\"" + leggings?.toString() + "\"}," else ""
        val bootsString: String = if((FzzyPort.ITEM.get(boots) as? ArmorItem)?.also { stacks[3] = it.defaultStack }?.slotType == EquipmentSlot.FEET) "{Count:1,id:\"" + boots?.toString() + "\"}," else ""
        var nbtString = "{ArmorItems:[$bootsString$legsString$chestString$helmetString"
        if (nbtString.last() == ',')
            nbtString = nbtString.dropLast(1)
        nbtString += "]}"
        creator = EntityUtil.loadEntity("minecraft:armor_stand$nbtString")
    }

    override fun onDisplayed(parent: GuiBookEntry, left: Int, top: Int) {
        super.onDisplayed(parent, left, top)
        val world = parent.minecraft.world ?: return
        loadEntity(world)
    }

    override fun getTextHeight(): Int {
        return 115
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, pticks: Float) {
        val x = GuiBook.PAGE_WIDTH / 2 - 50
        RenderSystem.enableBlend()
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        context.drawTexture(PatchouliCompat.ARMOR_STAND_BACKGROUND, x, 7, 0f, 0f, 100, 105, 128, 128)
        //GuiBook.drawFromTexture(context, book, x, 7, 405, 149, 106, 106)
        if (errored) {
            context.drawText(
                fontRenderer,
                I18n.translate("patchouli.gui.lexicon.loading_error"),
                58,
                60,
                0xFF0000,
                true
            )
        }
        if (entity != null) {
            PageEntity.renderEntity(context, entity, 71f, 60f, ClientTicker.total, renderScale, offset)
        }
        var y: Int
        stacks.forEachIndexed { index, itemStack ->
            y = 11 + index * 27
            parent.renderItemStack(context, x + 4, y, mouseX, mouseY, itemStack)

        }
        super.render(context, mouseX, mouseY, pticks)
    }

    private fun loadEntity(world: World) {
        if (!errored && (entity == null || entity?.isAlive != true || entity?.world !== world)) {
            try {
                val entityTemp = creator?.apply(world) ?: return
                entity = entityTemp
                val width = entityTemp.width
                val height = entityTemp.height
                val entitySize = max(1f, max(width, height))
                renderScale = 100f / entitySize * 0.8f
                offset = max(height, entitySize) * 0.5f
            } catch (e: Exception) {
                errored = true
                PatchouliAPI.LOGGER.error("Failed to load entity", e)
            }
        }
    }

}