package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class SwiftnessAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR_LEGS, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        addStatusToQueue(user.uuid,StatusEffects.SPEED,260,level - 1)
        //user.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, 200, level-1))
    }

}