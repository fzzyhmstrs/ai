package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.FleshGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.FleshGolemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.CrystallineGolemCrackFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import kotlin.math.abs

class FleshGolemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<FleshGolemEntity, FleshGolemEntityModel>(context,
        FleshGolemEntityModel(context.getPart(RegisterRenderer.FLESH_GOLEM_ENTITY)),
        0.7f
    ) {

    private val TEXTURE = AI.identity("textures/entity/crystal_golem/flesh_golem.png")

    init{
        addFeature(CrystallineGolemCrackFeatureRenderer(this))
    }

    override fun getTexture(entity: FleshGolemEntity): Identifier {
        return TEXTURE
    }

    override fun setupTransforms(
        fleshGolemEntity: FleshGolemEntity,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(fleshGolemEntity, matrixStack, f, g, h)
        if (fleshGolemEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = fleshGolemEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k))
    }
}