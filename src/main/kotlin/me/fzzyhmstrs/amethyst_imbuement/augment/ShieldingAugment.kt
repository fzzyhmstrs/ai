package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.SlotReference
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import java.util.*
import kotlin.math.*

class ShieldingAugment(weight: Rarity,mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight, mxLvl, *slot) {


    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return (stack.item is ImbuedJewelryItem)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val entries = Registry.ITEM.entries
        for (entry in entries){
            val item = entry.value
            if (item is ImbuedJewelryItem){
                list.add(ItemStack(item,1))
            }
        }
        return list
    }

    companion object ShieldingObject{

        private var lastApplied: Long = 0L
        private var previousAmount = 0
        private const val duration = 18000
        private val appliedShielding: MutableMap<UUID,MutableMap<SlotReference, Amounts>> = mutableMapOf()
        private val dirty : MutableMap<UUID, MutableMap<SlotReference,Int>> = mutableMapOf()
        private val dirtyFlag : MutableList<UUID> = mutableListOf()
        private val dirtyDeficit : MutableMap <UUID, Int> = mutableMapOf()

        fun addTrinketToQueue(entity: LivingEntity, sr: SlotReference, amount: Int){
            val uuid = entity.uuid
            if (appliedShielding.containsKey(uuid)){
                if (appliedShielding[uuid]?.containsKey(sr) == false) {
                    appliedShielding[uuid]?.set(sr, Amounts(amount,0, 0L))
                    markDirty(uuid, sr, amount)
                } else {
                    val priorAmounts = appliedShielding[uuid]?.get(sr)
                    if (priorAmounts != null){
                        val added = priorAmounts.amountAdd
                        val removed = priorAmounts.amountRemove
                        val time = priorAmounts.timeAdded
                        if (amount > added && amount > removed){
                            val amountToDirty = max(amount - added, amount - removed)
                            appliedShielding[uuid]?.set(sr, Amounts(amount,removed,0L))
                            markDirty(uuid,sr, amountToDirty)
                        } else {
                            appliedShielding[uuid]?.set(sr, Amounts(amount, removed, time))
                        }
                    } else {
                        appliedShielding[uuid]?.set(sr, Amounts(amount,0,0L))
                        markDirty(uuid,sr, amount)
                    }
                }
            } else {
                appliedShielding[uuid] = mutableMapOf()
                appliedShielding[uuid]?.set(sr, Amounts(amount,0,0L))
                markDirty(uuid, sr, amount)
            }
        }

        fun removeTrinketFromQueue(entity: LivingEntity, sr: SlotReference){
            val statusInstance = entity.getStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            if (statusInstance != null) {
                previousAmount = statusInstance.amplifier + 1
            }
            val uuid = entity.uuid
            if (appliedShielding.containsKey(uuid)){
                if (appliedShielding[uuid]?.containsKey(sr) == true){
                    val amountRemoved = appliedShielding[uuid]?.get(sr)?.amountAdd ?: 0
                    val timeRemoved = appliedShielding[uuid]?.get(sr)?.timeAdded ?: 0L
                    appliedShielding[uuid]?.set(sr,Amounts(0,amountRemoved,timeRemoved))
                    markDirty(uuid,sr, 0)
                    applyShielding(entity)
                }
            }
        }

        fun applyShielding(entity: LivingEntity){
            val timeCheck = entity.world.time
            if (timeCheck - lastApplied < 5) return
            lastApplied = timeCheck
            val uuid = entity.uuid
            var apply = false
            var totalAmount = 0
            var durationApply = duration
            val statusInstance = entity.getStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            val dirty = getDirtyFlag(uuid)
            if (appliedShielding.containsKey(uuid)){
                val map = appliedShielding[uuid]
                if (map?.isNotEmpty() == true){
                    for (slot in map.keys){
                        val timeAdded = map[slot]?.timeAdded ?: timeCheck
                        if (timeCheck - timeAdded > duration * 0.9){
                            apply = true
                        }
                        val amountAdd = map[slot]?.amountAdd ?: 0
                        totalAmount += amountAdd
                    }
                    if (apply || dirty){
                        if (dirty){
                            markDirtyDeficit(uuid, checkShieldingDeficit(entity))
                            durationApply = statusInstance?.duration?:duration
                            val deficit = getDirtyDeficit(uuid)
                            val dirtyAmount = getDirty(uuid)
                            totalAmount = max(totalAmount- deficit,dirtyAmount)
                        }
                        if (totalAmount - 1 == (statusInstance?.amplifier ?: -1)) {
                            cleanDirty(uuid)
                            return
                        }
                        entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
                        entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CUSTOM_ABSORPTION,durationApply,totalAmount - 1))
                        for (slot in map.keys){
                            //offset the duration by the difference of the amount is left, so that when it's time to reapply, the timer allows it
                            val durationOffset = duration - durationApply
                            val amountAdd = map[slot]?.amountAdd ?: 0
                            map[slot] = Amounts(amountAdd,0,timeCheck - durationOffset)
                        }
                        if (!dirty) cleanDirtyDeficit(uuid)
                        cleanDirty(uuid)
                    }
                }
            }
        }

        private fun checkShieldingDeficit(entity: LivingEntity): Int{
            val statusInstance: StatusEffectInstance = entity.getStatusEffect(RegisterStatus.CUSTOM_ABSORPTION) ?: return 0
            val baseAmount = statusInstance.amplifier + 1
            val currentAmount = entity.absorptionAmount
            val delta = baseAmount - currentAmount
            val deltaFloor = floor(delta)
            val deltaCiel = ceil(delta)
            val closer = min(abs(delta - deltaFloor), abs(delta - deltaCiel))
            return if (closer == abs(delta - deltaFloor)){
                deltaFloor.toInt()
            } else {
                deltaCiel.toInt()
            }
        }
        private fun markDirty(uuid: UUID,sr: SlotReference, amount: Int){
            dirtyFlag.add(uuid)
            if (dirty.containsKey(uuid)){
                if (dirty[uuid]?.containsKey(sr) == true){
                    val currentAmount = dirty[uuid]?.get(sr)?:0
                    if (amount > currentAmount || amount == 0){
                        dirty[uuid]?.set(sr,amount)
                    }
                } else {
                    dirty[uuid]?.set(sr,amount)
                }
            } else {
                dirty[uuid] = mutableMapOf()
                dirty[uuid]?.set(sr,amount)
            }
        }
        private fun getDirty(uuid: UUID): Int {
            return if (dirty.containsKey(uuid)){
                val map = dirty[uuid]
                var amount = 0
                if (map?.isNotEmpty() == true){
                    for (slot in map.keys){
                        amount += map[slot]?:0
                    }
                }
                amount
            } else {
                0
            }
        }
        private fun getDirtyFlag(uuid: UUID): Boolean{
            return dirtyFlag.contains(uuid)
        }
        private fun cleanDirty(uuid: UUID){
            if (dirty.containsKey(uuid)){
                dirty.remove(uuid)
            }
            if (appliedShielding.containsKey(uuid)){
                val map = appliedShielding[uuid]
                if (map?.isNotEmpty() == true){
                    for (slot in map.keys){
                        val amount = map[slot]?.amountAdd?:0
                        val time = map[slot]?.timeAdded?:0L
                        appliedShielding[uuid]?.set(slot, Amounts(amount, 0, time))
                    }
                }
            }
            if (dirtyFlag.contains(uuid)) {
                var present = true
                while (present) {
                    present = dirtyFlag.remove(uuid)
                }
            }
        }
        private fun markDirtyDeficit(uuid: UUID, amount: Int){
            if (dirtyDeficit.containsKey(uuid)){
                val previousAmount: Int = dirtyDeficit[uuid]?:0
                dirtyDeficit[uuid] = previousAmount + amount
            } else {
                dirtyDeficit[uuid] = amount
            }
        }
        private fun cleanDirtyDeficit(uuid: UUID){
            if (dirtyDeficit.containsKey(uuid)){
                dirtyDeficit.remove(uuid)
            }
        }
        private fun getDirtyDeficit(uuid: UUID): Int{
            return if (dirtyDeficit.containsKey(uuid)){
                dirtyDeficit[uuid]?:0
            } else {
                0
            }
        }

        private data class Amounts(val amountAdd: Int, val amountRemove: Int, val timeAdded: Long)
    }
}