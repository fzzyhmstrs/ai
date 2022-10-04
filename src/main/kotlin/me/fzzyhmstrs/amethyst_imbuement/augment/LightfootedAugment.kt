package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

class LightfootedAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_FEET, *slot) {

}