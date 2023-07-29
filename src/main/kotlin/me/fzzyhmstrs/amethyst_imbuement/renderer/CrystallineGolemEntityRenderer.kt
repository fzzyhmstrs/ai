package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.CrystallineGolemCrackFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import kotlin.math.abs

class CrystallineGolemEntityRenderer<T: CrystallineGolemEntity>(context: EntityRendererFactory.Context, private val texture: Identifier) :
    MobEntityRenderer<T, CrystallineGolemEntityModel<T>>(context,
        CrystallineGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)),
        0.7f
    ) {

    init {
        addFeature(CrystallineGolemCrackFeatureRenderer(this))
    }

    override fun getTexture(entity: T): Identifier {
        return texture
    }

    override fun setupTransforms(
        crystalGolemEntity: T,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(crystalGolemEntity, matrixStack, f, g, h)
        if (crystalGolemEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = crystalGolemEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k))
    }
}