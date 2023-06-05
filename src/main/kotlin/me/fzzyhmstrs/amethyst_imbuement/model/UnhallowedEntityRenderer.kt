package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.UnhallowedEntity
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.util.Identifier

@Suppress("PrivatePropertyName")
class UnhallowedEntityRenderer(ctx: EntityRendererFactory.Context): BipedEntityRenderer<UnhallowedEntity, UnhallowedEntityModel>(ctx, UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/unhallowed.png")

    init{
        addFeature(ArmorFeatureRenderer(this,
            UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE_INNER_ARMOR)),
            UnhallowedEntityModel(ctx.getPart(EntityModelLayers.ZOMBIE_OUTER_ARMOR))))
    }

    override fun getTexture(unhallowedEntity: UnhallowedEntity): Identifier {
        return TEXTURE
    }

}