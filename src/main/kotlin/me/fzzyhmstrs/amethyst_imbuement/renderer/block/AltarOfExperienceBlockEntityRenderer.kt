@file:Suppress("UNUSED_PARAMETER")

package me.fzzyhmstrs.amethyst_imbuement.renderer.block

import me.fzzyhmstrs.amethyst_imbuement.entity.block.AltarOfExperienceBlockEntity
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
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
        var h: Float = altarOfExperienceBlockEntity.field_11964 - altarOfExperienceBlockEntity.field_11963
        while (h >= Math.PI.toFloat()) {
            h -= Math.PI.toFloat() * 2
        }
        while (h < (-Math.PI).toFloat()) {
            h += Math.PI.toFloat() * 2
        }
        val k = altarOfExperienceBlockEntity.field_11963 + h * f
        matrixStack.multiply(FzzyRotation.POSITIVE_Y.rotation(-k))
        matrixStack.multiply(FzzyRotation.POSITIVE_Y.degrees(90F))

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