package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.living.CholemEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper

@Environment(value = EnvType.CLIENT)
class CholemEntityModel(private val root: ModelPart): SinglePartEntityModel<CholemEntity>() {

    private var leftLeg = root.getChild("leftLeg")
    private var rightLeg = root.getChild("rightLeg")
    private var body = root.getChild("body")
    private var head = root.getChild("head")
    private var leftArm = root.getChild("leftArm")
    private var rightArm = root.getChild("rightArm")

    companion object{
        fun getTexturedModelData(): TexturedModelData? {
            val modelData = ModelData()
            val modelPartData = modelData.root
            val leftLeg = modelPartData.addChild(
                "leftLeg",
                ModelPartBuilder.create().uv(26, 0).cuboid(1.0f, 0.0f, -0.5f, 1.0f, 5.0f, 1.0f, Dilation(0.0f))
                    .uv(5, 16).cuboid(0.0f, 4.95f, -2.0f, 3.0f, 0.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 19.0f, 0.0f)
            )
            val rightLeg = modelPartData.addChild(
                "rightLeg",
                ModelPartBuilder.create().uv(26, 0).cuboid(-2.0f, 0.0f, -0.5f, 1.0f, 5.0f, 1.0f, Dilation(0.0f))
                    .uv(5, 16).cuboid(-3.0f, 4.95f, -2.0f, 3.0f, 0.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 19.0f, 0.0f)
            )
            val body = modelPartData.addChild(
                "body",
                ModelPartBuilder.create().uv(8, 10).cuboid(-2.5f, -8.0f, -1.5f, 5.0f, 3.0f, 3.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-4.5f, -14.0f, -2.0f, 9.0f, 6.0f, 4.0f, Dilation(0.0f))
                    .uv(8, 16).cuboid(0.0f, -8.0f, 1.0f, 0.0f, 3.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val head = modelPartData.addChild(
                "head",
                ModelPartBuilder.create().uv(30, 0).cuboid(-3.0f, -3.0f, -2.4865f, 6.0f, 6.0f, 5.0f, Dilation(0.0f))
                    .uv(30, 15).cuboid(-3.0f, -1.0f, -4.389f, 6.0f, 2.0f, 2.0f, Dilation(0.0f))
                    .uv(30, 11).cuboid(-2.0f, 1.0f, -3.389f, 4.0f, 3.0f, 1.0f, Dilation(0.0f))
                    .uv(30, 19).cuboid(-0.5f, -4.0f, -1.5f, 1.0f, 5.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 7.0f, 0.0f)
            )
            val leftArm = modelPartData.addChild(
                "leftArm",
                ModelPartBuilder.create().uv(0, 10).cuboid(4.5f, -1.0f, -1.0f, 2.0f, 12.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 12.0f, 0.0f)
            )
            val rightArm = modelPartData.addChild(
                "rightArm",
                ModelPartBuilder.create().uv(0, 10).cuboid(-6.5f, -1.0f, -1.0f, 2.0f, 12.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 12.0f, 0.0f)
            )
            return TexturedModelData.of(modelData, 64, 32)
        }
    }

    override fun setAngles(cholemEntity: CholemEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        head.yaw = i * (Math.PI.toFloat() / 180)
        head.pitch = j * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(f, 13.0f) * g
        leftLeg.pitch = 1.5f * MathHelper.wrap(f, 13.0f) * g
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
    }

    override fun animateModel(cholemEntity: CholemEntity, f: Float, g: Float, h: Float) {
        val i = cholemEntity.getAttackTicks()
        if (i > 0) {
            rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
            leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
        } else {
            val j = cholemEntity.getLookingAtVillagerTicks()
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
        return this.root
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
        this.part.render(matrices, vertices, light, overlay, red, green, blue, alpha)
    }

}