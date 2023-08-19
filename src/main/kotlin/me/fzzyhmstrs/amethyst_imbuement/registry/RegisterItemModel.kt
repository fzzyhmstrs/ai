package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.renderer.GlisteringTridentItemEntityRenderer
import me.fzzyhmstrs.fzzy_core.registry.ItemModelRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.ModelIdentifier

@Environment(value = EnvType.CLIENT)
object RegisterItemModel {

    fun registerAll(){
        val blazingScepterModels = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID, "blazing_scepter","inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID, "blazing_scepter_in_hand","inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterScepter.BLAZING_SCEPTER, blazingScepterModels)
        val modelsPerMode = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID, "glistering_trident","inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID, "glistering_trident_in_hand","inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterTool.GLISTERING_TRIDENT, modelsPerMode)
        ItemModelRegistry.registerItemEntityModel(RegisterTool.GLISTERING_TRIDENT,
            GlisteringTridentItemEntityRenderer,
            RegisterRenderer.GLISTERING_TRIDENT,
            GlisteringTridentEntityModel::class.java)
    }
}