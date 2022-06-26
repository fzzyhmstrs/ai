package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_core.registry.ItemModelRegistry
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel.Companion.TEXTURE
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

object GlisteringTridentItemEntityRenderer: BuiltinItemRendererRegistry.DynamicItemRenderer {

    private val modelLoader by lazy {
        ItemModelRegistry.getEntityModelLoader(RegisterItem.GLISTERING_TRIDENT)
    }

    override fun render(
        stack: ItemStack,
        mode: ModelTransformation.Mode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        val renderModel = modelLoader.getModel()
        matrices.push()
        matrices.scale(1.0f, -1.0f, -1.0f)
        val block = ItemRenderer.getDirectItemGlintConsumer(
            vertexConsumers, renderModel.getLayer(
                TEXTURE
            ), false, stack.hasGlint()
        )
        renderModel.render(matrices, block, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f)
        matrices.pop()
    }
}