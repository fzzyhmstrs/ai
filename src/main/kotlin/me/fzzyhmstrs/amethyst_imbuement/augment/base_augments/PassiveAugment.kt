package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractPassiveAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

open class PassiveAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractPassiveAugment(weight,mxLvl, *slot) {

    val id: Identifier by lazy {
        Registries.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun canAccept(other: Enchantment): Boolean {
        return (other !is PassiveAugment || (Registries.ENCHANTMENT.getId(other) == Registries.ENCHANTMENT.getId(this) && this.maxLevel > 1))
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.toString(),true)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is AbstractAugmentJewelryItem || stack.isIn(RegisterTag.TOTEMS_TAG) || stack.isIn(RegisterTag.WARDS_TAG)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = Registries.ITEM.iterateEntries(RegisterTag.TOTEMS_TAG).map { it.value().defaultStack }.toMutableList()
        list.addAll(Registries.ITEM.iterateEntries(RegisterTag.WARDS_TAG).map { it.value().defaultStack })
        list.addAll(Registries.ITEM.stream().filter { it is AbstractAugmentJewelryItem }.map { it.defaultStack }.toList())
        return list
    }
}