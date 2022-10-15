package me.fzzyhmstrs.amethyst_imbuement.model

import com.google.common.collect.ImmutableList
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.util.math.MatrixStack


class DraconicBoxModel(_modelPart: ModelPart) : EntityModel<DraconicBoxEntity>() {

    private var modelPart: ModelPart = _modelPart
    private var base: ModelPart = modelPart.getChild(EntityModelPartNames.CUBE)


    override fun render(
        matrices: MatrixStack?,
        vertices: VertexConsumer?,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        base.render(
                matrices,
                vertices,
                light,
                overlay,
                red,
                green,
                blue,
                alpha
            )
    }

    override fun setAngles(
        entity: DraconicBoxEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        return
    }

    companion object {
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                EntityModelPartNames.CUBE,
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(-6f, 12f, -6f, 12f, 12f, 12f),
                ModelTransform.pivot(0f, 0f, 0f
                )
            )
            return TexturedModelData.of(modelData,64,64)
        }
    }
}