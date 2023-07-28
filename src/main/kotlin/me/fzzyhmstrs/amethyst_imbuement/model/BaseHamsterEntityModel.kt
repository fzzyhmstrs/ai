package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.hamster.BaseHamsterEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper

open class BaseHamsterEntityModel<T: BaseHamsterEntity>(root: ModelPart): EntityModel<T>() {
    private var body: ModelPart
    private var left_front_leg: ModelPart
    private var left_hind_leg: ModelPart
    private var right_hind_leg: ModelPart
    private var right_front_leg: ModelPart
    init {
        body = root.getChild("body")
        left_front_leg = root.getChild("left_front_leg")
        left_hind_leg = root.getChild("left_hind_leg")
        right_front_leg = root.getChild("right_front_leg")
        right_hind_leg = root.getChild("right_hind_leg")
    }

    companion object {
        fun getModelData(dilation: Dilation = Dilation(0.0f)): ModelData {
            val modelData = ModelData()
            val modelPartData: ModelPartData = modelData.root
            val body: ModelPartData = modelPartData.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, -2.5f, -6.0f, 5.0f, 3.0f, 6.0f, dilation)
                    .uv(20, 0).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f, dilation),
                ModelTransform.pivot(0.0f, 23.0f, 3.0f)
            )
                body.addChild(
                    "left_ear_r1",
                    ModelPartBuilder.create().uv(0, -1).cuboid(0.0f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, dilation),
                    ModelTransform.of(2.75f, -1.5f, -5.25f, 0.0f, 0.48f, 0.0f)
                )
                body.addChild(
                    "right_ear_r1",
                    ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, dilation),
                    ModelTransform.of(-2.75f, -1.5f, -5.25f, 0.0f, -0.48f, 0.0f)
                )
            modelPartData.addChild(
                "left_front_leg",
                ModelPartBuilder.create().uv(16, 0).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, dilation),
                ModelTransform.pivot(1.5f, 23.5f, -1.5f)
            )
            modelPartData.addChild(
                "left_hind_leg",
                ModelPartBuilder.create().uv(16, 0).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, dilation),
                ModelTransform.pivot(1.5f, 23.5f, 1.5f)
            )
            modelPartData.addChild(
                "right_hind_leg",
                ModelPartBuilder.create().uv(16, 0).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, dilation),
                ModelTransform.pivot(-1.5f, 23.5f, 1.5f)
            )
            modelPartData.addChild(
                "right_front_leg",
                ModelPartBuilder.create().uv(16, 0).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, dilation),
                ModelTransform.pivot(-1.5f, 23.5f, -1.5f)
            )
            return modelData
        }

        fun getTexturedModelData(dilation: Dilation = Dilation(0.0f)): TexturedModelData{
            return getTexturedModelData(getModelData(dilation))
        }
        
        fun getTexturedModelData(modelData: ModelData): TexturedModelData{
            return TexturedModelData.of(modelData,32,16)
        }

    }

    override fun setAngles(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.left_front_leg.pitch = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        this.left_hind_leg.pitch = MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
        this.right_front_leg.pitch = MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
        this.right_hind_leg.pitch = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
    }

    override fun render(
        matrices: MatrixStack?,
        vertexConsumer: VertexConsumer?,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        left_front_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        left_hind_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        right_front_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        right_hind_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
    }

}
