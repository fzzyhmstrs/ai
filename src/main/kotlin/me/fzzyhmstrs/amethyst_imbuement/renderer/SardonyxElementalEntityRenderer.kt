package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import me.fzzyhmstrs.amethyst_imbuement.model.SardonyxElementalEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.SardonyxElementalArmorFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import kotlin.math.abs

class SardonyxElementalEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<SardonyxElementalEntity, SardonyxElementalEntityModel>(context,
        SardonyxElementalEntityModel(context.getPart(RegisterRenderer.SARDONYX_ELEMENTAL)),
        0.4f
    ) {

    private val TEXTURE_NORMAL = Identifier(AI.MOD_ID,"textures/entity/sardonyx/sardonyx_elemental.png")
    private val TEXTURE_CHARGING = Identifier(AI.MOD_ID,"textures/entity/sardonyx/sardonyx_elemental_eyes.png")

    init{
        this.addFeature(SardonyxElementalArmorFeatureRenderer(this,context.modelLoader))
    }

    override fun getTexture(entity: SardonyxElementalEntity): Identifier {
        return if (entity.shouldRenderOverlay()) TEXTURE_CHARGING else TEXTURE_NORMAL
    }

    override fun setupTransforms(
            sardonyxElementalEntity: SardonyxElementalEntity,
            matrixStack: MatrixStack,
            f: Float,
            g: Float,
            h: Float
        ) {
            super.setupTransforms(sardonyxElementalEntity, matrixStack, f, g, h)
            if (sardonyxElementalEntity.limbAnimator.speed < 0.01) {
                return
            }
            val j = sardonyxElementalEntity.limbAnimator.getPos(h) + 6.0f
            val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * k))
    }
}