package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class ImmunityAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        EffectQueue.addStatusToQueue(user,RegisterStatus.IMMUNITY,260,0)
    }

}