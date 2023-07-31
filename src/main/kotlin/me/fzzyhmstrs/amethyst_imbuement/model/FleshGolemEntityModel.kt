package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.golem.FleshGolemEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.util.math.MathHelper

// Made with Blockbench 4.8.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
class FleshGolemEntityModel(private val root: ModelPart) : SinglePartEntityModel<FleshGolemEntity>() {
    private val head: ModelPart = root.getChild("head")
    private val rightArm: ModelPart = root.getChild("rightArm")
    private val rightSubArm: ModelPart = rightArm.getChild("rightSubArm")
    private val leftArm: ModelPart = root.getChild("leftArm")
    private val leftSubArm: ModelPart = leftArm.getChild("leftSubArm")
    private val rightLeg: ModelPart = root.getChild("rightLeg")
    private val leftLeg: ModelPart = root.getChild("leftLeg")

    override fun setAngles(entity: FleshGolemEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        head.yaw = i * (Math.PI.toFloat() / 180)
        head.pitch = j * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(f, 13.0f) * g
        leftLeg.pitch = 1.5f * MathHelper.wrap(f, 13.0f) * g
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
        val index = entity.getCrack().index
        rightSubArm.visible = index < 3
        leftSubArm.visible = index < 2
    }

    override fun animateModel(entity: FleshGolemEntity, f: Float, g: Float, h: Float) {
        val i = entity.getAttackTicks()
        if (i > 0) {
            rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
            leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
        } else {
            val j = entity.getLookingAtVillagerTicks()
            if (j > 0) {
                rightArm.pitch = -0.8f + 0.025f * MathHelper.wrap(j.toFloat(), 70.0f)
                leftArm.pitch = 0.0f
            } else {
                rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(f, 13.0f)) * g
                leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(f, 13.0f)) * g
            }
        }
    }

    override fun getPart(): ModelPart {
        return root
    }

    companion object {

        fun getTexturedModelData(): TexturedModelData {
                val modelData = ModelData()
                val modelPartData = modelData.root
                modelPartData.addChild(
                    "body",
                    ModelPartBuilder.create().uv(0, 40).cuboid(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f, Dilation(0.0f))
                        .uv(0, 70).cuboid(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, Dilation(0.5f)),
                    ModelTransform.pivot(0.0f, -7.0f, 0.0f)
                )
                modelPartData.addChild(
                    "head",
                    ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f, Dilation(0.0f))
                        .uv(24, 0).cuboid(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 0.0f, -2.0f)
                )
                modelPartData.addChild(
                    "rightLeg",
                    ModelPartBuilder.create().uv(37, 0).cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, Dilation(0.0f)),
                    ModelTransform.pivot(-4.0f, 18.0f, 0.0f)
                )
                modelPartData.addChild(
                    "leftLeg",
                    ModelPartBuilder.create().uv(60, 0).mirrored()
                        .cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, Dilation(0.0f)).mirrored(false),
                    ModelTransform.pivot(5.0f, 18.0f, 0.0f)
                )
                val arm0 = modelPartData.addChild(
                    "rightArm",
                    ModelPartBuilder.create().uv(60, 21).cuboid(-13.0f, -2.5f, -3.0f, 4.0f, 15.0f, 6.0f, Dilation(0.0f))
                        .uv(30, 73).cuboid(-12.0f, 12.5f, -2.0f, 2.0f, 4.0f, 4.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 0.0f, 0.0f)
                )
                    arm0.addChild(
                        "rightSubArm",
                        ModelPartBuilder.create().uv(60, 78)
                            .cuboid(-13.0f, 14.5f, -3.0f, 4.0f, 13.0f, 6.0f, Dilation(0.0f)),
                        ModelTransform.pivot(0.0f, 0.0f, 0.0f)
                    )
                val arm1 = modelPartData.addChild(
                    "leftArm",
                    ModelPartBuilder.create().uv(60, 42).cuboid(9.0f, -2.5f, -3.0f, 4.0f, 8.0f, 6.0f, Dilation(0.0f))
                        .uv(60, 56).cuboid(9.0f, 20.5f, -3.0f, 4.0f, 7.0f, 6.0f, Dilation(0.0f))
                        .uv(30, 81).cuboid(10.0f, 5.5f, -2.0f, 2.0f, 15.0f, 4.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 0.0f, 0.0f)
                )
                    arm1.addChild(
                        "leftSubArm",
                        ModelPartBuilder.create().uv(60, 101).cuboid(9.0f, 5.5f, -3.0f, 4.0f, 15.0f, 6.0f, Dilation(0.0f)),
                        ModelTransform.pivot(0.0f, 0.0f, 0.0f)
                    )
                return TexturedModelData.of(modelData, 128, 128)
            }
    }
}