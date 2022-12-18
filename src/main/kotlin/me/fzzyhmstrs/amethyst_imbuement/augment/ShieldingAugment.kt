package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketsApi
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.math.random.Random
import java.util.*

class ShieldingAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ImbuedJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registries.ITEM.indexedEntries
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
        private var blockChance: MutableMap<UUID,Float> = mutableMapOf()

        fun refreshTrinkets(entity: LivingEntity): Float{
            var chance = 0.0f
            if (RegisterEnchantment.SHIELDING.isEnabled()) {
                TrinketsApi.getTrinketComponent(entity).ifPresent { trinkets ->
                    trinkets.forEach { _, stack ->
                        val item = stack.item
                        if (item is ImbuedJewelryItem) {
                            val level = if (RegisterEnchantment.SHIELDING.isEnabled()) {
                                EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
                            } else {
                                0
                            }
                            chance += baseAmount + shieldingAmount * level
                        }
                    }
                }
            }
            blockChance[entity.uuid] = chance
            return chance
        }

        fun damageIsBlocked(random: Random, entity: LivingEntity, damageSource: DamageSource): Boolean{
            if (damageSource == DamageSource.OUT_OF_WORLD) return false
            if (damageSource == DamageSource.STARVE) return false
            if (damageSource == DamageSource.FALL) return false
            return random.nextFloat() < (blockChance[entity.uuid] ?: 0.0f)
        }

    }
}
