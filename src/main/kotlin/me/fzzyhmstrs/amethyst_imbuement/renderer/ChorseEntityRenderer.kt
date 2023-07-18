package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.ChorseEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

class ChorseEntityRenderer(context: EntityRendererFactory.Context): MobEntityRenderer<ChorseEntity, ChorseEntityModel>(context,ChorseEntityModel(context.getPart(RegisterRenderer.CHORSE_ENTITY)),0.75f) {
    private val TEXTURE = AI.identity("textures/entity/seahorse/chorse.png")

    override fun getTexture(entity: ChorseEntity): Identifier {
        return TEXTURE
    }

    override fun getAnimationProgress(chorseEntity: ChorseEntity, f: Float): Float {
        val g = MathHelper.lerp(f, chorseEntity.prevFlapProgress, chorseEntity.flapProgress)
        val h = MathHelper.lerp(f, chorseEntity.prevMaxWingDeviation, chorseEntity.maxWingDeviation)
        return (MathHelper.sin(g) + 1.0f) * h
    }

}