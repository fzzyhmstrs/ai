@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack

class CrystallineGolemCrackFeatureRenderer<T: CrystallineGolemEntity>(context: FeatureRendererContext<T, CrystallineGolemEntityModel<T>>) : FeatureRenderer<T, CrystallineGolemEntityModel<T>>(
    context
) {



    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        crystalGolemEntity: T,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        if (crystalGolemEntity.isInvisible) {
            return
        }
        val identifier = crystalGolemEntity.getCrackTexture() ?: return
        renderModel(
            this.contextModel,
            identifier,
            matrixStack,
            vertexConsumerProvider,
            i,
            crystalGolemEntity,
            1.0f,
            1.0f,
            1.0f
        )
    }
}