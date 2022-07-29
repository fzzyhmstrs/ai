package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.util.Identifier
import dev.emi.emi.api.recipe.*
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfLoreItem
import me.fzzyhmstrs.amethyst_imbuement.item.BookOfMythosItem
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.util.registry.Registry

class ImbuingEmiRecipe(recipe: ImbuingRecipe): EmiRecipe{
    
    private val id: Identifier
    private val inputs: List<EmiIngredient>
    private val outputs: List<EmiStack>
    private val isEnchantingType: Boolean

    init{
        id = recipe.id
        inputs = initInputs(recipe)
        outputs = initOutputs(recipe)
        isEnchantingType = recipe.getAugment() != ""
    
    }
    
    private fun initInputs(recipe: ImbuingRecipe): List<EmiIngredient>{
        val list: MutableList<EmiIngredient> = mutableListOf()
        val inputs = recipe.getInputs()
        for (i in inputs.indices){
            val input = inputs[i]
            if (recipe.getAugment() != "" && i == 6){
                val identifier = Identifier(recipe.getAugment())
                val enchant = Registry.ENCHANTMENT.get(identifier)
                val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
                if (enchant != null){
                    when (enchant) {
                        is BaseAugment -> {
                            val stacks = enchant.acceptableItemStacks().toTypedArray()
                            val ingredient = Ingredient.ofStacks(*stacks)
                            list.add(EmiIngredient.of(ingredient))
                        }
                        is ScepterAugment -> {
                            val stacks = enchant.acceptableItemStacks().toTypedArray()
                            val ingredient = Ingredient.ofStacks(*stacks)
                            list.add(EmiIngredient.of(ingredient))
                        }
                        else -> {
                            val enchantItemList: MutableList<ItemStack> = mutableListOf()
                            for (item in Registry.ITEM.iterator()){
                                val stack = ItemStack(item)
                                if (enchant.isAcceptableItem(stack)){
                                    enchantItemList.add(stack)
                                }
                            }
                            val ingredient = Ingredient.ofStacks(*enchantItemList.toTypedArray())
                            list.add(EmiIngredient.of(ingredient))
                        }
                    }
                } else if(modifier != null) {
                    val stacks = modifier.acceptableItemStacks().toTypedArray()
                    val ingredient = Ingredient.ofStacks(*stacks)
                    list.add(EmiIngredient.of(ingredient))
                } else {
                    list.add(EmiIngredient.of(Ingredient.EMPTY))
                }
                continue
            }
            list.add(EmiIngredient.of(input))
        }
        return list
    }

    private fun initOutputs(recipe: ImbuingRecipe): List<EmiStack>{
        val list: MutableList<EmiStack> = mutableListOf()
        if (recipe.getAugment() == "") {
            list.add(EmiStack.of(recipe.output))
        } else {
            val identifier = Identifier(recipe.getAugment())
            val enchant = Registry.ENCHANTMENT.get(identifier)
            val modifier = ModifierRegistry.getByType<AugmentModifier>(identifier)
            val stack: ItemStack
            if (enchant != null){
                stack = ItemStack(Items.ENCHANTED_BOOK,1)
                stack.addEnchantment(enchant,1)
                list.add(EmiStack.of(stack))
            } else if (modifier != null){
                stack = modifier.acceptableItemStacks().first()
                val moddedStack = stack.copy()
                ModifierHelper.addModifierForREI(modifier.modifierId, moddedStack)
                list.add(EmiStack.of(stack))
            } else {
                stack = ItemStack(Items.BOOK, 1)
                list.add(EmiStack.of(stack))
            }
        }
        return list
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.IMBUING_CATEGORY
    }
    
    override fun getId(): Identifier{
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return inputs
    }
    
    override fun getOutputs(): List<EmiStack>{
        return outputs
    }
    
    override fun supportsRecipeTree(): Boolean{
        return super.supportsRecipeTree() && !isEnchantingType
    }
    
    override fun getDisplayWidth(): Int{
        return 152
    }
    
    override fun getDisplayHeight(): Int{
        return 76
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        val xOffset = 0
        val yOffset = 0
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 88, 25)
        widgets.addSlot(inputs[0], xOffset, yOffset)
        widgets.addSlot(inputs[1], xOffset + 87, yOffset)
        widgets.addSlot(inputs[2], xOffset + 20, yOffset + 2)
        widgets.addSlot(inputs[3], xOffset + 43, yOffset + 2)
        widgets.addSlot(inputs[4], xOffset + 66, yOffset + 2)
        widgets.addSlot(inputs[5], xOffset + 20, yOffset + 25)
        widgets.addSlot(inputs[6], xOffset + 43, yOffset + 25)
        widgets.addSlot(inputs[7], xOffset + 66, yOffset + 25)
        widgets.addSlot(inputs[8], xOffset + 20, yOffset + 48)
        widgets.addSlot(inputs[9], xOffset + 43, yOffset + 48)
        widgets.addSlot(inputs[10], xOffset + 66, yOffset + 48)
        widgets.addSlot(inputs[11], xOffset, yOffset + 50)
        widgets.addSlot(inputs[12], xOffset + 87, yOffset + 50)
        widgets.addSlot(outputs[0], xOffset + 120, yOffset + 25).recipeContext(this)
    }

}
