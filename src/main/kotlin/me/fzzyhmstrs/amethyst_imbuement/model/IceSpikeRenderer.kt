package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.IceSpikeEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

@Suppress("PrivatePropertyName", "SpellCheckingInspection")
class IceSpikeRenderer(context: EntityRendererFactory.Context): EntityRenderer<IceSpikeEntity>(context) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/ice_spike.png")
    private val model: EvokerFangsEntityModel<IceSpikeEntity> = EvokerFangsEntityModel(context.getPart(
        EntityModelLayers.EVOKER_FANGS
    ))

    override fun render(
        iceSpikeEntity: IceSpikeEntity,
        f: Float,
        g: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        val h = iceSpikeEntity.getAnimationProgress(g)
        if (h == 0.0f) {
            return
        }
        var j = 2.0f
        if (h > 0.9f) {
            j *= (1.0f - h) / 0.1f
        }
        matrixStack.push()
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f - iceSpikeEntity.yaw))
        matrixStack.scale(-j, -j, j)
        matrixStack.translate(0.0, -0.626, 0.0)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        model.setAngles(iceSpikeEntity, h, 0.0f, 0.0f, iceSpikeEntity.yaw, iceSpikeEntity.pitch)
        val vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(TEXTURE))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
        super.render(iceSpikeEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(entity: IceSpikeEntity?): Identifier {
        return TEXTURE
    }
}