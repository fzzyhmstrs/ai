package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CholemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CholemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import kotlin.math.abs

@Suppress("PrivatePropertyName")
class CholemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<CholemEntity, CholemEntityModel>(context,
        CholemEntityModel(context.getPart(RegisterRenderer.CHORSE_ENTITY)),
        0.7f
    ) {

    private val TEXTURE = AI.identity("textures/entity/crystal_golem/cholem.png")

    override fun getTexture(entity: CholemEntity): Identifier {
        return TEXTURE
    }

    override fun setupTransforms(
        cholemEntity: CholemEntity,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(cholemEntity, matrixStack, f, g, h)
        if (cholemEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = cholemEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k))
    }
}