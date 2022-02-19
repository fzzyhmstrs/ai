package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3f
import kotlin.math.abs

@Suppress("PrivatePropertyName")
class CrystallineGolemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<CrystallineGolemEntity, CrystallineGolemEntityModel>(context,
        CrystallineGolemEntityModel(context.getPart(RegisterRenderer.CRYSTAL_GOLEM_ENTITY)),
        0.7f
    ) {

    init {
        addFeature(CrystallineGolemCrackFeatureRenderer(this))
    }

    private val TEXTURE = Identifier("amethyst_imbuement","textures/entity/crystal_golem/crystal_golem.png")

    override fun getTexture(entity: CrystallineGolemEntity): Identifier {
        return TEXTURE
    }

    override fun setupTransforms(
        crystalGolemEntity: CrystallineGolemEntity,
        matrixStack: MatrixStack,
        f: Float,
        g: Float,
        h: Float
    ) {
        super.setupTransforms(crystalGolemEntity, matrixStack, f, g, h)
        if (crystalGolemEntity.limbDistance.toDouble() < 0.01) {
            return
        }
        val j = crystalGolemEntity.limbAngle - crystalGolemEntity.limbDistance * (1.0f - h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(6.5f * k))
    }
}