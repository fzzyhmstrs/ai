package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterMaterialAddon
import net.minecraft.block.Block
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

object AcceptableItemStacks {
    
    val scepterAcceptableMap: MutableMap<Int,MutableList<ItemStack>> = mutableMapOf()

    fun baseAcceptableItemStacks(target: EnchantmentTarget?): MutableList<ItemStack>{
        val entries = Registry.ITEM.indexedEntries
        val list: MutableList<ItemStack> = mutableListOf()
        return when(target){
            null->{
                mutableListOf()
            }
            EnchantmentTarget.ARMOR->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is ArmorItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.ARMOR_FEET->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is ArmorItem && item.slotType == EquipmentSlot.FEET){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.ARMOR_HEAD->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is ArmorItem && item.slotType == EquipmentSlot.HEAD){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.ARMOR_CHEST->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is ArmorItem && item.slotType == EquipmentSlot.CHEST){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.ARMOR_LEGS->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is ArmorItem && item.slotType == EquipmentSlot.LEGS){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.CROSSBOW->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is CrossbowItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.BREAKABLE->{
                for (entry in entries){
                    val item = entry.value()
                    if (item.isDamageable){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.BOW->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is BowItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.VANISHABLE->{
                for (entry in entries){
                    val item = entry.value()
                    if (item.isDamageable || item is Vanishable || Block.getBlockFromItem(item) is Vanishable){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.WEAPON->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is SwordItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.DIGGER->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is MiningToolItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.TRIDENT->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is TridentItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.FISHING_ROD->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is FishingRodItem){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
            EnchantmentTarget.WEARABLE->{
                for (entry in entries){
                    val item = entry.value()
                    if (item is Wearable || Block.getBlockFromItem(item) is Wearable){
                        list.add(ItemStack(item,1))
                    }
                }
                list
            }
        }
    }
    
    fun scepterAcceptableItemStacks(tier:Int): MutableList<ItemStack>{
        if (scepterAcceptableMap.containsKey(tier)){
            return scepterAcceptableMap[tier] ?: mutableListOf()
        } else {
            val entries = Registry.ITEM.indexedEntries
            val list: MutableList<ItemStack> = mutableListOf()
            for (entry in entries){
                val item = entry.value()
                if (item is ScepterItem){
                    val material = item.material
                    if (material is ScepterMaterialAddon){
                        if (material.scepterTier() >= tier){
                            list.add(ItemStack(item,1))
                        }
                    }
                }
            }
            scepterAcceptableMap[tier] = list
            return list
        }
    }
}
