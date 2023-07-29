package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.horse.SeahorseEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack

// Made with Blockbench 4.8.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
class SeahorseEntityModel(root: ModelPart) : EntityModel<SeahorseEntity>() {
    private val body: ModelPart = root.getChild("Body")
    private val bag1: ModelPart = body.getChild("Bag1")
    private val bag2: ModelPart = body.getChild("Bag2")
    private val tailA: ModelPart = body.getChild("TailA")
    private val saddle: ModelPart = body.getChild("Saddle")


    private val leg1A: ModelPart = root.getChild("Leg1A")
    private val leg2A: ModelPart = root.getChild("Leg2A")
    private val leg3A: ModelPart = root.getChild("Leg3A")
    private val leg4A: ModelPart = root.getChild("Leg4A")

    private val head: ModelPart = root.getChild("Head")
    private val saddleMouthL: ModelPart = head.getChild("SaddleMouthL")
    private val saddleMouthR: ModelPart = head.getChild("SaddleMouthR")
    private val headSaddle: ModelPart = head.getChild("HeadSaddle")
    private val saddleMouthLineL: ModelPart = head.getChild("SaddleMouthLineL")
    private val saddleMouthLineR: ModelPart = head.getChild("SaddleMouthLineR")

    override fun setAngles(
        entity: SeahorseEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val bl1 = entity.isSaddled
        val bl2 = entity.hasPassengers()
        saddle.visible = bl1
        saddleMouthL.visible = bl1
        saddleMouthR.visible = bl1
        headSaddle.visible = bl1
        saddleMouthLineL.visible = bl1 && bl2
        saddleMouthLineR.visible = bl1 && bl2
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

        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)

