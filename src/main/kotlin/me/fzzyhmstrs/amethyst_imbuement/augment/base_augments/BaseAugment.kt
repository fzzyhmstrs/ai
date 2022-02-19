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

    open fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack = ItemStack.EMPTY){
        return
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
        return 150000
    }

    override fun getMaxPower(level: Int): Int {
        return 155000
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
        private val effectQueue: MutableMap<UUID,MutableMap<StatusEffect,MutableList<Pair<Int,Int>>>> = mutableMapOf()
        private val countQueue: MutableMap<UUID,MutableMap<String,Int>> = mutableMapOf()
        private val immediateEffectQueue: MutableMap<UUID,MutableMap<StatusEffect,Pair<Int,Int>>> = mutableMapOf()
        private var immediateEffectFlag: MutableList<UUID> = mutableListOf()

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

        fun addStatusToQueue(uuid: UUID, effect: StatusEffect, duration: Int, amplifier: Int, immediate: Boolean = false){
            if (immediate) {
                if (!immediateEffectFlag.contains(uuid)){
                    immediateEffectFlag.add(uuid)
                }
                if (immediateEffectQueue.containsKey(uuid)){
                    immediateEffectQueue[uuid]?.set(effect,Pair(duration, amplifier))
                } else {
                    immediateEffectQueue[uuid] = mutableMapOf()
                    immediateEffectQueue[uuid]?.set(effect,Pair(duration, amplifier))
                }
                return
            }
            if (effectQueue.containsKey(uuid)) {
                if (effectQueue[uuid]?.containsKey(effect) != true) {
                    effectQueue[uuid]?.set(effect, mutableListOf())
                }
                effectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            } else {
                effectQueue[uuid] = mutableMapOf()
                if (effectQueue[uuid]?.containsKey(effect) != true) {
                    effectQueue[uuid]?.set(effect, mutableListOf())
                }
                effectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            }
        }

        fun applyEffects(entity: LivingEntity){
            val uuid = entity.uuid
            if (effectQueue.containsKey(uuid)){
                val map = effectQueue[uuid]
                if (map?.isNotEmpty() == true){
                    for (effect in map.keys){
                        val effectList = map[effect]
                        if (effectList?.isNotEmpty() == true) {
                            for (effect2 in effectList) {
                                val dur = effect2.first
                                val amp = effect2.second
                                if (dur == 0) continue
                                entity.addStatusEffect(StatusEffectInstance(effect, dur, amp))
                            }
                        }
                    }
                    removeStatusesFromQueue(uuid)
                }
            }
        }

        private fun removeStatusesFromQueue(uuid: UUID){
            if (effectQueue.containsKey(uuid)){
                effectQueue[uuid]?.clear()
            }
        }

        fun checkImmediateEffects(): Boolean{
            return immediateEffectFlag.isNotEmpty()
        }

        fun applyImmediateEffects(entity: LivingEntity){
            val uuid = entity.uuid
            if (immediateEffectQueue.containsKey(uuid)){
                val map = immediateEffectQueue[uuid]
                if (map?.isNotEmpty() == true){
                    for (effect in map.keys){
                        val effectPair = map[effect]
                        val dur = effectPair?.first?:0
                        val amp = effectPair?.second?:0
                        if (dur == 0) continue
                        entity.addStatusEffect(StatusEffectInstance(effect, dur, amp))
                    }
                    removeImmediateStatusesFromQueue(uuid)
                }
            }
        }

        private fun removeImmediateStatusesFromQueue(uuid: UUID){
            if (immediateEffectQueue.containsKey(uuid)){
                immediateEffectQueue.remove(uuid)
            }
            while (immediateEffectFlag.contains(uuid)){
                immediateEffectFlag.remove(uuid)
            }

        }
    }
}