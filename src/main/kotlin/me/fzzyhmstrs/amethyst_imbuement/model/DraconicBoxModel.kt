package me.fzzyhmstrs.amethyst_imbuement.model

import com.google.common.collect.ImmutableList
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color


class DraconicBoxModel() : EntityModel<DraconicBoxEntity>() {

    private var modelPart: ModelPart? = null
    private var base: ModelPart? = null

    constructor(_modelPart: ModelPart) : this(){
        modelPart = _modelPart
        this.base = modelPart?.getChild(EntityModelPartNames.CUBE)
    }

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
        if (base != null) {
            ImmutableList.of(base as ModelPart).forEach { modelRenderer ->
                modelRenderer.render(
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
        }
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