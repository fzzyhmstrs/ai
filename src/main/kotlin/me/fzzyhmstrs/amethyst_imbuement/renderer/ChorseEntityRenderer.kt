package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.ChorseEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.util.Identifier

class ChorseEntityRenderer(context: EntityRendererFactory.Context): MobEntityRenderer<ChorseEntity, ChorseEntityModel>(context,ChorseEntityModel(context.getPart(RegisterRenderer.CHORSE_ENTITY)),0.75f) {
    private val TEXTURE = AI.identity("textures/entity/seahorse/chorse.png")

    override fun getTexture(entity: ChorseEntity): Identifier {
        return TEXTURE
    }
}