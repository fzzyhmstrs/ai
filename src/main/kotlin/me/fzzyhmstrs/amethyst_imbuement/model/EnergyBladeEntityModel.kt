package me.fzzyhmstrs.amethyst_imbuement.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.util.function.Function

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
@Environment(value = EnvType.CLIENT)
class EnergyBladeEntityModel(root: ModelPart) : Model(Function { texture: Identifier ->
    RenderLayer.getEntitySolid(texture) }) {
    private val bb_main: ModelPart = root.getChild("bb_main")

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
        bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha)
    }

    companion object {
        val texturedModelData: TexturedModelData
            get() {
                val modelData = ModelData()
                val modelPartData = modelData.root
                val bb_main = modelPartData.addChild(
                    "bb_main",
                    ModelPartBuilder.create().uv(0, 0).cuboid(-7.0f, -1.0f, -6.0f, 14.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(38, 1).cuboid(7.0f, -1.0f, -5.0f, 4.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(38, 1).cuboid(-11.0f, -1.0f, -5.0f, 4.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(11.0f, -1.0f, -4.0f, 3.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(-14.0f, -1.0f, -4.0f, 3.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(14.0f, -1.0f, -3.0f, 2.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(-16.0f, -1.0f, -3.0f, 2.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(-17.0f, -1.0f, -1.0f, 1.0f, 1.0f, 9.0f, Dilation(0.0f))
                        .uv(40, 1).cuboid(16.0f, -1.0f, -1.0f, 1.0f, 1.0f, 9.0f, Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 24.0f, 0.0f)
                )
                return TexturedModelData.of(modelData, 64, 16)
            }
    }
}