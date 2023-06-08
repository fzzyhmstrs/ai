package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.model.BaseHamsterEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.util.Identifier

class BaseHamsterEntityRenderer(ctx: EntityRendererFactory.Context,
                                model: BaseHamsterEntityModel, shadowRadius: Float
) : LivingEntityRenderer<out BaseHamsterEntity, out BaseHamsterEntityModel>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context) : this(ctx, BaseHamsterEntityModel(ctx.getPart(RegisterRenderer.BASE_HAMSTER_ENTITY)),0.2F)

    override fun getTexture(entity: BaseHamsterEntity): Identifier {
        return entity.getVariant().texture
    }

    override fun hasLabel(livingEntity: BaseHamsterEntity): Boolean {
        return false
    }
}
