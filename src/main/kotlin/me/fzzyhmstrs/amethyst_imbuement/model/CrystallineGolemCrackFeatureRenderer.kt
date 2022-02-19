@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.model

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class CrystallineGolemCrackFeatureRenderer(context: FeatureRendererContext<CrystallineGolemEntity, CrystallineGolemEntityModel>) : FeatureRenderer<CrystallineGolemEntity, CrystallineGolemEntityModel>(
    context
) {

    private val DAMAGE_TO_TEXTURE: Map<CrystallineGolemEntity.Crack, Identifier> = ImmutableMap.of(
        CrystallineGolemEntity.Crack.LOW,
        Identifier("amethyst_imbuement","textures/entity/crystal_golem/crystal_golem_crackiness_low.png"),
        CrystallineGolemEntity.Crack.MEDIUM,
        Identifier("amethyst_imbuement","textures/entity/crystal_golem/crystal_golem_crackiness_medium.png"),
        CrystallineGolemEntity.Crack.HIGH,
        Identifier("amethyst_imbuement","textures/entity/crystal_golem/crystal_golem_crackiness_high.png")
    )

    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        crystalGolemEntity: CrystallineGolemEntity,
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