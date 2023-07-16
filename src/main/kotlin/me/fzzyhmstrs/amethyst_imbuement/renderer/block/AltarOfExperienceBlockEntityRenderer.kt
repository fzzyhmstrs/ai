@file:Suppress("UNUSED_PARAMETER")

package me.fzzyhmstrs.amethyst_imbuement.renderer.block

import me.fzzyhmstrs.amethyst_imbuement.entity.block.AltarOfExperienceBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis


@Suppress("DEPRECATION")
@Environment(value = EnvType.CLIENT)
class AltarOfExperienceBlockEntityRenderer(ctx: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<AltarOfExperienceBlockEntity> {

    override fun render(
        altarOfExperienceBlockEntity: AltarOfExperienceBlockEntity,
        f: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        j: Int
    ) {
        matrixStack.push()
        matrixStack.scale(0.5F,0.5F,0.5F)
        matrixStack.translate(1.0, 1.9, 1.0)
        val g = altarOfExperienceBlockEntity.ticks.toFloat() + f
        matrixStack.translate(0.0, (0.1f + MathHelper.sin(g * 0.1f) * 0.01f).toDouble(), 0.0)

        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(altarOfExperienceBlockEntity.lookingRotR))
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F))

        val lightAbove = WorldRenderer.getLightmapCoordinates(altarOfExperienceBlockEntity.world, altarOfExperienceBlockEntity.pos.up())
        MinecraftClient.getInstance().itemRenderer.renderItem(
            stack,
            ModelTransformationMode.FIXED,
            lightAbove,
            j,
            matrixStack,
            vertexConsumerProvider,
            altarOfExperienceBlockEntity.world,
            altarOfExperienceBlockEntity.pos.asLong().toInt()
        )
        matrixStack.pop()

    }

    companion object {
        val stack = ItemStack(Items.EMERALD)
    }
}