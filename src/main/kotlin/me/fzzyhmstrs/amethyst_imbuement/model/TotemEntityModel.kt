package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.totem.AbstractEffectTotemEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack


class TotemEntityModel(_modelPart: ModelPart) : EntityModel<AbstractEffectTotemEntity>() {

    private val modelPart: ModelPart = _modelPart
    private val parts: List<ModelPart> = listOf(
        modelPart.getChild("base_1"),
        modelPart.getChild("base_2"),
        modelPart.getChild("base_3"),
        modelPart.getChild("base_4"),
        modelPart.getChild("pillar"),
        modelPart.getChild("post_1"),
        modelPart.getChild("post_2"),
        modelPart.getChild("post_3"),
        modelPart.getChild("post_4")
    )

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
        parts.forEach { part ->
            part.render(
                matrices,
                vertices,
                light,
                overlay,
                red,
                green,
                blue,
                alpha
            )
        }
    }

    override fun setAngles(
        entity: AbstractEffectTotemEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        return
    }

    companion object {
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                "base_1",
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(-7f, 23f, -7f, 14f, 1f, 14f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "base_2",
                ModelPartBuilder.create()
                    .uv(0, 15)
                    .cuboid(-6f, 22f, -6f, 12f, 1f, 12f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "base_3",
                ModelPartBuilder.create()
                    .uv(0, 28)
                    .cuboid(-5f, 20f, -5f, 10f, 2f, 10f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "base_4",
                ModelPartBuilder.create()
                    .uv(0, 40)
                    .cuboid(-4f, 18f, -4f, 8f, 2f, 8f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "pillar",
                ModelPartBuilder.create()
                    .uv(0, 50)
                    .cuboid(-3f, 10f, -3f, 6f, 8f, 6f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "post_1",
                ModelPartBuilder.create()
                    .uv(56,0)
                    .cuboid(3f, 6f, 3f, 1f, 6f, 1f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "post_2",
                ModelPartBuilder.create()
                    .uv(56,0)
                    .cuboid(3f, 6f, -4f, 1f, 6f, 1f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "post_3",
                ModelPartBuilder.create()
                    .uv(56,0)
                    .cuboid(-4f, 6f, 3f, 1f, 6f, 1f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            modelPartData.addChild(
                "post_4",
                ModelPartBuilder.create()
                    .uv(56,0)
                    .cuboid(-4f, 6f, -4f, 1f, 6f, 1f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            return TexturedModelData.of(modelData,64,64)
        }
    }
}