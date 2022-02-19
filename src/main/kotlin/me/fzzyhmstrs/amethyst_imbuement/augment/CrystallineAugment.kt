package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

class CrystallineAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): BaseAugment(weight, mxLvl,EnchantmentTarget.ARMOR, *slot) {

    override fun getAttackDamage(level: Int, group: EntityGroup?): Float {
        return 0.25F * level
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || (stack.item is AxeItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.entries
        list.addAll(super.acceptableItemStacks().asIterable())
        list.addAll(super.acceptableItemStacks().asIterable())
        list.addAll(super.acceptableItemStacks().asIterable())
        list.addAll(super.acceptableItemStacks().asIterable())
        for (entry in entries){
            val item = entry.value
            if (item is AxeItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }
}