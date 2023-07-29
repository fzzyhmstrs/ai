package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.horse.DraftHorseEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.render.entity.model.HorseEntityModel

class DraftHorseEntityModel(modelPart: ModelPart): HorseEntityModel<DraftHorseEntity>(modelPart) {

    fun getTexturedModelData(dilation: Dilation): TexturedModelData {
        val modelData = getModelData(dilation)
        val modelPartData = modelData.root
        val modelPartData2 = modelPartData.getChild(EntityModelPartNames.BODY)
        val modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f)
        modelPartData2.addChild(
            EntityModelPartNames.LEFT_CHEST,
            modelPartBuilder,
            ModelTransform.of(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f)
        )
        modelPartData2.addChild(
            EntityModelPartNames.RIGHT_CHEST,
            modelPartBuilder,
            ModelTransform.of(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f)
        )
        return TexturedModelData.of(modelData, 64, 64)
    }


}