package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.model.BaseHamsterEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.util.Identifier

open class BaseHamsterEntityRenderer<T:BaseHamsterEntity>(ctx: EntityRendererFactory.Context,
                                model: BaseHamsterEntityModel<T>, shadowRadius: Float
) : LivingEntityRenderer<T, BaseHamsterEntityModel<T>>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context, modelPart: ModelPart = ctx.getPart(RegisterRenderer.HAMSTER_ENTITY_MAIN)) : this(ctx, BaseHamsterEntityModel(modelPart),0.2F)

    override fun getTexture(entity: T): Identifier {
        return entity.getVariant().texture
    }

    override fun hasLabel(livingEntity: T): Boolean {
        return false
    }
}
