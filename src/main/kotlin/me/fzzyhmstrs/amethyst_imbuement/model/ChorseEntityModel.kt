package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper

@Environment(value = EnvType.CLIENT)
class ChorseEntityModel(private val grown: ModelPart, private val baby: ModelPart): EntityModel<ChorseEntity>() {

    private val leftFrontLeg: ModelPart = grown.getChild("left_front_leg")
    private val rightFrontLeg: ModelPart = grown.getChild("right_front_leg")
    private val leftHindLeg: ModelPart = grown.getChild("left_rear_leg")
    private val rightHindLeg: ModelPart = grown.getChild("right_rear_leg")
    private val head: ModelPart = grown.getChild("head")
    private val leftWing: ModelPart = grown.getChild("left_wing")
    private val rightWing: ModelPart = grown.getChild("right_wing")

    private val bb_leftFrontLeg: ModelPart = baby.getChild("bb_left_front_leg")
    private val bb_rightFrontLeg: ModelPart = baby.getChild("bb_right_front_leg")
    private val bb_leftHindLeg: ModelPart = baby.getChild("bb_left_rear_leg")
    private val bb_rightHindLeg: ModelPart = baby.getChild("bb_right_rear_leg")
    private val bb_head: ModelPart = baby.getChild("bb_head")

