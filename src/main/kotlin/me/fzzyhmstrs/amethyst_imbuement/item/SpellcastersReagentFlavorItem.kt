package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

class SpellcastersReagentItem(private val attribute: EntityAttributeModifier,settings: Settings): CustomFlavorItem(settings),SpellcastersReagent {

    override fun getAttributeModifier(): EntityAttributeModifier{
        return attribute
    }

}
