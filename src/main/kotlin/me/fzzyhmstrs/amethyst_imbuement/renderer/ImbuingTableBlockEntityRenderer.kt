package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuingTableBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.BookModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

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
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-k))
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0f))
        val l = MathHelper.lerp(f, imbuingTableBlockEntity.pageAngle, imbuingTableBlockEntity.nextPageAngle)
        val m = MathHelper.fractionalPart(l + 0.25f) * 1.6f - 0.3f
        val n = MathHelper.fractionalPart(l + 0.75f) * 1.6f - 0.3f
        val o = MathHelper.lerp(
            f,
            imbuingTableBlockEntity.pageTurningSpeed,
            imbuingTableBlockEntity.nextPageTurningSpeed
        )
        book.setPageAngles(g, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o)
        val vertexConsumer = BOOK_TEXTURE.getVertexConsumer(
            vertexConsumerProvider
        ) {texture: Identifier -> RenderLayer.getEntitySolid(texture)}
        book.renderBook(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
    }

    companion object {
        val IMBUING_TABLE_BOOK_SPRITE_ID = Identifier(AI.MOD_ID,"entity/imbuing_table_book")
        val BOOK_TEXTURE = SpriteIdentifier(
            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            IMBUING_TABLE_BOOK_SPRITE_ID
        )
    }

    init {
        book = BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK))
    }
}