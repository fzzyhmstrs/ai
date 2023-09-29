package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.item.interfaces.SpellcastersReagent
import net.minecraft.block.Block
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.BlockItem
import java.util.*

class GlisteningIceBlockItem(block: Block,settings: Settings): BlockItem(block, settings), SpellcastersReagent {
    override fun getAttributeModifier(): Pair<EntityAttribute, EntityAttributeModifier> {
        return Pair(RegisterAttribute.SPELL_COOLDOWN,EntityAttributeModifier(UUID.fromString("102d4ad8-c5e5-11ed-afa1-0242ac120002"),"glistening_modifier",-2.5,EntityAttributeModifier.Operation.ADDITION))
    }
}