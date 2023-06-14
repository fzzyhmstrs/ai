package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.registry.Registries

class BulwarkAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW,*slot) {

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(id.toString(),1)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registries.ITEM.indexedEntries
        for (entry in entries){
            val item = entry.value()
            if (item is ShieldItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ShieldItem)
    }

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        //user is the user
        if (user.world.isClient || !checkEnabled()) return false
        user.heal(1.0f)
        return true
    }

}