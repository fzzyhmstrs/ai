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

        private var lastApplied: Long = 0L
        private const val duration = 18000
        private val entityShielding: MutableMap<UUID,EntityShieldingInstance> = mutableMapOf()

        fun addTrinket(entity: LivingEntity, amount: Int){
            val uuid = entity.uuid
            if (entityShielding.containsKey(uuid)) {
                entityShielding[uuid]?.addAmount(amount)
            } else {
                val time = entity.world.time
                entityShielding[uuid] = EntityShieldingInstance(amount,time)
            }
            applyEntityShielding(entity)
        }

        fun removeTrinket(entity: LivingEntity, amount: Int){
            val uuid = entity.uuid
            if (entityShielding.containsKey(uuid)) {
                entityShielding[uuid]?.removeAmount(amount)
            }
            applyEntityShielding(entity)
        }

        fun applyEntityShielding(entity: LivingEntity){
            val timeCheck = entity.world.time
            if (timeCheck - lastApplied < 2) return
            lastApplied = timeCheck
            val uuid = entity.uuid
            if (entityShielding.containsKey(uuid)) {
                val data = entityShielding[uuid]?:return
                apply(timeCheck, entity, data)
            }
        }

        private fun apply(timeCheck: Long, entity: LivingEntity, data: EntityShieldingInstance){
            val amount = data.amountToApply(timeCheck,entity)
            if (data.checkAmountZero()){
                entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            } else {
                if (data.isDirty()){
                    entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
                    entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CUSTOM_ABSORPTION,data.getDuration(entity),amount - 1))
                    data.clean()
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

        private class EntityShieldingInstance(amount: Int, timeApplied: Long){

            private var a: Int
            private var d: Int
            private var t: Long
            private var fresh: Boolean
            private var dirty: Boolean

            init{
                a = amount
                d = 0
                t = timeApplied
                fresh = true
                dirty = true
            }

            fun addAmount(newAmount: Int){
                a += newAmount
                markDirty()
            }

            fun removeAmount(oldAmount: Int){
                a = max(0,a - oldAmount)
                markDirty()
            }

            fun isDirty(): Boolean{
                return dirty
            }

            fun clean(){
                dirty = false
            }

            fun checkAmountZero(): Boolean {
                return (a == 0)
            }

            fun amountToApply(timeToCheck: Long, entity: LivingEntity): Int{
                fresh = if (checkLastTimeApplied(timeToCheck)){
                    clearDeficit()
                    updateTimeApplied(timeToCheck)
                    markDirty()
                    true
                } else {
                    checkDeficit(entity)
                    false
                }
                return max(0,a - d)
            }

            fun getDuration(entity: LivingEntity): Int{
                val time = entity.world.time
                return if (!fresh) {
                    (t + duration - time).toInt()
                } else {
                    duration
                }
            }

            private fun markDirty(){
                dirty = true
            }
            private fun checkDeficit(entity: LivingEntity){
                if (!isDirty()) return
                val d2 = checkShieldingDeficit(entity)
                d += d2
            }
            private fun clearDeficit(){
                d = 0
            }
            private fun updateTimeApplied(newTimeApplied: Long){
                t = newTimeApplied
            }
            private fun checkLastTimeApplied(timeToCheck: Long): Boolean{
                return (timeToCheck - t) > (duration * 0.9)
            }


        }

    }
}