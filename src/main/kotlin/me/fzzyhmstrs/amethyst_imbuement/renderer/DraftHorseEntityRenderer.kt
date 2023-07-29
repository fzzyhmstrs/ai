package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.DraftHorseEntity
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.DraftHorseArmorFeatureRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.HorseEntityModel
import net.minecraft.util.Identifier

class DraftHorseEntityRenderer(context: EntityRendererFactory.Context): MobEntityRenderer<DraftHorseEntity, HorseEntityModel<DraftHorseEntity>>(context,
    HorseEntityModel(context.getPart(EntityModelLayers.HORSE)),0.75f) {
    private val TEXTURE = AI.identity("textures/entity/seahorse/draft_horse.png")

    init{
        this.addFeature(DraftHorseArmorFeatureRenderer(this,HorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR))))
    }

    override fun getTexture(entity: DraftHorseEntity): Identifier {
        return TEXTURE
    }

}