    companion object {

        fun getBabyTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                "bb_body",
                ModelPartBuilder.create().uv(0, 52).cuboid(-1.5f, -5.0f, -2.5f, 3.0f, 3.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 45).cuboid(0.0f, -4.0f, -1.0f, 0.0f, 2.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            modelPartData.addChild(
                "bb_left_front_leg",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.0f, 22.0f, -1.5f)
            )
            val right_front_leg = modelPartData.addChild(
                "bb_right_front_leg",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(-1.0f, 22.0f, -1.5f)
            )
            val left_rear_leg = modelPartData.addChild(
                "bb_left_rear_leg",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.0f, 22.0f, 1.5f)
            )
            val right_rear_leg = modelPartData.addChild(
                "bb_right_rear_leg",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(-1.0f, 22.0f, 1.5f)
            )
            val head = modelPartData.addChild(
                "bb_head",
                ModelPartBuilder.create().uv(48, 52).cuboid(-2.0f, -3.0f, -3.0f, 4.0f, 4.0f, 4.0f, Dilation(0.0f))
                    .uv(42, 62).cuboid(-1.0f, -1.0f, -4.0f, 2.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(53, 47).cuboid(-1.5f, -3.35f, -2.5f, 3.0f, 1.0f, 3.0f, Dilation(0.0f))
                    .uv(56, 40).cuboid(-1.0f, -8.05f, -2.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f))
                    .uv(11, 50).cuboid(-1.0f, -3.8f, -2.0f, 2.0f, 2.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 19.0f, -1.0f)
            )
            head.addChild(
                "bow_2_r1",
                ModelPartBuilder.create().uv(24, 43).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 0.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(-1.4306f, -3.4511f, -1.0f, 0.0f, 0.0f, 0.3054f)
            )
            head.addChild(
                "bow_1_r1",
                ModelPartBuilder.create().uv(16, 43).cuboid(-0.5f, 0.0f, -2.0f, 4.0f, 0.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -3.0f, -1.0f, 0.0f, 0.0f, -0.3054f)
            )
            return TexturedModelData.of(modelData, 64, 64)
        }

        fun getGrownTexturedModelData(dilation: Dilation = Dilation.NONE): TexturedModelData {
            val modelData = ModelData()
            val modelPartData: ModelPartData = modelData.root
            modelPartData.addChild(
                "left_front_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(3.0f, 13.0f, -9.0f)
            )
            modelPartData.addChild(
                "right_front_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, -9.0f)
            )
            modelPartData.addChild(
                "left_rear_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(3.0f, 13.0f, 9.0f)
            )
            modelPartData.addChild(
                "right_rear_leg",
                ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, 9.0f)
            )
            modelPartData.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, 4.0f, -11.0f, 10.0f, 10.0f, 22.0f, dilation)
                    .uv(48, 58).cuboid(0.0f, 6.0f, 11.0f, 0.0f, 3.0f, 3.0f, dilation),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
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
                head.addChild(
                    "hat_brim",
                    ModelPartBuilder.create().uv(38, 43).cuboid(-2f, -3.35f, -1.5f, 4.0f, 1.0f, 4.0f, Dilation.NONE),
                    ModelTransform.of(0.0f, -14.9894f, -1.0975f, -0.48f, 0.0f, 0.0f)
                )
                head.addChild(
                    "hat_hat",
                    ModelPartBuilder.create().uv(42, 32).cuboid(-1.5f, -11.05f, -1f, 3.0f, 8.0f, 3.0f, Dilation.NONE),
                    ModelTransform.of(0.0f, -14.9894f, -1.0975f, -0.48f, 0.0f, 0.0f)
                )
            modelPartData.addChild(
                "left_wing",
                ModelPartBuilder.create().uv(16, 48).cuboid(0.0f, 0.0f, -4.5f, 1.0f, 7.0f, 9.0f, dilation),
                ModelTransform.pivot(5.0f, 5.0f, -4.5f)
            )
            modelPartData.addChild(
                "right_wing",
                ModelPartBuilder.create().uv(16, 48).cuboid(-1.0f, 0.0f, -4.5f, 1.0f, 7.0f, 9.0f, dilation),
                ModelTransform.pivot(-5.0f, 5.0f, -4.5f)
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
        this.grown.render(matrices,vertexConsumer,light, overlay, red, green, blue, alpha)
        this.baby.render(matrices,vertexConsumer,light, overlay, red, green, blue, alpha)
    }

    override fun animateModel(chorseEntity: ChorseEntity, f: Float, g: Float, h: Float) {
        val i = MathHelper.lerpAngleDegrees(h, chorseEntity.prevBodyYaw, chorseEntity.bodyYaw)
        val j = MathHelper.lerpAngleDegrees(h, chorseEntity.prevHeadYaw, chorseEntity.headYaw)
        val k = MathHelper.lerp(h, chorseEntity.prevPitch, chorseEntity.pitch)
        val l = MathHelper.clamp(j - i, -20f, 20f)
        var m = k * (Math.PI.toFloat() / 180)
        if (g > 0.2f) {
            m += MathHelper.cos(f * 0.8f) * 0.15f * g
        }
        if (!chorseEntity.isBaby) {
            grown.visible = true
            baby.visible = false

            val n = chorseEntity.getEatingGrassAnimationProgress(h)
            //val p = 1.0f
            val q = chorseEntity.getEatingAnimationProgress(h)
            val r = chorseEntity.age.toFloat() + h
            //head.pivotY = 11.579f
            //head.pivotZ = -5.7153f
            //body.pitch = 0.0f
            head.pitch = 0.5235988f + m
            head.yaw = l * (Math.PI.toFloat() / 180)
            val s = if ((chorseEntity as Entity).isTouchingWater) 0.2f else 1.0f
            val t = MathHelper.cos(s * f * 0.6662f + Math.PI.toFloat())
            val u = t * 0.8f * g
            val v = (1.0f - n) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f)
            head.pitch = n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v
            head.yaw = (1.0f - n) * head.yaw
            head.pivotY = n * 11.0f + (1.0f - n) * 11.579f
            head.pivotZ = n * -6.5f + (1.0f - n) * (-5.7153f)
            //body.pitch = p * body.pitch
            //val w = 0f
            //val x = MathHelper.cos(r * 0.6f + Math.PI.toFloat())
           /* this.leftFrontLeg.pivotY = 14.0f * 1.0f
            this.leftFrontLeg.pivotZ = -10.0f * 1.0f
            this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY
            this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ*/
            val y = u * 1.0f
            val z = -(u * 1.0f)
            leftHindLeg.pitch = 0f - t * 0.5f * g * 1.0f
            rightHindLeg.pitch = 0f + t * 0.5f * g * 1.0f
            leftFrontLeg.pitch = y
            rightFrontLeg.pitch = z
            //val bl2 = (chorseEntity as PassiveEntity).isBaby
            /*this.body.visible = !bl2
                this.rightHindLeg.visible = !bl2
                this.leftHindLeg.visible = !bl2
                this.rightFrontLeg.visible = !bl2
                this.leftFrontLeg.visible = !bl2*/
            //body.pivotY = if (bl2) 10.8f else 0.0f
        } else {
            grown.visible = false
            baby.visible = true
            //head.pivotY = 11.579f
            //head.pivotZ = -5.7153f
            //body.pitch = 0.0f
            bb_head.pitch = m
            bb_head.yaw = l * (Math.PI.toFloat() / 180)
            val s = if ((chorseEntity as Entity).isTouchingWater) 0.2f else 1.0f
            val t = MathHelper.cos(s * f * 0.6662f + Math.PI.toFloat())
            val u = t * 0.8f * g
            //body.pitch = p * body.pitch
            //val w = 0f
            //val x = MathHelper.cos(r * 0.6f + Math.PI.toFloat())
            /*this.leftFrontLeg.pivotY = 14.0f * 1.0f
            this.leftFrontLeg.pivotZ = -10.0f * 1.0f
            this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY
            this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ*/
            val y = u * 1.0f
            val z = -(u * 1.0f)
            bb_leftHindLeg.pitch = 0f - t * 0.5f * g * 1.0f
            bb_rightHindLeg.pitch = 0f + t * 0.5f * g * 1.0f
            bb_leftFrontLeg.pitch = y
            bb_rightFrontLeg.pitch = z
            //val bl2 = (chorseEntity as PassiveEntity).isBaby
            /*this.body.visible = !bl2
                this.rightHindLeg.visible = !bl2
                this.leftHindLeg.visible = !bl2
                this.rightFrontLeg.visible = !bl2
                this.leftFrontLeg.visible = !bl2*/
            //body.pivotY = if (bl2) 10.8f else 0.0f
        }
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