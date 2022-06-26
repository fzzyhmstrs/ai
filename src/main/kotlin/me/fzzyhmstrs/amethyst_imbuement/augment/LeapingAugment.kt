package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class LeapingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractEquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_FEET, *slot) {

    override fun equipmentEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        EffectQueue.addStatusToQueue(user,StatusEffects.JUMP_BOOST,260,level - 1)
    }

}