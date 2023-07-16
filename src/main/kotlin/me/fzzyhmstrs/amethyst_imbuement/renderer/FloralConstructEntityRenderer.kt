package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.living.FloralConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.model.FloralConstructEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.util.Identifier
class FloralConstructEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<FloralConstructEntity, FloralConstructEntityModel>(context,
        FloralConstructEntityModel(context.getPart(RegisterRenderer.FLORAL_CONSTRUCT_ENTITY)),
        0.4f
    ) {

    override fun getTexture(entity: FloralConstructEntity): Identifier {
        return entity.texture
    }
}