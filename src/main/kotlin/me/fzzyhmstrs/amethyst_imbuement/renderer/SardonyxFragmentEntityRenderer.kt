package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.SardonyxFragmentEntity
import me.fzzyhmstrs.amethyst_imbuement.model.SardonyxFragmentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import kotlin.math.abs

class SardonyxFragmentEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<SardonyxFragmentEntity, SardonyxFragmentEntityModel>(context,
        SardonyxFragmentEntityModel(context.getPart(RegisterRenderer.SARDONYX_FRAGMENT)),
        0.4f
    ) {

    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/sardonyx/sardonyx_fragment.png")

    override fun getTexture(entity: SardonyxFragmentEntity): Identifier {
        return TEXTURE
    }

    override fun setupTransforms(
        sardonyxFragmentEntity: SardonyxFragmentEntity,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(sardonyxFragmentEntity, matrixStack, f, g, h)
        if (sardonyxFragmentEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = sardonyxFragmentEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k))
    }
}