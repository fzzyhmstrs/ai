package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.AbstractEffectTotemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.TotemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

class TotemEntityRenderer(ctx: EntityRendererFactory.Context,
                          model: TotemEntityModel, shadowRadius: Float
) : LivingEntityRenderer<AbstractEffectTotemEntity, TotemEntityModel>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context) : this(ctx, TotemEntityModel(ctx.getPart(RegisterRenderer.TOTEM_ENTITY)),0.2F)

    override fun render(
        livingEntity: AbstractEffectTotemEntity,
        f: Float,
        g: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i)
        matrixStack.push()
        matrixStack.scale(0.5F,0.5F,0.5F)
        matrixStack.translate(0.0, 2.25, 0.0)
        val t = livingEntity.ticks.toFloat() + f
        matrixStack.translate(0.0, (0.1f + MathHelper.sin(t * 0.1f) * 0.01f).toDouble(), 0.0)
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(livingEntity.lookingRotR))
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F))
        MinecraftClient.getInstance().itemRenderer.renderItem(
            livingEntity.stack,
            ModelTransformationMode.FIXED,
            i,
            OverlayTexture.DEFAULT_UV,
            matrixStack,
            vertexConsumerProvider,
            livingEntity.world,
            livingEntity.seed
        )
        matrixStack.pop()
    }

    override fun getTexture(entity: AbstractEffectTotemEntity): Identifier {
        return AI.identity( "textures/entity/totem.png")
    }

    override fun hasLabel(livingEntity: AbstractEffectTotemEntity): Boolean {
        return false
    }

    override fun getRenderLayer(
        entity: AbstractEffectTotemEntity,
        showBody: Boolean,
        translucent: Boolean,
        showOutline: Boolean
    ): RenderLayer? {
        if (!showBody){
            return null
        }
        return RenderLayer.getEntityCutout(getTexture(entity))
    }
}