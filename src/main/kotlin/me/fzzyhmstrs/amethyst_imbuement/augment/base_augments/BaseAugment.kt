package me.fzzyhmstrs.amethyst_imbuement.augment.base_augments

import me.fzzyhmstrs.amethyst_imbuement.util.AcceptableItemStacks
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.*
import java.util.*


open class BaseAugment(weight: Rarity, val mxLvl: Int = 1, val target: EnchantmentTarget, vararg slot: EquipmentSlot): Enchantment(weight, target ,slot) {

    open fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack = ItemStack.EMPTY){
        return
    }

    open fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack = ItemStack.EMPTY): Boolean{
        return true
    }

    open fun unequipEffect(user: LivingEntity, level: Int, stack: ItemStack = ItemStack.EMPTY){
        return
    }

    open fun attributeModifier(stack: ItemStack, uuid: UUID): Pair<EntityAttribute,EntityAttributeModifier>? {
        return null
    }

    open fun acceptableItemStacks(): MutableList<ItemStack>{
        return AcceptableItemStacks.baseAcceptableItemStacks(target)
    }

    override fun getMinPower(level: Int): Int {
        return 30
    }

    override fun getMaxPower(level: Int): Int {
        return 50
    }

    override fun getMaxLevel(): Int {
        return mxLvl
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return false
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return false
    }

    companion object{
        private val countQueue: MutableMap<UUID,MutableMap<String,Int>> = mutableMapOf()
        private val effectQueue: MutableMap<LivingEntity,MutableMap<StatusEffect,MutableList<Pair<Int,Int>>>> = mutableMapOf()
        private var checkEffects: Boolean = false

        fun addCountToQueue(uuid: UUID,countTag: String,count: Int){
            if (countQueue.containsKey(uuid)){
                countQueue[uuid]?.set(countTag,count)
            } else {
                countQueue[uuid] = mutableMapOf()
                countQueue[uuid]?.set(countTag,count)
            }
        }

        fun readCountFromQueue(uuid: UUID, countTag: String): Int {
            if (countQueue.containsKey(uuid)){
                if (countQueue[uuid]?.containsKey(countTag) == true){
                    return countQueue[uuid]?.getOrDefault(countTag,0)?:0
                }
            }
            return 0
        }

        fun addStatusToQueue(livingEntity: LivingEntity, effect: StatusEffect, duration: Int, amplifier: Int){
            if (effectQueue.containsKey(livingEntity)) {
                if (effectQueue[livingEntity]?.containsKey(effect) != true) {
                    effectQueue[livingEntity]?.set(effect, mutableListOf())
                }
                effectQueue[livingEntity]?.get(effect)?.add(Pair(duration,amplifier))
            } else {
                effectQueue[livingEntity] = mutableMapOf(effect to mutableListOf(Pair(duration,amplifier)))
            }
            checkEffects = true
        }


        fun applyEffects(){
            for ((entity,statusMap) in effectQueue){
                if (entity.isDead|| entity.isRemoved) continue
                for ((effect,effectList) in statusMap){
                    for ((dur,amp) in effectList) {
                        entity.addStatusEffect(StatusEffectInstance(effect, dur, amp))
                    }
                }
            }
            //clear out the queue if any are needed
            clearStatusesFromQueue()
        }

        private fun clearStatusesFromQueue(){
            effectQueue.clear()
            checkEffects = false
        }

        fun checkEffectsQueue(): Boolean{
            return checkEffects
        }
    }
}