package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class LuckyAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        addStatusToQueue(user,StatusEffects.LUCK,260,0)
    }

}