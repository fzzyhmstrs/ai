package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.render.entity.model.SinglePartEntityModel
import net.minecraft.util.math.MathHelper

class CrystallineGolemEntityModel(private val root: ModelPart): SinglePartEntityModel<CrystallineGolemEntity>() {

    private val head: ModelPart = root.getChild(EntityModelPartNames.HEAD)
    private val rightArm: ModelPart = root.getChild(EntityModelPartNames.RIGHT_ARM)
    private val leftArm: ModelPart = root.getChild(EntityModelPartNames.LEFT_ARM)
    private val rightLeg: ModelPart = root.getChild(EntityModelPartNames.RIGHT_LEG)
    private val leftLeg: ModelPart = root.getChild(EntityModelPartNames.LEFT_LEG)

    companion object {
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f).uv(24, 0)
                    .cuboid(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f),
                ModelTransform.pivot(0.0f, -7.0f, -2.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 40).cuboid(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f).uv(0, 70)
                    .cuboid(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, Dilation(0.5f)),
                ModelTransform.pivot(0.0f, -7.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.RIGHT_ARM,
                ModelPartBuilder.create().uv(60, 21).cuboid(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f),
                ModelTransform.pivot(0.0f, -7.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.LEFT_ARM,
                ModelPartBuilder.create().uv(60, 58).cuboid(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f),
                ModelTransform.pivot(0.0f, -7.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(37, 0).cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f),
                ModelTransform.pivot(-4.0f, 11.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(60, 0).mirrored().cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f),
                ModelTransform.pivot(5.0f, 11.0f, 0.0f)
            )
            return TexturedModelData.of(modelData, 128, 128)
        }
    }

    override fun getPart(): ModelPart {
        return this.root
    }

    override fun setAngles(crystalGolemEntity: CrystallineGolemEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        head.yaw = i * (Math.PI.toFloat() / 180)
        head.pitch = j * (Math.PI.toFloat() / 180)
        rightLeg.pitch = -1.5f * MathHelper.wrap(f, 13.0f) * g
        leftLeg.pitch = 1.5f * MathHelper.wrap(f, 13.0f) * g
        rightLeg.yaw = 0.0f
        leftLeg.yaw = 0.0f
    }

    override fun animateModel(crystalGolemEntity: CrystallineGolemEntity, f: Float, g: Float, h: Float) {
        val i = crystalGolemEntity.getAttackTicks()
        if (i > 0) {
            rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
            leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(i.toFloat() - h, 10.0f)
        } else {
            val j = crystalGolemEntity.getLookingAtVillagerTicks()
            if (j > 0) {
                rightArm.pitch = -0.8f + 0.025f * MathHelper.wrap(j.toFloat(), 70.0f)
                leftArm.pitch = 0.0f
            } else {
                rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(f, 13.0f)) * g
                leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(f, 13.0f)) * g
            }
        }
    }
}