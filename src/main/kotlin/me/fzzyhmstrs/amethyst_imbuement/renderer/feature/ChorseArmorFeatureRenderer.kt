@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import me.fzzyhmstrs.amethyst_imbuement.entity.horse.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.ChorseEntityModel
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.DyeableHorseArmorItem
import net.minecraft.item.HorseArmorItem
import net.minecraft.util.Identifier

class ChorseArmorFeatureRenderer(context: FeatureRendererContext<ChorseEntity, ChorseEntityModel>, private val model: ChorseEntityModel)
: 
FeatureRenderer<ChorseEntity, ChorseEntityModel>(context)
{

    private val ENTITY_TEXTURE_PREFIX_LENGTH = "textures/entity/horse/".length

    private val ENTITY_TEXTURE_CACHE: MutableMap<HorseArmorItem,Identifier> = mutableMapOf()

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        chorse: ChorseEntity,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        val p: Float
        val o: Float
        val n: Float
        val itemStack = chorse.getArmorType()
        if (itemStack?.item !is HorseArmorItem) {
            return
        }
        val horseArmorItem = itemStack.item as HorseArmorItem
        this.contextModel.copyStateTo(model)
        model.animateModel(chorse, f, g, h)
        model.setAngles(chorse, f, g, j, k, l)
        if (horseArmorItem is DyeableHorseArmorItem) {
            val m = horseArmorItem.getColor(itemStack)
            n = (m shr 16 and 0xFF).toFloat() / 255.0f
            o = (m shr 8 and 0xFF).toFloat() / 255.0f
            p = (m and 0xFF).toFloat() / 255.0f
        } else {
            n = 1.0f
            o = 1.0f
            p = 1.0f
        }
        val id = ENTITY_TEXTURE_CACHE.computeIfAbsent(horseArmorItem) { Identifier("textures/entity/chorse/" + it.entityTexture.path.substring(ENTITY_TEXTURE_PREFIX_LENGTH)) }

        val vertexConsumer =
            vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(id))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0f)
    }
}
