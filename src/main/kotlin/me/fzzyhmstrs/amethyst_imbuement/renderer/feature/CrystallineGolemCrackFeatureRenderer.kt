@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer.feature

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class CrystallineGolemCrackFeatureRenderer<T: CrystallineGolemEntity>(context: FeatureRendererContext<T, CrystallineGolemEntityModel<T>>) : FeatureRenderer<T, CrystallineGolemEntityModel<T>>(
    context
) {

    private val DAMAGE_TO_TEXTURE: Map<CrystallineGolemEntity.Crack, Identifier> = ImmutableMap.of(
        CrystallineGolemEntity.Crack.LOW,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_low.png"),
        CrystallineGolemEntity.Crack.MEDIUM,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_medium.png"),
        CrystallineGolemEntity.Crack.HIGH,
        AI.identity("textures/entity/crystal_golem/crystal_golem_crackiness_high.png")
    )

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        crystalGolemEntity: T,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        if (crystalGolemEntity.isInvisible) {
            return
        }
        val crack = crystalGolemEntity.getCrack()
        if (crack == CrystallineGolemEntity.Crack.NONE) {
            return
        }
        val identifier = DAMAGE_TO_TEXTURE[crack]
        renderModel(
            this.contextModel,
            identifier,
            matrixStack,
            vertexConsumerProvider,
            i,
            crystalGolemEntity,
            1.0f,
            1.0f,
            1.0f
        )
    }
}