package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.client.render.entity.model.OcelotEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class ImbuedFamiliarArmorFeatureRenderer(
    context: FeatureRendererContext<ImbuedFamiliarEntity, OcelotEntityModel<ImbuedFamiliarEntity>>,
    loader: EntityModelLoader
) :
    FeatureRenderer<ImbuedFamiliarEntity, OcelotEntityModel<ImbuedFamiliarEntity>>(context) {
    private val model: OcelotEntityModel<ImbuedFamiliarEntity>
    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        familiarEntity: ImbuedFamiliarEntity,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {

    }

    companion object {
        private val SKIN = Identifier("textures/entity/cat/cat_collar.png")
    }

    init {
        model = OcelotEntityModel(loader.getModelPart(RegisterRenderer.CAT_ARMOR))
    }
}
