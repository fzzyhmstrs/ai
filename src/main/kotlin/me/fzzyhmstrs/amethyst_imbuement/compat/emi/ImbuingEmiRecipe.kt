package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipe
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.text.OrderedText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class ImbuingEmiRecipe(recipe: ImbuingRecipe): EmiRecipe{
    
    private val id: Identifier
    private val inputs: List<EmiIngredient>
    private val outputs: List<EmiStack>
    private val costText: OrderedText
    private val costOffset: Int
    private val isEnchantingType: Boolean

    init{
        id = recipe.id
        inputs = initInputs(recipe)
        outputs = initOutputs(recipe)
        val cost = recipe.getCost()
        if(cost > 99){
            costText = TranslatableText("display.imbuing.cost.big",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 116 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        } else{
            costText = TranslatableText("display.imbuing.cost.small",cost).formatted(Formatting.GREEN).asOrderedText()
            costOffset = 119 - MinecraftClient.getInstance().textRenderer.getWidth(costText) / 2
        }
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
                            for (chk in stacks.indices){
                                val item = stacks[chk].item
                                if (item is AbstractAugmentBookItem) {
                                    val augBookStack = stacks[chk].copy()
                                    AbstractAugmentBookItem.addLoreKeyForREI(augBookStack, recipe.getAugment())
                                    stacks[chk] = augBookStack
                                }
                            }
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
                list.add(EmiStack.of(moddedStack))
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
        return 134
    }
    
    override fun getDisplayHeight(): Int{
        return 60
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        val xOffset = 0
        val yOffset = 0
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 82, 21)
        widgets.addSlot(inputs[0], xOffset, yOffset)
        widgets.addSlot(inputs[1], xOffset + 81, yOffset)
        widgets.addSlot(inputs[2], xOffset + 20, yOffset + 2)
        widgets.addSlot(inputs[3], xOffset + 40, yOffset + 2)
        widgets.addSlot(inputs[4], xOffset + 60, yOffset + 2)
        widgets.addSlot(inputs[5], xOffset + 20, yOffset + 21)
        widgets.addSlot(inputs[6], xOffset + 40, yOffset + 21)
        widgets.addSlot(inputs[7], xOffset + 60, yOffset + 21)
        widgets.addSlot(inputs[8], xOffset + 20, yOffset + 40)
        widgets.addSlot(inputs[9], xOffset + 40, yOffset + 40)
        widgets.addSlot(inputs[10], xOffset + 60, yOffset + 40)
        widgets.addSlot(inputs[11], xOffset, yOffset + 42)
        widgets.addSlot(inputs[12], xOffset + 81, yOffset + 42)
        widgets.addSlot(outputs[0], xOffset + 111, yOffset + 21).recipeContext(this)
        widgets.addText(costText,costOffset,42,0x55FF55,true)
    }

}
