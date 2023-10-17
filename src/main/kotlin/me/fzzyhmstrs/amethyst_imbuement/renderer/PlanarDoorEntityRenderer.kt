package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.PlanarDoorEntity
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Suppress("PrivatePropertyName")
class PlanarDoorEntityRenderer(context: EntityRendererFactory.Context): EntityRenderer<PlanarDoorEntity>(context) {
    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/energy_blade.png")

    override fun render(
        planarDoorEntity: PlanarDoorEntity,
            f: Float,
            g: Float,
            matrixStack: MatrixStack,
            vertexConsumerProvider: VertexConsumerProvider,
            i: Int
        ) {

    }

    override fun shouldRender(entity: PlanarDoorEntity?, frustum: Frustum?, x: Double, y: Double, z: Double): Boolean {
        return false
    }

    override fun getTexture(entity: PlanarDoorEntity?): Identifier {
        return TEXTURE
    }
}