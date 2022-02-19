package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.AltarOfExperienceBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.BookModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.command.argument.EntityArgumentType.entity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3f
import org.apache.commons.lang3.StringUtils.overlay


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

        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(altarOfExperienceBlockEntity.lookingRotR));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90F));

        val lightAbove = WorldRenderer.getLightmapCoordinates(altarOfExperienceBlockEntity.world, altarOfExperienceBlockEntity.pos.up())
        MinecraftClient.getInstance().itemRenderer.renderItem(
            stack,
            ModelTransformation.Mode.FIXED,
            lightAbove,
            j,
            matrixStack,
            vertexConsumerProvider,
            altarOfExperienceBlockEntity.pos.asLong().toInt()
        )
        matrixStack.pop()

    }

    companion object {
        val stack = ItemStack(Items.EMERALD)
    }
}