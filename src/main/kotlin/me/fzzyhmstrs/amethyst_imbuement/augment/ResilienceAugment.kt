package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import java.util.*

class ResilienceAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot) {

    val uuids: EnumMap<EquipmentSlot,UUID> = EnumMap(mapOf(
        EquipmentSlot.HEAD to UUID.fromString("797c99c2-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.CHEST to UUID.fromString("797c9c7e-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.LEGS to UUID.fromString("797c9e54-ba09-11ed-afa1-0242ac120002"),
        EquipmentSlot.FEET to UUID.fromString("797c9fd0-ba09-11ed-afa1-0242ac120002")
    ))

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem
    }
}