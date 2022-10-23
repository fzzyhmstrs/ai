package me.fzzyhmstrs.amethyst_imbuement.compat

import dev.emi.emi.api.EmiApi
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.compat.emi.EmiClientPlugin
import me.fzzyhmstrs.amethyst_imbuement.screen.KnowledgeBookScreen
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

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
            if (chk.key != viewer && chk.value < ranking && FabricLoader.getInstance().isModLoaded(chk.key)) return true
        }
        return false
    }

    fun centerSlotGenerator(recipe: ImbuingRecipe): Ingredient{
        val identifier = Identifier(recipe.getAugment())
        val enchant = Registry.ENCHANTMENT.get(identifier)
        val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
        if (enchant != null){
            when (enchant) {
                is BaseAugment -> {
                    val stacks = enchant.acceptableItemStacks().toTypedArray()
                    return Ingredient.ofStacks(*stacks)
                }
                is ScepterAugment -> {
                    val stacks = enchant.acceptableItemStacks().toTypedArray()
                    for (chk in stacks.indices){
                        val item = stacks[chk].item
                        if (item is AbstractAugmentBookItem) {
                            val augBookStack = stacks[chk].copy()
                            AbstractAugmentBookItem.addLoreKeyForREI(augBookStack, recipe.getAugment())
                            stacks[chk] = augBookStack
                        }
                    }
                    return Ingredient.ofStacks(*stacks)
                }
                else -> {
                    val enchantItemList: MutableList<ItemStack> = mutableListOf()
                    for (item in Registry.ITEM.iterator()){
                        val stack = ItemStack(item)
                        if (enchant.isAcceptableItem(stack)){
                            enchantItemList.add(stack)
                        }
                    }
                    return Ingredient.ofStacks(*enchantItemList.toTypedArray())
                }
            }
        } else if(modifier != null) {
            val stacks = modifier.acceptableItemStacks().toTypedArray()
            return Ingredient.ofStacks(*stacks)
        } else {
            return Ingredient.EMPTY
        }
    }

}