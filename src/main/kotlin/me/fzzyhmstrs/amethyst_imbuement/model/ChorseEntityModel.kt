package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.util.math.MathHelper

@Environment(value = EnvType.CLIENT)
class ChorseEntityModel(root: ModelPart): EntityModel<ChorseEntity>() {

    private val leftFrontLeg: ModelPart
    private val rightFrontLeg: ModelPart
    private val leftHindLeg: ModelPart
    private val rightHindLeg: ModelPart
    private val body: ModelPart
    private val head: ModelPart
    private val leftWing: ModelPart
    private val rightWing: ModelPart
    init {
        leftFrontLeg = root.getChild("left_front_leg")
        rightFrontLeg = root.getChild("right_front_leg")
        leftHindLeg = root.getChild("left_rear_leg")
        rightHindLeg = root.getChild("right_rear_leg")
        body = root.getChild("body")
        head = root.getChild("head")
        leftWing = root.getChild("left_wing")
        rightWing = root.getChild("right_wing")
    }

    companion object {
        fun getTexturedModelData(dilation: Dilation = Dilation.NONE): TexturedModelData {
            val modelData = ModelData()
            val modelPartData: ModelPartData = modelData.root
            val left_front_leg: ModelPartData = modelPartData.addChild(
                "left_front_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(3.0f, 13.0f, -9.0f)
            )
            val right_front_leg: ModelPartData = modelPartData.addChild(
                "right_front_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, -9.0f)
            )
            val left_rear_leg: ModelPartData = modelPartData.addChild(
                "left_rear_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(3.0f, 13.0f, 9.0f)
            )
            val right_rear_leg: ModelPartData = modelPartData.addChild(
                "right_rear_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, 9.0f)
            )
            val body: ModelPartData = modelPartData.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, 4.0f, -11.0f, 10.0f, 10.0f, 22.0f, dilation)
                    .uv(48, 58).cuboid(0.0f, 6.0f, 11.0f, 0.0f, 3.0f, 3.0f, dilation),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val head: ModelPartData = modelPartData.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 32)
                    .cuboid(-2.0f, -13.579f, -4.2847f, 4.0f, 12.0f, 6.0f, dilation),
                ModelTransform.of(0.0f, 11.579f, -5.7153f, 0.48f, 0.0f, 0.0f)
            )
                head.addChild(
                    "cube_r1",
                    ModelPartBuilder.create().uv(54, 60).cuboid(-2.0f, -12.0f, -11.0f, 4.0f, 3.0f, 1.0f, dilation)
                        .uv(0, 60).cuboid(-3.0f, -14.0f, -12.0f, 6.0f, 2.0f, 2.0f, dilation),
                    ModelTransform.of(0.0f, 0.0f, 0.0f, -0.48f, 0.0f, 0.0f)
                )
                head.addChild(
                    "cube_r2",
                    ModelPartBuilder.create().uv(20, 32).cuboid(-3.0f, -3.0f, -2.0f, 6.0f, 6.0f, 5.0f, dilation),
                    ModelTransform.of(0.0f, -14.9894f, -1.0975f, -0.48f, 0.0f, 0.0f)
                )
            val left_wing: ModelPartData = modelPartData.addChild(
                "left_wing",
                ModelPartBuilder.create().uv(16, 48).cuboid(0.0f, 0.0f, -4.5f, 1.0f, 7.0f, 9.0f, dilation),
                ModelTransform.pivot(5.0f, 4.0f, -4.5f)
            )
            val right_wing: ModelPartData = modelPartData.addChild(
                "right_wing",
                ModelPartBuilder.create().uv(16, 48).cuboid(-1.0f, 0.0f, -4.5f, 1.0f, 7.0f, 9.0f, dilation),
                ModelTransform.pivot(-5.0f, 4.0f, -4.5f)
            )
            return TexturedModelData.of(modelData, 64, 64)
        }
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
        leftFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        rightFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leftHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        rightHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leftWing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        rightWing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
    }

    override fun animateModel(abstractHorseEntity: ChorseEntity, f: Float, g: Float, h: Float) {
        super.animateModel(abstractHorseEntity, f, g, h)
        val i = MathHelper.lerpAngleDegrees(
            h,
            (abstractHorseEntity as AbstractHorseEntity).prevBodyYaw,
            (abstractHorseEntity as AbstractHorseEntity).bodyYaw
        )
        val j = MathHelper.lerpAngleDegrees(
            h,
            (abstractHorseEntity as AbstractHorseEntity).prevHeadYaw,
            (abstractHorseEntity as AbstractHorseEntity).headYaw
        )
        val k = MathHelper.lerp(
            h,
            (abstractHorseEntity as AbstractHorseEntity).prevPitch,
            (abstractHorseEntity as Entity).pitch
        )
        var l = j - i
        var m = k * (Math.PI.toFloat() / 180)
        if (l > 20.0f) {
            l = 20.0f
        }
        if (l < -20.0f) {
            l = -20.0f
        }
        if (g > 0.2f) {
            m += MathHelper.cos(f * 0.8f) * 0.15f * g
        }
        val n = (abstractHorseEntity as AbstractHorseEntity).getEatingGrassAnimationProgress(h)
        val o = 0f//(abstractHorseEntity as AbstractHorseEntity).getAngryAnimationProgress(h)
        val p = 1.0f - o
        val q = (abstractHorseEntity as AbstractHorseEntity).getEatingAnimationProgress(h)
        val bl = (abstractHorseEntity as AbstractHorseEntity).tailWagTicks != 0
        val r = (abstractHorseEntity as AbstractHorseEntity).age.toFloat() + h
        head.pivotY = 11.579f
        head.pivotZ = -5.7153f
        body.pitch = 0.0f
        head.pitch = 0.5235988f + m
        head.yaw = l * (Math.PI.toFloat() / 180)
        val s = if ((abstractHorseEntity as Entity).isTouchingWater) 0.2f else 1.0f
        val t = MathHelper.cos(s * f * 0.6662f + Math.PI.toFloat())
        val u = t * 0.8f * g
        val v = (1.0f - Math.max(o, n)) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f)
        head.pitch = o * (0.2617994f + m) + n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v
        head.yaw = o * l * (Math.PI.toFloat() / 180) + (1.0f - Math.max(o, n)) * head.yaw
        head.pivotY = o * -4.0f + n * 11.0f + (1.0f - Math.max(o, n)) * head.pivotY
        head.pivotZ = o * -4.0f + n * -6.5f + (1.0f - Math.max(o, n)) * head.pivotZ
        body.pitch = o * -0.7853982f + p * body.pitch
        val w = 0.2617994f * o
        val x = MathHelper.cos(r * 0.6f + Math.PI.toFloat())
        this.leftFrontLeg.pivotY = 2.0f * o + 14.0f * p
        this.leftFrontLeg.pivotZ = -6.0f * o - 10.0f * p
        this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY
        this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ
        val y = (-1.0471976f + x) * o + u * p
        val z = (-1.0471976f - x) * o - u * p
        this.leftHindLeg.pitch = w - t * 0.5f * g * p
        this.rightHindLeg.pitch = w + t * 0.5f * g * p
        this.leftFrontLeg.pitch = y
        this.rightFrontLeg.pitch = z
        val bl2 = (abstractHorseEntity as PassiveEntity).isBaby
        this.rightHindLeg.visible = !bl2
        this.leftHindLeg.visible = !bl2
        this.rightFrontLeg.visible = !bl2
        this.leftFrontLeg.visible = !bl2
        body.pivotY = if (bl2) 10.8f else 0.0f
    }

    override fun setAngles(
        entity: ChorseEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        this.rightWing.roll = animationProgress
        this.leftWing.roll = -animationProgress
    }

}