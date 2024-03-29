package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import java.util.*

class ShieldingAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is AbstractAugmentJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return RegisterItem.jewelryStacks
    }

    override fun attributeModifier(stack: ItemStack,level: Int, uuid: UUID): Pair<EntityAttribute, EntityAttributeModifier> {
        return Pair(RegisterAttribute.SHIELDING,
            EntityAttributeModifier(uuid,"shielding_augment_mod",0.02 * level,EntityAttributeModifier.Operation.ADDITION)
        )
    }
}
