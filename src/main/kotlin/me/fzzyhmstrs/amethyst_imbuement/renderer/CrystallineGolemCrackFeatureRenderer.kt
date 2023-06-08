@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
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
        Identifier(AI.MOD_ID,"textures/entity/crystal_golem/crystal_golem_crackiness_low.png"),
        CrystallineGolemEntity.Crack.MEDIUM,
        Identifier(AI.MOD_ID,"textures/entity/crystal_golem/crystal_golem_crackiness_medium.png"),
        CrystallineGolemEntity.Crack.HIGH,
        Identifier(AI.MOD_ID,"textures/entity/crystal_golem/crystal_golem_crackiness_high.png")
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