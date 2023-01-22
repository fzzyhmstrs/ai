package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractActiveAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry


open class ActiveAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractActiveAugment(weight,mxLvl, *slot) {

    override fun canAccept(other: Enchantment): Boolean {
        return (other !is ActiveAugment) || ((Registry.ENCHANTMENT.getId(other) == Registry.ENCHANTMENT.getId(this) && this.maxLevel > 1))
    }

    override fun checkEnabled(): Boolean{
        val id = Registry.ENCHANTMENT.getId(this)?:return true
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.path,true)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.isOf(RegisterItem.TOTEM_OF_AMETHYST))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        list.add(ItemStack(RegisterItem.TOTEM_OF_AMETHYST,1))
        return list
    }

}