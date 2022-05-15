package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class SpectralVisionAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        addStatusToQueue(user, RegisterStatus.SPECTRAL_VISION,260,0)

    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.isOf(RegisterItem.IMBUED_HEADBAND))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.IMBUED_HEADBAND,1))
        return list
    }
}