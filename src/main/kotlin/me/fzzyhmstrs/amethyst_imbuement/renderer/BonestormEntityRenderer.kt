package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BonestormEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.model.UnhallowedEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.BipedEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.model.BlazeEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.util.Identifier

@Suppress("PrivatePropertyName")
class BonestormEntityRenderer(ctx: EntityRendererFactory.Context): MobEntityRenderer<BonestormEntity, BlazeEntityModel<BonestormEntity>>(ctx, BlazeEntityModel(ctx.getPart(RegisterRenderer.BONESTORM_ENTITY)), 0.5f) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/bonestorm.png")


    override fun getTexture(bonestormEntity: BonestormEntity): Identifier {
        return TEXTURE
    }

}