package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class SpellcastersReagentFlavorItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    settings: Settings)
    : 
    CustomFlavorItem(settings), SpellcastersReagent
{


    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

    }

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }

}
