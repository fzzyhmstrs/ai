package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.IceShardEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.ProjectileEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

class IceShardEntityRenderer(context: EntityRendererFactory.Context): ProjectileEntityRenderer<IceShardEntity>(context) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/ice_shard.png")

    override fun getTexture(entity: IceShardEntity): Identifier {
        return TEXTURE
    }

    override fun render(
        persistentProjectileEntity: IceShardEntity,
        f: Float,
        g: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        matrixStack.push()
        matrixStack.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(
                MathHelper.lerp(
                    g,
                    (persistentProjectileEntity as PersistentProjectileEntity).prevYaw,
                    (persistentProjectileEntity as Entity).yaw
                ) - 90.0f
            )
        )
        matrixStack.multiply(
            RotationAxis.POSITIVE_Z.rotationDegrees(
                MathHelper.lerp(
                    g,
                    (persistentProjectileEntity as PersistentProjectileEntity).prevPitch,
                    (persistentProjectileEntity as Entity).pitch
                )
            )
        )
        val s = (persistentProjectileEntity as PersistentProjectileEntity).shake.toFloat() - g
        if (s > 0.0f) {
            val t = -MathHelper.sin(s * 3.0f) * s
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(t))
        }
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f))
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
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f))
            vertex(matrix4f, matrix3f, vertexConsumer, -9, -2, 0, 0.0f, 0.0f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, -2, 0, 0.75f, 0.0f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, 2, 0, 0.75f, 0.15625f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, -9, 2, 0, 0.0f, 0.15625f, 0, 1, 0, i)
        }
        for (u in 0..1) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f))
            vertex(matrix4f, matrix3f, vertexConsumer, -9, -2, 0, 0.0f, 0.3125f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, -2, 0, 0.75f, 0.3125f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, 9, 2, 0, 0.75f, 0.46875f, 0, 1, 0, i)
            vertex(matrix4f, matrix3f, vertexConsumer, -9, 2, 0, 0.0f, 0.46875f, 0, 1, 0, i)
        }
        matrixStack.pop()
        super.render(persistentProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }
}
