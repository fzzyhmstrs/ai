package me.fzzyhmstrs.amethyst_imbuement.renderer.block

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.block.ImbuingTableBlockEntity
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.BookModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Suppress("DEPRECATION")
@Environment(value = EnvType.CLIENT)
class ImbuingTableBlockEntityRenderer(ctx: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<ImbuingTableBlockEntity> {
    private val book: BookModel
    override fun render(
        imbuingTableBlockEntity: ImbuingTableBlockEntity,
        f: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        j: Int
    ) {
        matrixStack.push()
        matrixStack.translate(0.5, 0.75, 0.5)
        val g = imbuingTableBlockEntity.ticks.toFloat() + f
        matrixStack.translate(0.0, (0.1f + MathHelper.sin(g * 0.1f) * 0.01f).toDouble(), 0.0)
        var h: Float = imbuingTableBlockEntity.field_11964 - imbuingTableBlockEntity.field_11963
        while (h >= Math.PI.toFloat()) {
            h -= Math.PI.toFloat() * 2
        }
        while (h < (-Math.PI).toFloat()) {
            h += Math.PI.toFloat() * 2
        }
        val k = imbuingTableBlockEntity.field_11963 + h * f
        matrixStack.multiply(FzzyRotation.POSITIVE_Y.rotation(-k))
        matrixStack.multiply(FzzyRotation.POSITIVE_Z.degrees(80.0f))
        val l = MathHelper.lerp(f, imbuingTableBlockEntity.pageAngle, imbuingTableBlockEntity.nextPageAngle)
        val m = MathHelper.fractionalPart(l + 0.25f) * 1.6f - 0.3f
        val n = MathHelper.fractionalPart(l + 0.75f) * 1.6f - 0.3f
        val o = MathHelper.lerp(
            f,
            imbuingTableBlockEntity.pageTurningSpeed,
            imbuingTableBlockEntity.nextPageTurningSpeed
        )
        book.setPageAngles(g, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o)
        val vertexConsumer = vertexConsumerProvider.getBuffer(book.getLayer(IMBUING_TABLE_BOOK_SPRITE_ID))
        book.renderBook(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
    }

    companion object {
        val IMBUING_TABLE_BOOK_SPRITE_ID = Identifier(AI.MOD_ID,"textures/entity/imbuing_table_book.png")
    }

    init {
        book = BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK))
    }
}