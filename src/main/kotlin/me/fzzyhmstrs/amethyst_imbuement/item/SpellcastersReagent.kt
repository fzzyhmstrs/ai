package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier

interface SpellcastersReagent: Reagent{
    fun getAttributeModifier(): Pair<EntityAttribute,EntityAttributeModifier>
}
