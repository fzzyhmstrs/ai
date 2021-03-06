package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.ItemModelRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentItemEntityRenderer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.ModelIdentifier

@Environment(value = EnvType.CLIENT)
object RegisterItemModel {

    fun registerAll(){
        val modelsPerMode = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID + ":glistering_trident#inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID + ":glistering_trident_in_hand#inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterItem.GLISTERING_TRIDENT, modelsPerMode)
        ItemModelRegistry.registerItemEntityModel(RegisterItem.GLISTERING_TRIDENT,
            GlisteringTridentItemEntityRenderer,
            RegisterRenderer.GLISTERING_TRIDENT,
            GlisteringTridentEntityModel::class.java)
    }
}