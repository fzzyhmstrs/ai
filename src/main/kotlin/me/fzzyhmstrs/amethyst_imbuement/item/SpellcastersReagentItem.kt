package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttribut

class SpellcastersReagentItem(
    private val attribute: EntityAttribute,
    private val modifier: EntityAttributeModifier,
    settings: Settings)
    : 
    Item(settings),SpellcastersReagent 
{

    override fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>{
        return Pair(attribute,modifier)
    }

}
