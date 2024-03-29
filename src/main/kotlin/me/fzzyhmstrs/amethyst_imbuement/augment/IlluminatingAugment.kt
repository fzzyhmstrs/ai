package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.item.weapon.SniperBowItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

class IlluminatingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is SniperBowItem
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return RegisterItem.sniperBowStacks
    }
}