package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.OcelotEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class ImbuedFamiliarEntityRenderer (context: EntityRendererFactory.Context) :
    MobEntityRenderer<ImbuedFamiliarEntity, OcelotEntityModel<ImbuedFamiliarEntity>>(
        context,
        OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT)),
        0.4f
    ) {
    override fun getTexture(familiarEntity: ImbuedFamiliarEntity): Identifier {
        return familiarEntity.getTexture()
    }

    override fun scale(familiarEntity: ImbuedFamiliarEntity, matrixStack: MatrixStack, f: Float) {
        super.scale(familiarEntity, matrixStack, f)
        matrixStack.scale(1.5f, 1.5f, 1.5f)
    }

    init {
        //addFeature(CatCollarFeatureRenderer(this, context.modelLoader))
    }
}