package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.renderer.GildedLockboxItemEntityRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.GlisteringTridentItemEntityRenderer
import me.fzzyhmstrs.fzzy_core.registry.ItemModelRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.util.ModelIdentifier

@Environment(value = EnvType.CLIENT)
object RegisterItemModel {

    fun registerAll(){
        val blazingScepterModelModes = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID, "blazing_scepter","inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID, "blazing_scepter_in_hand","inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterScepter.BLAZING_SCEPTER, blazingScepterModelModes)
        val glisteringTridentModelModes = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID, "glistering_trident","inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID, "glistering_trident_in_hand","inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterTool.GLISTERING_TRIDENT, glisteringTridentModelModes)
        ItemModelRegistry.registerItemEntityModel(RegisterTool.GLISTERING_TRIDENT,
            GlisteringTridentItemEntityRenderer,
            RegisterRenderer.GLISTERING_TRIDENT,
            GlisteringTridentEntityModel::class.java)
        val gildedLockBoxModelModes = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID, "blazing_scepter","inventory"))
            .withAll(ModelIdentifier(AI.MOD_ID, "gilded_lockbox","inventory"),true)
        ItemModelRegistry.registerItemModelId(RegisterBlock.GILDED_LOCKBOX.asItem(), gildedLockBoxModelModes)
        BuiltinItemRendererRegistry.INSTANCE.register(RegisterBlock.GILDED_LOCKBOX.asItem(), GildedLockboxItemEntityRenderer)
    }
}