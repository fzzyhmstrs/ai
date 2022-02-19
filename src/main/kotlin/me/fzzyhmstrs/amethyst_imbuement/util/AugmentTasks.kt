package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.UsedActiveAugment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.world.World

interface AugmentTasks {

    //not covered by this interface are "specialEffect" methods. Those need to be called specifically from

    //used with "ActiveAugment" for accessing the activateEffect method
    fun activeEnchantmentTasks(stack: ItemStack, world: World, entity: Entity){
        if (entity !is LivingEntity) return
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is ActiveAugment){
                val lvl = enchants[enchant] ?: 1
                enchant.activateEffect(entity,lvl, stack)
            }
        }
    }

    //used with "ActiveAugment" for deactivating any effects that need that via the deactivateEffect method
    fun inactiveEnchantmentTasks(stack: ItemStack,world: World, entity: Entity){
        if (entity !is LivingEntity) return
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is ActiveAugment){
                val lvl = enchants[enchant] ?: 1
                enchant.deactivateEffect(entity,lvl,stack)
            }
        }
    }

    //used with "UsedActiveAugment" for accessing the useEffect method
    fun usageEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is LivingEntity) return
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is UsedActiveAugment){
                val lvl = enchants[enchant] ?: 1
                enchant.useEffect(entity,lvl,stack)
            }
        }
    }

    //used with "PassiveAugment" for accessing the tickEffect method
    fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is LivingEntity) return
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is PassiveAugment){
                val lvl = enchants[enchant] ?: 1
                enchant.tickEffect(entity,lvl,stack)
            }
        }
    }

    //used with "PassiveAugment" for accessing the tickEffect method
    fun unequipEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is LivingEntity) return
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is BaseAugment){
                val lvl = enchants[enchant] ?: 1
                enchant.unequipEffect(entity,lvl,stack)
            }
        }
    }

    //used with "PassiveAugment" for accessing the tickEffect method
    fun modifierEnchantmentTasks(stack: ItemStack,world: World,entity: Entity): MutableMap<EntityAttribute, EntityAttributeModifier>?{
        if (entity !is LivingEntity) return null
        val modifiers : MutableMap<EntityAttribute, EntityAttributeModifier> = mutableMapOf()
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is BaseAugment){
                //val lvl = enchants[enchant] ?: 1
                val modifier: Pair<EntityAttribute, EntityAttributeModifier> = enchant.attributeModifier(stack,entity.uuid)?:continue
                modifiers[modifier.first] = modifier.second
            }
        }
        return modifiers
    }

}