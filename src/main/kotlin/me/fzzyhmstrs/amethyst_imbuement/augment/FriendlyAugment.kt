package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment.FRIENDLY
import me.fzzyhmstrs.fzzy_core.trinket_util.TrinketUtil
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class FriendlyAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        if (!checkEnabled()) return false
        val stacks = TrinketUtil.getTrinketStacks(user)
        stacks.forEach {
            if (it.item is AbstractAugmentJewelryItem){
                if (EnchantmentHelper.getLevel(FRIENDLY, it) > 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is AbstractAugmentJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return Registries.ITEM.stream().filter { it is AbstractAugmentJewelryItem }.map { it.defaultStack }.toList()
    }

}