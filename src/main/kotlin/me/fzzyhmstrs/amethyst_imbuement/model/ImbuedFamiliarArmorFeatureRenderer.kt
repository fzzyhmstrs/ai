package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.client.render.entity.model.OcelotEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.DyeableHorseArmorItem
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack

class ImbuedFamiliarArmorFeatureRenderer(
    context: FeatureRendererContext<ImbuedFamiliarEntity, OcelotEntityModel<ImbuedFamiliarEntity>>,
    loader: EntityModelLoader
) :
    FeatureRenderer<ImbuedFamiliarEntity, OcelotEntityModel<ImbuedFamiliarEntity>>(context) {
    private val model: OcelotEntityModel<ImbuedFamiliarEntity> = OcelotEntityModel(loader.getModelPart(RegisterRenderer.CAT_ARMOR))
    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        familiarEntity: ImbuedFamiliarEntity,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        val itemStack: ItemStack = familiarEntity.getArmorType()
        val item = itemStack.item
        if (item !is HorseArmorItem) {
            return
        }
        contextModel.copyStateTo(this.model)
        model.animateModel(familiarEntity, f, g, h)
        model.setAngles(familiarEntity, f, g, j, k, l)
        val p: Float
        val o: Float
        val n: Float
        if (item is DyeableHorseArmorItem) {
            val m = item.getColor(itemStack)
            n = (m shr 16 and 0xFF).toFloat() / 255.0f
            o = (m shr 8 and 0xFF).toFloat() / 255.0f
            p = (m and 0xFF).toFloat() / 255.0f
        } else {
            n = 1.0f
            o = 1.0f
            p = 1.0f
        }
        val vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(familiarEntity.getCatArmorTex(itemStack)))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0f)
    }
}
