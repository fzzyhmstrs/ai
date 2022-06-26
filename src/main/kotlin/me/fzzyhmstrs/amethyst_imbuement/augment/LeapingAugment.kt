package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class LeapingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR_FEET, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        addStatusToQueue(user,StatusEffects.JUMP_BOOST,260,level - 1)
    }

}