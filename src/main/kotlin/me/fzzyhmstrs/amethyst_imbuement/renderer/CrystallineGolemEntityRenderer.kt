package me.fzzyhmstrs.amethyst_imbuement.renderer

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.CrystallineGolemCrackFeatureRenderer
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyRotation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import kotlin.math.abs

@Suppress("PrivatePropertyName")
@Environment(value = EnvType.CLIENT)
class CrystallineGolemEntityRenderer(context: EntityRendererFactory.Context) :
    MobEntityRenderer<CrystallineGolemEntity, CrystallineGolemEntityModel>(context,
        CrystallineGolemEntityModel(context.getPart(RegisterRenderer.CRYSTAL_GOLEM_ENTITY)),
        0.7f
    ) {

    init {
        addFeature(CrystallineGolemCrackFeatureRenderer(this))
    }

    private val TEXTURE = Identifier(AI.MOD_ID,"textures/entity/crystal_golem/crystal_golem.png")

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
        if (crystalGolemEntity.limbAnimator.speed < 0.01) {
            return
        }
        val j = crystalGolemEntity.limbAnimator.getPos(h) + 6.0f
        val k = (abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f
        matrixStack.multiply(FzzyRotation.POSITIVE_Z.degrees(6.5f * k))
    }
}