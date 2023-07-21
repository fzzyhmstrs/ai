package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.model.UnhallowedEntityModel
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.util.Identifier

@Suppress("PrivatePropertyName")
class UnhallowedEntityRenderer<T: PlayerCreatedConstructEntity>(ctx: EntityRendererFactory.Context, private val TEXTURE: Identifier): BipedEntityRenderer<T, UnhallowedEntityModel<T>>(ctx, UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f) {

    init{
        addFeature(ArmorFeatureRenderer(this,
            UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE_INNER_ARMOR)),
            UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE_OUTER_ARMOR)),
            ctx.modelManager)
        )
    }

    override fun getTexture(unhallowedEntity: T): Identifier {
        return TEXTURE
    }

}