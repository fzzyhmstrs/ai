package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.TridentEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class GlisteringTridentEntityModel(private val root: ModelPart) : TridentEntityModel(root) {


    companion object {
        var TEXTURE: Identifier = Identifier(AI.MOD_ID,"textures/entity/glistering_trident.png")

        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            val modelPartData2 = modelPartData.addChild(
                "pole",
                ModelPartBuilder.create().uv(0, 6).cuboid(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f),
                ModelTransform.NONE
            )
            modelPartData2.addChild(
                "base",
                ModelPartBuilder.create().uv(4, 0).cuboid(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f),
                ModelTransform.NONE
            )
            modelPartData2.addChild(
                "left_spike",
                ModelPartBuilder.create().uv(4, 3).cuboid(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                ModelTransform.NONE
            )
            modelPartData2.addChild(
                "middle_spike",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                ModelTransform.NONE
            )
            modelPartData2.addChild(
                "right_spike",
                ModelPartBuilder.create().uv(4, 3).mirrored().cuboid(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                ModelTransform.NONE
            )
            return TexturedModelData.of(modelData, 32, 32)
        }
    }

    override fun render(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha)
    }


}