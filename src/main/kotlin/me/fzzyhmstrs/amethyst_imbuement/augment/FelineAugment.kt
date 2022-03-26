package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketsApi
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment.FELINE
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class FelineAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        val comp = TrinketsApi.getTrinketComponent(user)
        if (comp.isPresent) {
            val items = comp.get().allEquipped
            for (slot in items) {
                if (slot.right.item is ImbuedJewelryItem) {
                    if (EnchantmentHelper.getLevel(FELINE, slot.right) > 0) {
                        return true
                    }
                }
            }
        }
        var stack2: ItemStack = user.offHandStack
        if (EnchantmentHelper.getLevel(FELINE, stack2) > 0) return true
        stack2 = user.mainHandStack
        return EnchantmentHelper.getLevel(FELINE, stack2) > 0
    }

}