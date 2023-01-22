package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class LeapingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_FEET, *slot) {

    override fun equipmentEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        if (!enabled) return
        EffectQueue.addStatusToQueue(user,StatusEffects.JUMP_BOOST,260,level - 1)
    }

}