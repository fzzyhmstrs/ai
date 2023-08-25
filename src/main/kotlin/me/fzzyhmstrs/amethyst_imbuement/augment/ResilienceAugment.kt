package me.fzzyhmstrs.amethyst_imbuement.augment

import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.AttributeProviding
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import java.util.*

class ResilienceAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot):
    EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot),
    AttributeProviding

{

    private val uuids: EnumMap<EquipmentSlot,UUID> = EnumMap(mapOf(
        EquipmentSlot.HEAD to UUID.fromString("797c99c2-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.CHEST to UUID.fromString("797c9c7e-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.LEGS to UUID.fromString("797c9e54-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.FEET to UUID.fromString("797c9fd0-ba09-11ed-afa1-0242ac120002")
    ))

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem
    }

    override fun modifyAttributeMap(map: Multimap<EntityAttribute, EntityAttributeModifier>,slot: EquipmentSlot, level: Int) {
        val uuid = uuids[slot] ?: return
        val name = "resilience_modifier_" + slot.getName()
        map.put(
            EntityAttributes.GENERIC_ARMOR,
            EntityAttributeModifier(uuid, name, 0.0 + level, EntityAttributeModifier.Operation.ADDITION)
        )
        map.put(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
            EntityAttributeModifier(uuid, name, (0.0 + level) / 2.0, EntityAttributeModifier.Operation.ADDITION)
        )
    }
}