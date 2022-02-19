package me.fzzyhmstrs.amethyst_imbuement.depreciated

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import java.util.*

class ClientStatusQueue {

    private val clientEffectQueue: MutableMap<UUID,MutableMap<StatusEffect,MutableList<Pair<Int,Int>>>> = mutableMapOf()

    fun addClientStatusToQueue(uuid: UUID, effect: StatusEffect, duration: Int, amplifier: Int){
        if (clientEffectQueue.containsKey(uuid)) {
            if (clientEffectQueue[uuid]?.containsKey(effect) == true) {
                clientEffectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            } else {
                clientEffectQueue[uuid]?.set(effect, mutableListOf())
                clientEffectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            }
        } else {
            clientEffectQueue[uuid] = mutableMapOf()
            if (clientEffectQueue[uuid]?.containsKey(effect) == true) {
                clientEffectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            } else {
                clientEffectQueue[uuid]?.set(effect, mutableListOf())
                clientEffectQueue[uuid]?.get(effect)?.add(Pair(duration,amplifier))
            }
        }
    }

    fun applyClientEffects(entity: LivingEntity){
        val uuid = entity.uuid
        if (clientEffectQueue.containsKey(uuid)){
            val map = clientEffectQueue[uuid]
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
                removeClientStatusesFromQueue(uuid)
            }
        }
    }

    private fun removeClientStatusesFromQueue(uuid: UUID){
        if (clientEffectQueue.isEmpty()) return
        if (clientEffectQueue.containsKey(uuid)){
            clientEffectQueue[uuid]?.clear()
        }
    }

}

