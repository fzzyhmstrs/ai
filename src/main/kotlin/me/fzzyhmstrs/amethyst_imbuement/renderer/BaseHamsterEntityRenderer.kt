package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.model.BaseHamsterEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.BaseHamsterArmorFeatureRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.BaseHamsterItemFeatureRenderer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Suppress("LeakingThis")
open class BaseHamsterEntityRenderer<T:BaseHamsterEntity>(ctx: EntityRendererFactory.Context,
                                                          model: BaseHamsterEntityModel<T>, shadowRadius: Float
) : LivingEntityRenderer<T, BaseHamsterEntityModel<T>>(ctx, model, shadowRadius) {

    constructor(ctx: EntityRendererFactory.Context, modelPart: ModelPart = ctx.getPart(RegisterRenderer.HAMSTER_ENTITY_MAIN)) : this(ctx, BaseHamsterEntityModel(modelPart),0.2F)

    init{
        addFeatures(ctx)
    }

    open fun addFeatures(ctx: EntityRendererFactory.Context){
        this.addFeature(BaseHamsterArmorFeatureRenderer(this, BaseHamsterEntityModel(ctx.getPart(RegisterRenderer.HAMSTER_ENTITY_ARMOR))))
        this.addFeature(BaseHamsterItemFeatureRenderer(this))
    }

    override fun getTexture(entity: T): Identifier {
        return entity.getVariant().texture
    }

    override fun hasLabel(livingEntity: T): Boolean {
        return false
    }

    override fun scale(entity: T, matrices: MatrixStack, amount: Float) {
        val scale = entity.entityScale
        matrices.scale(scale,scale,scale)
        super.scale(entity, matrices, amount)
    }
}
