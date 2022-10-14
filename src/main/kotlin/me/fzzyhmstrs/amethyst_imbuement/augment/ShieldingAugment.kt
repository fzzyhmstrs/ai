package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketsApi
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import java.util.*

class ShieldingAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ImbuedJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.indexedEntries
        for (entry in entries){
            val item = entry.value()
            if (item is ImbuedJewelryItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }

    companion object ShieldingObject{

        const val baseAmount = 0.025f
        const val shieldingAmount = 0.02f
        private var blockChance = 0.0f

        fun refreshTrinkets(entity: LivingEntity){
            var chance = 0.0f
            TrinketsApi.getTrinketComponent(entity).ifPresent {trinkets ->
                trinkets.forEach { _, stack ->
                    val item = stack.item
                    if (item is ImbuedJewelryItem){
                        val level = if (RegisterEnchantment.SHIELDING.isEnabled()) {
                            EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
                        } else {
                            0
                        }
                        chance += baseAmount + shieldingAmount * level
                    }
                }
            }
            blockChance = chance
        }

        fun damageIsBlocked(random: Random): Boolean{
            return random.nextFloat() < blockChance
        }

    }
}
