package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.trinket_util.TrinketUtil
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.util.Hand

class ImmunityAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    /*override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        EffectQueue.addStatusToQueue(user,RegisterStatus.IMMUNITY,260,0)
    }*/

    fun checkAndDamageTrinket(entity: LivingEntity, effect: StatusEffectInstance): Boolean{
        if (effect.effectType.isBeneficial) return true
        val trinkets = TrinketUtil.getTrinketStacks(entity)
        val world = entity.world
        val amplifier = effect.amplifier + 1
        val damage = amplifier * AiConfig.items.manaItems.imbuedJewelryDamagePerAmplifier.get()
        var bl = false
        for (stack in trinkets){
            if (EnchantmentHelper.getLevel(RegisterEnchantment.IMMUNITY,stack) == 0) continue
            if(!RegisterItem.IMBUED_AMULET.checkCanUse(stack,world,entity,damage, AcText.empty())) continue
            RegisterItem.IMBUED_AMULET.manaDamage(stack,world,entity,damage)
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY,60))
            bl = true
        }
        val totem1 = entity.getStackInHand(Hand.OFF_HAND)
        if (EnchantmentHelper.getLevel(RegisterEnchantment.IMMUNITY,totem1) != 0){
            if(RegisterItem.IMBUED_AMULET.checkCanUse(totem1,world,entity,damage, AcText.empty())){
                RegisterItem.IMBUED_AMULET.manaDamage(totem1,world,entity,damage)
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY,60))
                bl = true
            }
        }

        val totem2 = entity.getStackInHand(Hand.MAIN_HAND)
        if (EnchantmentHelper.getLevel(RegisterEnchantment.IMMUNITY,totem2) != 0){
            if(RegisterItem.IMBUED_AMULET.checkCanUse(totem2,world,entity,damage, AcText.empty())){
                RegisterItem.IMBUED_AMULET.manaDamage(totem2,world,entity,damage)
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY,60))
                bl = true
            }
        }

        return bl
    }

}