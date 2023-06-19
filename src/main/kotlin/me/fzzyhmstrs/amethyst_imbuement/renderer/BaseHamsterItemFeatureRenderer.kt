@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.model.BaseHamsterEntityModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.math.Vec3f

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
        matrixStack.translate(0.0,1.27,0.0)
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-135f))
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90F))
        matrixStack.scale(0.5f,0.5f,0.5f)
        MinecraftClient.getInstance().itemRenderer.renderItem(item,ModelTransformation.Mode.FIXED,i,OverlayTexture.DEFAULT_UV,matrixStack,vertexConsumerProvider,hamster.id)
        matrixStack.pop()
    }
}
