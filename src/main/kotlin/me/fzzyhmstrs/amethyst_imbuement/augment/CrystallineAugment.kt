package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class CrystallineAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl, EnchantmentTarget.WEAPON, *slot) {

    override fun getAttackDamage(level: Int, group: EntityGroup?): Float {
        if (!checkEnabled()) return 0f
        return 0.25F * level
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (EnchantmentTarget.CROSSBOW.isAcceptableItem(stack.item) ||
                EnchantmentTarget.TRIDENT.isAcceptableItem(stack.item) ||
                EnchantmentTarget.BOW.isAcceptableItem(stack.item) ||
                (stack.item is AxeItem) ||
                EnchantmentTarget.WEAPON.isAcceptableItem(stack.item))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.addAll(super.acceptableItemStacks().asIterable())
        for (item in Registries.ITEM){
            if ((EnchantmentTarget.CROSSBOW.isAcceptableItem(item) ||
                        EnchantmentTarget.TRIDENT.isAcceptableItem(item) ||
                        EnchantmentTarget.BOW.isAcceptableItem(item) ||
                        (item is AxeItem) ||
                        EnchantmentTarget.WEAPON.isAcceptableItem(item))){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }
}