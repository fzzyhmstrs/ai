package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

open class TotemPassiveAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl,*slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isOf(RegisterTool.TOTEM_OF_AMETHYST)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterTool.TOTEM_OF_AMETHYST,1))
        return list
    }
}