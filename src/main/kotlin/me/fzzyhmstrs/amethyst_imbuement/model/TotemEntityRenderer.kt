package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.AbstractEffectTotemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.util.Identifier

class TotemEntityRenderer(ctx: EntityRendererFactory.Context,
                          model: TotemEntityModel, shadowRadius: Float
) : LivingEntityRenderer<AbstractEffectTotemEntity, TotemEntityModel>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context) : this(ctx, TotemEntityModel(ctx.getPart(RegisterRenderer.TOTEM_ENTITY)),0.2F)

    override fun getTexture(entity: AbstractEffectTotemEntity): Identifier {
        return Identifier(AI.MOD_ID, "textures/entity/totem.png")
    }

    override fun hasLabel(livingEntity: AbstractEffectTotemEntity): Boolean {
        return false
    }

    override fun getRenderLayer(
        entity: AbstractEffectTotemEntity,
        showBody: Boolean,
        translucent: Boolean,
        showOutline: Boolean
    ): RenderLayer? {
        if (!showBody){
            return null
        }
        return RenderLayer.getEntityCutout(getTexture(entity))
    }
}