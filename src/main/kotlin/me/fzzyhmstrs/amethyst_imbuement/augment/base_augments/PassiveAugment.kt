package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
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
        return (stack.item is AbstractAugmentJewelryItem) || (stack.isOf(RegisterTool.TOTEM_OF_AMETHYST)) || (stack.isOf(RegisterTool.IMBUED_WARD))
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registries.ITEM.indexedEntries
        list.add(ItemStack(RegisterTool.TOTEM_OF_AMETHYST,1))
        for (entry in entries){
            val item = entry.value()
            if (item is AbstractAugmentJewelryItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }
}