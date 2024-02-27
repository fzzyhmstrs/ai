package me.fzzyhmstrs.amethyst_imbuement.compat

import dev.emi.emi.api.EmiApi
import me.fzzyhmstrs.amethyst_imbuement.compat.emi.EmiClientPlugin
import me.fzzyhmstrs.amethyst_imbuement.compat.mcde.McdeCompat
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object ModCompatHelper {

    private val patchouliLoaded by lazy {
        FabricLoader.getInstance().isModLoaded("patchouli")
    }

    private val mcdeLoaded by lazy {
        FabricLoader.getInstance().isModLoaded("mcde")
    }

    private val viewerHierarchy: Map<String, Int> = mapOf(
        "emi" to 1,
        "roughlyenoughitems" to 2,
        "jei" to 3
    )

    fun isPatchouliLoaded(): Boolean{
        return patchouliLoaded
    }

    fun openBookEntry(entry: Identifier, page: Int = 0){
        if (patchouliLoaded)
            PatchouliCompat.openBookEntry(entry, page)
    }

    fun openBookGui(playerEntity: ServerPlayerEntity, identifier: Identifier){
        if (patchouliLoaded)
            PatchouliCompat.openBookGui(playerEntity, identifier)
    }

    fun registerPage(){
        if (patchouliLoaded)
            PatchouliCompat.registerPage()
    }

    fun ignoreEnchantment(enchantment: Enchantment, stack: ItemStack): Boolean{
        var ignore = AiConfig.blocks.disenchanter.ignoreEnchantment(enchantment)
        ignore = ignore || FzzyPort.ENCHANTMENT.isInTag(enchantment, RegisterTag.DISENCHANTING_BLACKLIST)
        if (mcdeLoaded)
            ignore = ignore || McdeCompat.ignoreEnchantment(enchantment, stack)
        return ignore
    }

    fun isValidHandlerOffset(offset: Int): Boolean {
        return viewerHierarchy.values.contains(offset)
    }

    fun getScreenHandlerOffset(): Int{
        for (chk in viewerHierarchy){
            if(FabricLoader.getInstance().isModLoaded(chk.key)){
                return chk.value
            }
        }
        return 0
    }

    fun runHandlerViewer(offset: Int){
        if (offset == 0){
            val oldScreen = MinecraftClient.getInstance().currentScreen
            if (oldScreen is ImbuingTableScreen){
                MinecraftClient.getInstance().player?.sendMessage(AcText.literal("Recipe Book currently down for maintenance"))
                //MinecraftClient.getInstance().setScreen(ImbuingRecipeBookScreen(oldScreen))
            }
        }
        if (offset == 1){
            EmiApi.displayRecipeCategory(EmiClientPlugin.IMBUING_CATEGORY)
        }
    }

    fun isViewerSuperseded(viewer: String): Boolean{
        val ranking = viewerHierarchy[viewer]?:return true
        for (chk in viewerHierarchy){
            if (chk.key != viewer && chk.value < ranking && FabricLoader.getInstance().isModLoaded(chk.key)) return true
        }
        return false
    }

}