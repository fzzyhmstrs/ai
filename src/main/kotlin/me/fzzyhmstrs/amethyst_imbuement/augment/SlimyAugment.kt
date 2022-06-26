package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot

class SlimyAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR_FEET, *slot) {

}