package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import java.util.*

class HealthyAugment(weight: Rarity, mxLvl: Int, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {

    /*override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (user.hasStatusEffect(StatusEffects.HEALTH_BOOST)) return
        addStatusToQueue(user.uuid, StatusEffects.HEALTH_BOOST, 500000000, 0)
    }

    override fun unequipEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (user.hasStatusEffect(StatusEffects.HEALTH_BOOST)) {
            user.removeStatusEffect(StatusEffects.HEALTH_BOOST)
        }
    }*/

    override fun attributeModifier(stack: ItemStack, uuid: UUID): Pair<EntityAttribute, EntityAttributeModifier>? {
        return Pair(
            EntityAttributes.GENERIC_MAX_HEALTH,
            EntityAttributeModifier(uuid, "amethyst_imbuement:max_health", 4.0, EntityAttributeModifier.Operation.ADDITION))
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.isOf(RegisterItem.IMBUED_AMULET))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.IMBUED_AMULET,1))
        return list
    }
}