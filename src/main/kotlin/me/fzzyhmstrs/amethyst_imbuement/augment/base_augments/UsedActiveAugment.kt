package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractUsedActiveAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

open class UsedActiveAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): AbstractUsedActiveAugment(weight,mxLvl,*slot) {

    val id: Identifier by lazy {
        Registries.ENCHANTMENT.getId(this)?: throw IllegalStateException("Couldn't find this enchantment in the Registry!: $this")
    }

    override fun canAccept(other: Enchantment): Boolean {
        return (other !is ActiveAugment)
    }

    override fun checkEnabled(): Boolean{
        return AiConfig.trinkets.enabledAugments.getOrDefault(id.toString(),true)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isIn(RegisterTag.TOTEMS_TAG)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return Registries.ITEM.iterateEntries(RegisterTag.TOTEMS_TAG).map { it.value().defaultStack }.toMutableList()
    }
}