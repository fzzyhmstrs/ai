package me.fzzyhmstrs.amethyst_imbuement.item.jewelry

import me.fzzyhmstrs.amethyst_imbuement.item.interfaces.SpellcastersReagent
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.FlavorHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.world.World

class SteelWardItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    settings: Settings)
    : 
    ShieldItem(settings), SpellcastersReagent, Modifiable
{

    private val flavorText: MutableText by lazy{
        FlavorHelper.makeFlavorText(this)
    }

    private val flavorTextDesc: MutableText by lazy{
        FlavorHelper.makeFlavorTextDesc(this)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        FlavorHelper.addFlavorText(tooltip, context, flavorText, flavorTextDesc)
    }

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }
}
