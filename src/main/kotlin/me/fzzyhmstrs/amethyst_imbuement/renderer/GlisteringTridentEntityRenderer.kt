package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.GlisteringTridentEntity
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Suppress("PrivatePropertyName")
@Environment(value = EnvType.CLIENT)
class GlisteringTridentEntityRenderer(context: EntityRendererFactory.Context) : EntityRenderer<GlisteringTridentEntity>(context) {
    private var TEXTURE = Identifier(AI.MOD_ID,"textures/entity/glistering_trident.png")
    var model = GlisteringTridentEntityModel(context.getPart(RegisterRenderer.GLISTERING_TRIDENT))

    override fun render(
        glisteringTridentEntity: GlisteringTridentEntity,
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
                    glisteringTridentEntity.prevYaw,
                    glisteringTridentEntity.yaw
                ) - 90.0f
            )
        )
        matrixStack.multiply(
            FzzyRotation.POSITIVE_Z.degrees(
                MathHelper.lerp(
                    g,
                    glisteringTridentEntity.prevPitch,
                    glisteringTridentEntity.pitch
                ) + 90.0f
            )
        )
        val vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
            vertexConsumerProvider,
            model.getLayer(getTexture(glisteringTridentEntity)),
            false,
            glisteringTridentEntity.isEnchanted
        )
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
        super.render(glisteringTridentEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(entity: GlisteringTridentEntity): Identifier {
        return TEXTURE
    }
}
