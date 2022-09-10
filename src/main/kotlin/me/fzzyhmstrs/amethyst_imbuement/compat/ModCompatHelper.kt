package me.fzzyhmstrs.amethyst_imbuement.compat

import dev.emi.emi.api.EmiApi
import me.fzzyhmstrs.amethyst_imbuement.compat.emi.EmiClientPlugin
import net.fabricmc.loader.api.FabricLoader

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
        return -1
    }

    fun runHandlerViewer(offset: Int){
        if (offset == 1){
            EmiApi.displayRecipeCategory(EmiClientPlugin.IMBUING_CATEGORY)
        }
    }

    fun isViewerSuperseded(viewer: String): Boolean{
        val ranking = viewerHierarchy[viewer]?:return true
        for (chk in viewerHierarchy){
            if (chk.key != viewer && chk.value < ranking) return true
        }
        return false
    }

}