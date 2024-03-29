package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.BaseShardEntity
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.ProjectileEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(value = EnvType.CLIENT)
class BaseShardEntityRenderer<T: BaseShardEntity>(context: EntityRendererFactory.Context, private val TEXTURE: Identifier): ProjectileEntityRenderer<T>(context) {

    override fun getTexture(entity: T): Identifier {
        return TEXTURE
    }

    override fun render(
        persistentProjectileEntity: T,
        f: Float,
        g: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        matrixStack.push()
        matrixStack.multiply(
            FzzyRotation.POSITIVE_Y.degrees(
                MathHelper.lerp(
                    g,
                    persistentProjectileEntity.prevYaw,
                    persistentProjectileEntity.yaw
                ) - 90.0f
            )
        )
        matrixStack.multiply(
            FzzyRotation.POSITIVE_Z.degrees(
                MathHelper.lerp(
                    g,
                    persistentProjectileEntity.prevPitch,
                    persistentProjectileEntity.pitch
                )
            )
        )
        val s = persistentProjectileEntity.shake.toFloat() - g
        if (s > 0.0f) {
            val t = -MathHelper.sin(s * 3.0f) * s
            matrixStack.multiply(FzzyRotation.POSITIVE_Z.degrees(t))
        }
        matrixStack.multiply(FzzyRotation.POSITIVE_X.degrees(45.0f))
        matrixStack.scale(0.05625f, 0.05625f, 0.05625f)
        matrixStack.translate(-4.0, 0.0, 0.0)
        val vertexConsumer =
            vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(getTexture(persistentProjectileEntity)))
        val entry = matrixStack.peek()
        val matrix4f = entry.positionMatrix
        val matrix3f = entry.normalMatrix
        vertex(matrix4f, matrix3f, vertexConsumer, -0, -2, -2, 0.0f, 0.15625f, -1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, 2, -2, 0.0f, 0.3125f, -1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, 2, -2, 0.0f, 0.15625f, 1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, i)
        vertex(matrix4f, matrix3f, vertexConsumer, -0, -2, -2, 0.0f, 0.3125f, 1, 0, 0, i)
        for (u in 0..1) {
            matrixStack.multiply(FzzyRotation.POSITIVE_X.degrees(90.0f))
            vertex(matrix4f, matrix3f, vertexConsumer, -9, -2, 0, 0.0f, 0.0f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, -2, 0, 0.75f, 0.0f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, 2, 0, 0.75f, 0.15625f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, -9, 2, 0, 0.0f, 0.15625f, 0, 1, 0, i)
        }
        for (u in 0..1) {
            matrixStack.multiply(FzzyRotation.POSITIVE_X.degrees(90.0f))
            vertex(matrix4f, matrix3f, vertexConsumer, -9, -2, 0, 0.0f, 0.3125f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, -2, 0, 0.75f, 0.3125f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, 2, 0, 0.75f, 0.46875f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, -9, 2, 0, 0.0f, 0.46875f, 0, 1, 0, i)
        }
        matrixStack.pop()
        super.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }
}
