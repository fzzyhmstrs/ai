package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import java.util.*

class HealthyAugment(weight: Rarity, mxLvl: Int, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {

    override fun attributeModifier(stack: ItemStack, uuid: UUID): Pair<EntityAttribute, EntityAttributeModifier> {
        return Pair(
            EntityAttributes.GENERIC_MAX_HEALTH,
            EntityAttributeModifier(uuid, "amethyst_imbuement:max_health", 4.0, EntityAttributeModifier.Operation.ADDITION))
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.isOf(RegisterTool.IMBUED_AMULET))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterTool.IMBUED_AMULET,1))
        return list
    }
}