        leg1A.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leg2A.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leg3A.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
        leg4A.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
    }

    companion object {
        fun getTexturedModelData(dilation: Dilation = Dilation.NONE): TexturedModelData{
            val modelData = ModelData()
            val modelPartData = modelData.root
            val body = modelPartData.addChild(
                "Body",
                ModelPartBuilder.create().uv(0, 32)
                    .cuboid(-5.0f, -8.0f, -20.0f, 10.0f, 10.0f, 22.0f, dilation),
                ModelTransform.pivot(0.0f, 11.0f, 9.0f)
            )
                body.addChild(
                    "TailA",
                    ModelPartBuilder.create().uv(42, 36).cuboid(-1.5f, 0.0f, -3.0f, 3.0f, 14.0f, 4.0f, dilation),
                    ModelTransform.of(0.0f, 4.0f, 11.0f, 0.5236f, 0.0f, 0.0f)
                )
                body.addChild(
                    "Saddle",
                    ModelPartBuilder.create().uv(26, 0).cuboid(-5.0f, 1.0f, -5.5f, 10.0f, 9.0f, 9.0f, Dilation(0.5f)),
                    ModelTransform.pivot(0.0f, 2.0f, 2.0f)
                )
                body.addChild(
                    "Bag1",
                    ModelPartBuilder.create().uv(26, 21).cuboid(-9.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation),
                    ModelTransform.of(-5.0f, 3.0f, 11.0f, 0.0f, -1.5708f, 0.0f)
                )
                body.addChild(
                    "Bag2",
                    ModelPartBuilder.create().uv(26, 21).mirrored()
                        .cuboid(1.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation).mirrored(false),
                    ModelTransform.of(5.0f, 3.0f, 11.0f, 0.0f, 1.5708f, 0.0f)
                )
            val head = modelPartData.addChild(
                "Head",
                ModelPartBuilder.create().uv(0, 13).cuboid(-3.0f, -5.0f, -6.0f, 6.0f, 5.0f, 7.0f, dilation)
                    .uv(0, 25).cuboid(-2.0f, -5.0f, -11.0f, 4.0f, 5.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -4.0f, -11.0f, 0.5236f, 0.0f, 0.0f)
            )
                head.addChild(
                    "Ear1",
                    ModelPartBuilder.create().uv(19, 16).mirrored()
                        .cuboid(-0.5f, -18.0f, 2.99f, 2.0f, 3.0f, 1.0f, dilation).mirrored(false),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, 0.0873f)
                )
                head.addChild(
                    "Ear2",
                    ModelPartBuilder.create().uv(19, 16).cuboid(-1.5f, -18.0f, 2.99f, 2.0f, 3.0f, 1.0f, dilation),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, -0.0873f)
                )
                head.addChild(
                    "Neck",
                    ModelPartBuilder.create().uv(0, 35).cuboid(-2.0f, -11.0f, -3.0f, 4.0f, 12.0f, 7.0f, dilation)
                        .uv(64, 44).cuboid(-1.0f, -16.0f, 4.0f, 2.0f, 16.0f, 4.0f, dilation),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, 0.0f)
                )
                head.addChild(
                    "SaddleMouthL",
                    ModelPartBuilder.create().uv(29, 5).cuboid(2.0f, -14.0f, -6.0f, 1.0f, 2.0f, 2.0f, dilation),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, 0.0f)
                )
                head.addChild(
                    "SaddleMouthR",
                    ModelPartBuilder.create().uv(29, 5).cuboid(-3.0f, -14.0f, -6.0f, 1.0f, 2.0f, 2.0f, dilation),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, 0.0f)
                )
                head.addChild(
                    "SaddleMouthLineL",
                    ModelPartBuilder.create().uv(32, 2).cuboid(3.1f, -10.0f, -11.5f, 0.0f, 3.0f, 16.0f, dilation),
                    ModelTransform.pivot(0.0f, 7.0f, -8.0f)
                )
                head.addChild(
                    "SaddleMouthLineR",
                    ModelPartBuilder.create().uv(32, 2)
                        .cuboid(-3.1f, -10.0f, -11.5f, 0.0f, 3.0f, 16.0f, dilation),
                    ModelTransform.pivot(0.0f, 7.0f, -8.0f)
                )
                head.addChild(
                    "HeadSaddle",
                    ModelPartBuilder.create().uv(19, 0).cuboid(-2.0f, -16.0f, -5.0f, 4.0f, 5.0f, 2.0f, Dilation(0.25f))
                        .uv(0, 0).cuboid(-3.0f, -16.0f, -3.0f, 6.0f, 5.0f, 7.0f, Dilation(0.25f)),
                    ModelTransform.of(0.0f, 7.0f, -8.0f, 0.5236f, 0.0f, 0.0f)
                )
            modelPartData.addChild(
                "Leg1A",
                ModelPartBuilder.create().uv(48, 21).mirrored()
                    .cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 11.0f, 4.0f, dilation).mirrored(false)
                    .uv(76, -3).cuboid(2.0f, 0.0f, 2.0f, 0.0f, 11.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.0f, 13.0f, 9.0f)
            )
            modelPartData.addChild(
                "Leg2A",
                ModelPartBuilder.create().uv(48, 21).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 11.0f, 4.0f, dilation)
                    .uv(70, -3).cuboid(-2.0f, 0.0f, 2.0f, 0.0f, 11.0f, 3.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, 9.0f)
            )
            modelPartData.addChild(
                "Leg3A",
                ModelPartBuilder.create().uv(64, 21).mirrored()
                    .cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 11.0f, 4.0f, dilation).mirrored(false)
                    .uv(64, -3).cuboid(2.0f, 0.0f, 2.0f, 0.0f, 11.0f, 3.0f, dilation),
                ModelTransform.pivot(3.0f, 13.0f, -9.0f)
            )
            modelPartData.addChild(
                "Leg4A",
                ModelPartBuilder.create().uv(64, 21).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 11.0f, 4.0f, dilation)
                    .uv(82, -3).cuboid(-2.0f, 0.0f, 2.0f, 0.0f, 11.0f, 3.0f, dilation),
                ModelTransform.pivot(-3.0f, 13.0f, -9.0f)
            )
            return TexturedModelData.of(modelData, 128, 64)
        }
    }
}