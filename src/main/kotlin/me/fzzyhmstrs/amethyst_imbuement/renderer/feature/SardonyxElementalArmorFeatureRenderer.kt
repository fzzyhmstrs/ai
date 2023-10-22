package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import me.fzzyhmstrs.amethyst_imbuement.model.SardonyxElementalEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

class SardonyxElementalArmorFeatureRenderer(featureRendererContext: FeatureRendererContext<SardonyxElementalEntity, SardonyxElementalEntityModel>, loader: EntityModelLoader) :
    EnergySwirlOverlayFeatureRenderer<SardonyxElementalEntity, SardonyxElementalEntityModel>(featureRendererContext) {

    private val model =  SardonyxElementalEntityModel(loader.getModelPart(RegisterRenderer.SARDONYX_ELEMENTAL_ARMOR))
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/sardonyx/elemental_armor.png")

    override fun getEnergySwirlX(partialAge: Float): Float {
        return MathHelper.cos(partialAge * 0.02f) * 3.0f
    }

    override fun getEnergySwirlTexture(): Identifier {
        return TEXTURE
    }

    override fun getEnergySwirlModel(): EntityModel<SardonyxElementalEntity> {
        return model
    }
}