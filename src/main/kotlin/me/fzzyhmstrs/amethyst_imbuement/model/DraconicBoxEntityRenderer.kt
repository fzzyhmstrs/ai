package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.util.Identifier

class DraconicBoxEntityRenderer(ctx: EntityRendererFactory.Context,
                                model: DraconicBoxModel, shadowRadius: Float
) : LivingEntityRenderer<DraconicBoxEntity, DraconicBoxModel>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context) : this(ctx, DraconicBoxModel(ctx.getPart(RegisterRenderer.DRACONIC_BOX_ENTITY)),0.2F)

    override fun getTexture(entity: DraconicBoxEntity): Identifier {
        return Identifier(AI.MOD_ID, "textures/entity/draconic_box.png")
    }

    override fun hasLabel(livingEntity: DraconicBoxEntity): Boolean {
        return false
    }

    override fun getRenderLayer(
        entity: DraconicBoxEntity,
        showBody: Boolean,
        translucent: Boolean,
        showOutline: Boolean
    ): RenderLayer? {
        if (!showBody){
            return null
        }
        return RenderLayer.getEntityTranslucent(getTexture(entity), true)
    }
}