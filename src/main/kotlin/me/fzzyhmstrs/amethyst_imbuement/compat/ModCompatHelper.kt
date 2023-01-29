package me.fzzyhmstrs.amethyst_imbuement.compat

import dev.emi.emi.api.EmiApi
import me.fzzyhmstrs.amethyst_imbuement.compat.emi.EmiClientPlugin
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingRecipeBookScreen
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreen
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient

object ModCompatHelper {

    private val viewerHierarchy: Map<String, Int> = mapOf(
        "emi" to 1,
        "roughlyenoughitems" to 2,
        "jei" to 3
    )

    fun isValidHandlerOffset(offset: Int): Boolean{
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
                MinecraftClient.getInstance().setScreen(ImbuingRecipeBookScreen(oldScreen))
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