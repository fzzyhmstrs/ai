package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack

class ImbuingRecipeBookScreen: Screen(AcText.translatable("")) {


    companion object RecipeContainer{

        var recipeMap: Map<ItemStack,List<ImbuingRecipe>> = mutableMapOf()

        fun registerClient(){
            ClientPlayConnectionEvents.JOIN.register {_,_,client ->
                val manager = client?.world?.recipeManager?:return@register
                val tempMap: MutableMap<ItemStack,MutableList<ImbuingRecipe>> = mutableMapOf()
                val imbuingList = manager.listAllOfType(ImbuingRecipe.Type)
                imbuingList.forEach {
                    val output = ModCompatHelper.outputGenerator(it)
                    if (!tempMap.containsKey(output)){
                        tempMap[output] = mutableListOf(it)
                    } else {
                        tempMap[output]?.add(it)
                    }
                }
                recipeMap = tempMap
                println(recipeMap)
            }
        }


    }
}