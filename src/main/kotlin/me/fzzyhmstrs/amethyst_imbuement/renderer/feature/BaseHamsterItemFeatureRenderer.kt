@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.model.BaseHamsterEntityModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.math.RotationAxis

class BaseHamsterItemFeatureRenderer<T:BaseHamsterEntity,M:BaseHamsterEntityModel<T>>(context: FeatureRendererContext<T, M>)
: 
FeatureRenderer<T, M>(context)
{

  
    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        hamster: T,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        val item = hamster.getEquippedStack(EquipmentSlot.MAINHAND)
        if (item.isEmpty){
            return
        }
        matrixStack.push()
        matrixStack.translate(0f,1.27f,0f)
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-135f))
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90F))
        matrixStack.scale(0.5f,0.5f,0.5f)
        MinecraftClient.getInstance().itemRenderer.renderItem(item, ModelTransformationMode.FIXED,i,OverlayTexture.DEFAULT_UV,matrixStack,vertexConsumerProvider,hamster.world,hamster.id)
        matrixStack.pop()
    }
}
