package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.living.FloralConstructEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.util.math.MathHelper

class FloralConstructEntityModel(private val root: ModelPart): SinglePartEntityModel<FloralConstructEntity>() {

    private val leftLeg: ModelPart = root.getChild("leftLeg")
    private val rightLeg: ModelPart = root.getChild("rightLeg")
    private val body: ModelPart = root.getChild("body")
    private val head: ModelPart = root.getChild("head")
    private val petal4: ModelPart = head.getChild("petal4")
    private val petal3: ModelPart = head.getChild("petal3")
    private val petal2: ModelPart = head.getChild("petal2")
    private val petal1: ModelPart = head.getChild("petal1")
    private val leftArm: ModelPart = root.getChild("leftArm")
    private val rightArm: ModelPart = root.getChild("rightArm")

    companion object{
        fun getTexturedModelData(): TexturedModelData? {
            val modelData = ModelData()
            val modelPartData = modelData.root
            val head = modelPartData.addChild(
                "head",
                ModelPartBuilder.create().uv(8, 16).cuboid(-2.0f, -17.0f, -2.0f, 4.0f, 4.0f, 4.0f, Dilation(0.0f))
                    .uv(40, 0).cuboid(-4.0f, -17.0f, -4.0f, 8.0f, 0.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 11.0f, 0.0f)
            )
                val petal4 = head.addChild(
                    "petal4",
                    ModelPartBuilder.create().uv(52, 2).cuboid(0.0f, -5.0f, -3.0f, 0.0f, 5.0f, 6.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -17.0f, 0.0f, 0.0f, 0.0f, 1.0908f)
                )
                val petal3 = head.addChild(
                    "petal3",
                    ModelPartBuilder.create().uv(52, 2).cuboid(0.0f, -5.0f, -3.0f, 0.0f, 5.0f, 6.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -17.0f, 0.0f, 0.0f, 0.0f, -1.0908f)
                )
                val petal2 = head.addChild(
                    "petal2",
                    ModelPartBuilder.create().uv(52, 8).cuboid(-3.0f, -5.0f, 0.0f, 6.0f, 5.0f, 0.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -17.0f, 0.0f, -1.0908f, 0.0f, 0.0f)
                )
                val petal1 = head.addChild(
                    "petal1",
                    ModelPartBuilder.create().uv(52, 8).cuboid(-3.0f, -5.0f, 0.0f, 6.0f, 5.0f, 0.0f, Dilation(0.0f)),
                    ModelTransform.of(0.0f, -17.0f, 0.0f, 1.0908f, 0.0f, 0.0f)
                )
            val body = modelPartData.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -13.0f, -3.0f, 8.0f, 10.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val leftLeg = modelPartData.addChild(
                "leftLeg",
                ModelPartBuilder.create().uv(0, 25).cuboid(1.0f, -3.0f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.0f, 21.0f, 0.0f)
            )
            val rightLeg = modelPartData.addChild(
                "rightLeg",
                ModelPartBuilder.create().uv(0, 25).cuboid(-4.0f, -3.0f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.0f, 21.0f, 0.0f)
            )
            val leftArm = modelPartData.addChild(
                "leftArm",
                ModelPartBuilder.create().uv(0, 16).cuboid(4.0f, -12.0f, -1.0f, 2.0f, 7.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(5.0f, 12.0f, 0.0f)
            )
            val rightArm = modelPartData.addChild(
                "rightArm",
                ModelPartBuilder.create().uv(0, 16).cuboid(-6.0f, -12.0f, -1.0f, 2.0f, 7.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-5.0f, 12.0f, 0.0f)
            )
            return TexturedModelData.of(modelData, 64, 32)
        }
    }

    override fun setAngles(
        entity: FloralConstructEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        head.yaw = headYaw * (Math.PI.toFloat() / 180)
        head.pitch = headPitch * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(limbAngle, 13.0f) * limbDistance
        leftLeg.pitch = 1.5f * MathHelper.wrap(limbAngle, 13.0f) * limbDistance
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
    }

    override fun getPart(): ModelPart {
        return root
    }
}