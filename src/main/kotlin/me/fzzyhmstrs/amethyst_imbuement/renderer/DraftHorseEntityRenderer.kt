package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.horse.DraftHorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.DraftHorseEntityModel
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.DraftHorseArmorFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.util.Identifier

class DraftHorseEntityRenderer(context: EntityRendererFactory.Context): MobEntityRenderer<DraftHorseEntity, DraftHorseEntityModel>(context,
    DraftHorseEntityModel(context.getPart(EntityModelLayers.HORSE)),0.75f) {
    private val TEXTURE = Identifier("textures/entity/horse/horse_creamy.png")

    init{
        this.addFeature(DraftHorseArmorFeatureRenderer(this, DraftHorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR))))
    }

    override fun getTexture(entity: DraftHorseEntity): Identifier {
        return TEXTURE
    }

}