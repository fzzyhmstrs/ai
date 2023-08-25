package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

open class TotemPassiveAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl,*slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isIn(RegisterTag.TOTEMS_TAG)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return Registries.ITEM.iterateEntries(RegisterTag.TOTEMS_TAG).map { it.value().defaultStack }.toMutableList()
    }
}