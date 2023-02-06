package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractPassiveAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

open class PassiveAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractPassiveAugment(weight,mxLvl, *slot) {

    override fun canAccept(other: Enchantment): Boolean {
        return (other !is PassiveAugment || (Registries.ENCHANTMENT.getId(other) == Registries.ENCHANTMENT.getId(this) && this.maxLevel > 1))
    }

    override fun checkEnabled(): Boolean{
        val id = Registries.ENCHANTMENT.getId(this)?:return true
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.path,true)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is AbstractAugmentJewelryItem) || (stack.isOf(RegisterItem.TOTEM_OF_AMETHYST))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registries.ITEM.indexedEntries
        list.add(ItemStack(RegisterItem.TOTEM_OF_AMETHYST,1))
        for (entry in entries){
            val item = entry.value()
            if (item is ImbuedJewelryItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }
}