package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.util.math.MathHelper

// Made with Blockbench 4.8.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
@Environment(value = EnvType.CLIENT)
class SardonyxElementalEntityModel(private val root: ModelPart) : SinglePartEntityModel<SardonyxElementalEntity>() {
    private val head: ModelPart = root.getChild("head")
    private val jaw: ModelPart = head.getChild("jaw")
    private val rightArm: ModelPart = root.getChild("rightArm")
    private val leftArm: ModelPart = root.getChild("leftArm")
    private val rightLeg: ModelPart = root.getChild("rightLeg")
    private val leftLeg: ModelPart = root.getChild("leftLeg")

    override fun setAngles(
        entity: SardonyxElementalEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.yaw = netHeadYaw * (Math.PI.toFloat() / 180)
        head.pitch = headPitch * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(limbSwing, 13.0f) * limbSwingAmount
        leftLeg.pitch = 1.5f * MathHelper.wrap(limbSwing, 13.0f) * limbSwingAmount
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
        jaw.pivotY = 0.0f
        if (entity.target != null) {
            jaw.pivotY += 3.0f
        }
    }

    override fun animateModel(crystalGolemEntity: SardonyxElementalEntity, f: Float, g: Float, h: Float) {
        val i = crystalGolemEntity.getAttackTicks()
        if (i > 0) {
            rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
            leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
        } else {
            rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(f, 13.0f)) * g
            leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(f, 13.0f)) * g
        }
    }

    companion object {
        fun getTexturedModelData(dilation: Dilation = Dilation.NONE): TexturedModelData {
                val modelData = ModelData()
                val modelPartData = modelData.root
                val body = modelPartData.addChild(
                    "body",
                    ModelPartBuilder.create().uv(0, 38)
                        .cuboid(-11.0f, -6.0f, -7.0f, 22.0f, 16.0f, 14.0f, dilation)
                        .uv(0, 68).cuboid(-7.5f, 9.0f, -4.0f, 15.0f, 6.0f, 8.0f, dilation)
                        .uv(84, 84).cuboid(8.0f, -9.0f, -6.0f, 10.0f, 10.0f, 12.0f, dilation)
                        .uv(84, 106).cuboid(-18.0f, -9.0f, -6.0f, 10.0f, 10.0f, 12.0f, dilation)
                        .uv(104, 6).cuboid(0.0f, -11.0f, 1.0f, 0.0f, 18.0f, 12.0f, dilation),
                    ModelTransform.pivot(0.0f, -7.0f, 0.0f)
                )
                    body.addChild(
                        "shardsR",
                        ModelPartBuilder.create().uv(104, 24)
                            .cuboid(-4.0f, -11.0f, 1.0f, 0.0f, 18.0f, 12.0f, dilation),
                        ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, -0.1745f, -0.1309f)
                    )
                    body.addChild(
                        "shardsL",
                        ModelPartBuilder.create().uv(104, -12)
                            .cuboid(4.0f, -11.0f, 1.0f, 0.0f, 18.0f, 12.0f, dilation),
                        ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.1745f, 0.1309f)
                    )
                val head = modelPartData.addChild(
                    "head",
                    ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -9.0f, -5.5f, 8.0f, 7.0f, 8.0f, dilation),
                    ModelTransform.pivot(0.0f, -11.0f, -4.0f)
                )
                    head.addChild(
                        "jaw",
                        ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -2.0f, -5.5f, 8.0f, 3.0f, 8.0f, dilation),
                    ModelTransform.pivot(0.0f, 0.0f, 0.0f)
                )
                modelPartData.addChild(
                    "rightArm",
                    ModelPartBuilder.create().uv(20, 99).cuboid(-16.0f, 2.5f, -3.0f, 4.0f, 23.0f, 6.0f, dilation),
                    ModelTransform.pivot(0.0f, -10.0f, 0.0f)
                )
                modelPartData.addChild(
                    "leftArm",
                    ModelPartBuilder.create().uv(0, 99).cuboid(12.0f, 2.5f, -3.0f, 4.0f, 23.0f, 6.0f, dilation),
                    ModelTransform.pivot(0.0f, -10.0f, 0.0f)
                )
                modelPartData.addChild(
                    "rightLeg",
                    ModelPartBuilder.create().uv(32, -1).cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 6.0f, dilation),
                    ModelTransform.pivot(-4.0f, 11.0f, 0.0f)
                )
                modelPartData.addChild(
                    "leftLeg",
                    ModelPartBuilder.create().uv(56, -1).mirrored()
                        .cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 6.0f, dilation).mirrored(false),
                    ModelTransform.pivot(5.0f, 11.0f, 0.0f)
                )
                return TexturedModelData.of(modelData, 128, 128)
            }
    }

    override fun getPart(): ModelPart {
        return root
    }
}