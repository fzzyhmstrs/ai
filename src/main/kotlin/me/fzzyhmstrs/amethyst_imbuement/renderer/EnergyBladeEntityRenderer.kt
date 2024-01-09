package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.EnergyBladeEntity
import me.fzzyhmstrs.amethyst_imbuement.model.EnergyBladeEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

@Suppress("PrivatePropertyName")
@Environment(value = EnvType.CLIENT)
class EnergyBladeEntityRenderer(context: EntityRendererFactory.Context): EntityRenderer<EnergyBladeEntity>(context) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/energy_blade.png")
    private val model: EnergyBladeEntityModel = EnergyBladeEntityModel(context.getPart(RegisterRenderer.ENERGY_BLADE_ENTITY))

    override fun render(
        energyBladeEntity: EnergyBladeEntity,
            f: Float,
            g: Float,
            matrixStack: MatrixStack,
            vertexConsumerProvider: VertexConsumerProvider,
            i: Int
        ) {

            matrixStack.push()
            //matrixStack.translate(0f,-1.5f,0f)
            matrixStack.multiply(
                FzzyRotation.POSITIVE_Y.degrees(
                    180f - MathHelper.lerp(
                        g,
                        energyBladeEntity.prevYaw,
                        energyBladeEntity.yaw
                    )
                )
            )
            matrixStack.multiply(
                FzzyRotation.NEGATIVE_X.degrees(
                    MathHelper.lerp(
                        g,
                        energyBladeEntity.prevPitch,
                        energyBladeEntity.pitch
                    )
                )
            )
        matrixStack.translate(0f,-1.25f,0f)

            //model.setAngles(energyBladeEntity, 0.0f, 0.0f, 0.0f, energyBladeEntity.yaw, energyBladeEntity.pitch)
            val vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(TEXTURE))
            model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
        super.render(energyBladeEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(entity: EnergyBladeEntity?): Identifier {
        return TEXTURE
    }
}
