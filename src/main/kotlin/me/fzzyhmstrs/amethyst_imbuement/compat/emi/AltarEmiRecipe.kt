package me.fzzyhmstrs.amethyst_imbuement.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipe
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class AltarEmiRecipe(recipe: AltarRecipe): EmiRecipe{
    
    private val id: Identifier
    private val dust: EmiIngredient
    private val base: EmiIngredient
    private val addition: EmiIngredient
    private val result: List<EmiStack>
    private val text = if(recipe.react) AcText.translatable(recipe.reactMessage).formatted(Formatting.LIGHT_PURPLE).asOrderedText() else AcText.empty().asOrderedText()

    init{
        id = recipe.id
        dust = EmiIngredient.of(recipe.dust)
        base = EmiIngredient.of(recipe.base)
		addition = EmiIngredient.of(recipe.addition)
	    result = if(!recipe.react) {
            if (!recipe.result.isOf(RegisterItem.SPELL_SCROLL)) {
                listOf(EmiStack.of(recipe.result))
            } else {
                generateScrollOutputs(recipe.addition)
            }
        } else {
            base.emiStacks
        }
    }

    private fun generateScrollOutputs(addition: Ingredient): List<EmiStack>{
        val list = mutableListOf(EmiStack.of(ItemStack(RegisterItem.SPELL_SCROLL)))
        if (addition.test(RegisterItem.BOOK_OF_MYTHOS.defaultStack)){
            Registries.ENCHANTMENT.stream()
                .filter { enchant -> enchant is ScepterAugment && enchant.getTier() == 3 }
                .map {enchant -> EmiStack.of(SpellScrollItem.createSpellScroll(enchant as ScepterAugment))}
                .forEach { es -> list.add(es) }
        } else {
            Registries.ENCHANTMENT.stream()
                .filter { enchant -> enchant is ScepterAugment && enchant.getTier() < 3 }
                .map {enchant -> EmiStack.of(SpellScrollItem.createSpellScroll(enchant as ScepterAugment))}
                .forEach { es -> list.add(es) }
        }
        return list
    }
    
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
    }
    
    override fun getId(): Identifier{
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return listOf(dust,base,addition)
    }
    
    override fun getOutputs(): List<EmiStack>{
        return result
    }
    
    override fun getDisplayWidth(): Int{
        return 112
    }
    
    override fun getDisplayHeight(): Int{
        return 28
    }
    
    override fun addWidgets(widgets: WidgetHolder){
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 0)
		widgets.add(ReagentAlertSlot(dust,base, 0, 0))
		widgets.addSlot(base, 18, 0)
		widgets.add(ReagentAlertSlot(addition,base, 36, 0))
		widgets.addSlot(EmiIngredient.of(result), 94, 0).recipeContext(this)
		widgets.addText(text,1,19,0x404040,true)
    }

}
