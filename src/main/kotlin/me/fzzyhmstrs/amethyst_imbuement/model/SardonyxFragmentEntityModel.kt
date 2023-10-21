package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.living.SardonyxFragmentEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
class SardonyxFragmentEntityModel(root: ModelPart) : EntityModel<SardonyxFragmentEntity>() {
    private val leftLeg: ModelPart
    private val rightLeg: ModelPart
    private val body: ModelPart
    private val head: ModelPart
    private val leftArm: ModelPart
    private val rightArm: ModelPart

    init {
        leftLeg = root.getChild("leftLeg")
        rightLeg = root.getChild("rightLeg")
        body = root.getChild("body")
        head = root.getChild("head")
        leftArm = root.getChild("leftArm")
        rightArm = root.getChild("rightArm")
    }

    override fun setAngles(cholemEntity: SardonyxFragmentEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        head.yaw = i * (Math.PI.toFloat() / 180)
        head.pitch = j * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(f, 13.0f) * g
        leftLeg.pitch = 1.5f * MathHelper.wrap(f, 13.0f) * g
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
    }

    override fun animateModel(cholemEntity: SardonyxFragmentEntity, f: Float, g: Float, h: Float) {
        val i = cholemEntity.getAttackTicks()
        if (i > 0) {
            rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
            leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
        } else {
            rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(f, 13.0f)) * g
            leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(f, 13.0f)) * g
        }
    }

    override fun render(
        matrices: MatrixStack,
        vertexConsumer: VertexConsumer,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leftArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        rightArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
    }

    companion object {
        val texturedModelData: TexturedModelData
            get() {
                val modelData = ModelData()
                val modelPartData = modelData.root
                val leftLeg = modelPartData.addChild(
                    "leftLeg",
                    ModelPartBuilder.create().uv(8, 24).cuboid(0.5f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 19.0f, 0.0f)
                )
                val rightLeg = modelPartData.addChild(
                    "rightLeg",
                    ModelPartBuilder.create().uv(0, 24).cuboid(-2.5f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 19.0f, 0.0f)
                )
                val body = modelPartData.addChild(
                    "body",
                    ModelPartBuilder.create().uv(8, 10).cuboid(-2.5f, -8.0f, -1.5f, 5.0f, 3.0f, 3.0f, Dilation(0.0f))
                        .uv(0, 0).cuboid(-4.5f, -14.0f, -2.0f, 9.0f, 6.0f, 4.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 24.0f, 0.0f)
                )
                val head = modelPartData.addChild(
                    "head",
                    ModelPartBuilder.create().uv(30, 0).cuboid(-3.0f, -3.0f, -2.4865f, 6.0f, 6.0f, 5.0f, Dilation(0.0f))
                        .uv(8, 9).cuboid(0.0f, -5.0f, -2.25f, 0.0f, 7.0f, 7.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 7.0f, 0.0f)
                )
                val leftArm = modelPartData.addChild(
                    "leftArm",
                    ModelPartBuilder.create().uv(0, 10).cuboid(4.5f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 11.0f, 0.0f)
                )
                val rightArm = modelPartData.addChild(
                    "rightArm",
                    ModelPartBuilder.create().uv(24, 10)
                        .cuboid(-6.5f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 11.0f, 0.0f)
                )
                return TexturedModelData.of(modelData, 64, 32)
            }
    }
}