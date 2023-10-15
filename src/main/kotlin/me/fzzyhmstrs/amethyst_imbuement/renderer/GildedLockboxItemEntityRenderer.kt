package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.entity.block.GildedLockboxBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

object GildedLockboxItemEntityRenderer: BuiltinItemRendererRegistry.DynamicItemRenderer {

    private val blockEntity by lazy {
        GildedLockboxBlockEntity(BlockPos.ORIGIN, RegisterBlock.GILDED_LOCKBOX.defaultState)
    }

    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        //val renderModel = modelLoader.getModel()
        MinecraftClient.getInstance().blockEntityRenderDispatcher.renderEntity(blockEntity,matrices,vertexConsumers,light,overlay)
    }
}