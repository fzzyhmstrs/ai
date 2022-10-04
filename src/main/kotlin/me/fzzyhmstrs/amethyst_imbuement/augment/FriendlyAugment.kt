package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.TrinketUtil
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment.FRIENDLY
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class FriendlyAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        if (!enabled) return false
        val stacks = TrinketUtil.getTrinketStacks(user)
        stacks.forEach {
            if (it.item is ImbuedJewelryItem){
                if (EnchantmentHelper.getLevel(FRIENDLY, it) > 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ImbuedJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.IMBUED_HEADBAND,1))
        list.add(ItemStack(RegisterItem.IMBUED_AMULET,1))
        list.add(ItemStack(RegisterItem.IMBUED_RING,1))
        return list
    }

}