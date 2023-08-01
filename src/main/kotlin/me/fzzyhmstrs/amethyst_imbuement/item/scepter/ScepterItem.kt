package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.item.DefaultScepterItem
import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


open class ScepterItem(
    material: ScepterToolMaterial,
    settings: Settings)
    :
    DefaultScepterItem(material, settings),
    Reactant,
    Reagent
{
    override val fallbackId: Identifier = AI.identity( "magic_missile")

    override fun getItemBarColor(stack: ItemStack): Int {
        return AiConfig.items.manaItems.getItemBarColor(stack)
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        if (type != AltarRecipe.Type) return true
        for (reagent in reagents){
            if (reagent.item is SpellScrollItem){
                if (reagent.nbt?.contains(RegisterItem.SPELL_SCROLL.SPELL) != true) return false
                if ((reagent.nbt?.contains(RegisterItem.SPELL_SCROLL.DISENCHANTED) != true)) return false
                var count = 0
                for (reagent1 in reagents){
                    if (reagent1.isOf(RegisterItem.KNOWLEDGE_POWDER)){
                        count++
                    }
                }
                return count > 0
            }
        }
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        for (reagent in reagents){
            if (reagent.item is SpellScrollItem){
                var count = 0
                for (reagent1 in reagents){
                    if (reagent1.isOf(RegisterItem.KNOWLEDGE_POWDER)){
                        count++
                    }
                }
                if (count == 0) return
                val nbt = reagent.nbt?:return
                val spellString = nbt.getString(RegisterItem.SPELL_SCROLL.SPELL)
                val spell = Registries.ENCHANTMENT.get(Identifier(spellString))?:return
                stack.addEnchantment(spell,1)
                return
            }
        }
    }
}
