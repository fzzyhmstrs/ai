package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.block.Block
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.BlockItem

class SpellcastersReagentBlockItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    block: Block,
    settings: Settings)
    : 
    BlockItem(block,settings), SpellcastersReagent
{

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }

}
