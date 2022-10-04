package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack

class ResilienceAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is ArmorItem
    }
}