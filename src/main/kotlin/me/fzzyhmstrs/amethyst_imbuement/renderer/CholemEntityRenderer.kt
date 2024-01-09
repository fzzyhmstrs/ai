package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.living.CholemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CholemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import kotlin.math.abs

@Environment(value = EnvType.CLIENT)
class CholemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<CholemEntity, CholemEntityModel>(context,
        CholemEntityModel(context.getPart(RegisterRenderer.CHOLEM_ENTITY)),
        0.4f
    ) {

    override fun getTexture(entity: CholemEntity): Identifier {
        return entity.texture()
    }

    override fun setupTransforms(
        cholemEntity: CholemEntity,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(cholemEntity, matrixStack, f, g, h)
        if (cholemEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = cholemEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(FzzyRotation.POSITIVE_Z.degrees(6.5f * k))
    }
}
