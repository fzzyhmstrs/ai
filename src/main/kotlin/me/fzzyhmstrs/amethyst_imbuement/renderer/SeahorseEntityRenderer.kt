package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.SeahorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.SeahorseEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.SeahorseArmorFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.util.Identifier

class SeahorseEntityRenderer(context: EntityRendererFactory.Context): MobEntityRenderer<SeahorseEntity, SeahorseEntityModel>(context,
    SeahorseEntityModel(context.getPart(RegisterRenderer.SEAHORSE_ENTITY)),0.75f) {
    private val TEXTURE = AI.identity("textures/entity/seahorse/seahorse.png")

    init{
        this.addFeature(SeahorseArmorFeatureRenderer(this, SeahorseEntityModel(context.getPart(RegisterRenderer.SEAHORSE_ENTITY_ARMOR))))
    }

    override fun getTexture(entity: SeahorseEntity): Identifier {
        return TEXTURE
    }

}