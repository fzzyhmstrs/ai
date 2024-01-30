@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.model.ChorseEntityModel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.DyeableHorseArmorItem
import net.minecraft.item.HorseArmorItem
import net.minecraft.util.Identifier

@Environment(value = EnvType.CLIENT)
class ChorseArmorFeatureRenderer(context: FeatureRendererContext<ChorseEntity, ChorseEntityModel>, private val model: ChorseEntityModel)
: 
FeatureRenderer<ChorseEntity, ChorseEntityModel>(context)
{

    private val ENTITY_TEXTURE_PREFIX_LENGTH = "textures/entity/horse/armor/horse_armor_".length

    private val FALLBACK_TEXTURE = AI.identity("textures/entity/chorse/armor/chorse_armor_fallback.png")

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
        val id = ENTITY_TEXTURE_CACHE.computeIfAbsent(horseArmorItem) {
            (AI.identity("textures/entity/chorse/armor/chorse_armor_" + it.entityTexture.path.substring(ENTITY_TEXTURE_PREFIX_LENGTH)))
                .takeIf { it1 -> MinecraftClient.getInstance().resourceManager.getResource(it1).isPresent } ?: FALLBACK_TEXTURE
        }


        val vertexConsumer =
            vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(id))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0f)
    }
}