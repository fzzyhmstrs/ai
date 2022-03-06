package me.fzzyhmstrs.amethyst_imbuement.depreciated

import dev.emi.trinkets.api.TrinketComponent
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import kotlin.math.abs
import kotlin.math.max

object ShieldingObject {

    private var lastCheck = 0L
    private var lastApplyAmountAttempt = 0
    private var lastApplyDurationAttempt = 0
    private var items: MutableMap<Int,Pair<Pair<Int,Int>,Long>> = mutableMapOf()
    //                           ID, shield amount, duration, time registered/applied


    fun registerTrinket(id:Int,amount:Int, time:Long,duration: Int, entity: LivingEntity):Int{
        return if(id == 0){ //register a brand new trinket, giving a new ide
            val newId = entity.getNewId()
            items[newId] = Pair(Pair(amount,duration),time)
                applyNewShielding(amount, duration, entity, false)
            newId
        } else{ //if the function passes an existing trinket, check to make sure it's passing a proper ID first
            if(!items.containsKey(id)){
                items[id] = Pair(Pair(amount,duration), time)
                    applyNewShielding(amount, duration, entity, false)
            } else{
                //println("item amount: " + amount.toString() + " maps amount: " + items[id]!!.first.first.toString())
                if (amount > items[id]!!.first.first){
                    items[id] = Pair(Pair(amount,duration), items[id]!!.second)
                    val timeSince = time - items[id]!!.second
                    if (timeSince >= (items[id]!!.first.second * 9 / 10)) {
                        applyNewShielding(amount, duration, entity, false)
                    }
                }
            }
            id
        }
    }

    fun removeTrinket(id:Int){
        if(items.containsKey(id)) {
            items[id] = Pair(Pair(0,0), items[id]!!.second)
        }
    }

    private fun applyNewShielding(amount: Int, duration: Int, entity: LivingEntity, fullAmt: Boolean){
        if (entity !is PlayerEntity) return
        val amplifier = getAbsorpMissing(entity).second
        val increment: Int = if (amplifier <= 0){1} else {0}

        //println("inital amount applied: A->" + amplifier.toString() + " + B->" + amount.toString() + " - C->" + increment.toString())
        var totalAbsorpSoFar = 0
        for (v in items.keys) {
            totalAbsorpSoFar += items[v]!!.first.first
        }
        if(totalAbsorpSoFar - 1 >= amplifier) {
            entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            val finalAmp = if (fullAmt) {totalAbsorpSoFar - 1} else {amplifier + amount- increment}
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CUSTOM_ABSORPTION, duration, finalAmp),null)
        }
    }

    fun checkApplyShielding(time:Long, entity: LivingEntity){
        if (entity !is PlayerEntity) return
        if (time - lastCheck < 20) return
        lastCheck = time
        var amountCanApply = 0
        var maxDuration = 0
        if (lastApplyAmountAttempt > 0){
            amountCanApply = lastApplyAmountAttempt
            maxDuration = lastApplyDurationAttempt
        } else {
            var noApplyReady = true
            for (v in items.keys) {
                amountCanApply += items[v]!!.first.first
                maxDuration = max(maxDuration, items[v]!!.first.second)
                if (time - items[v]!!.second >= (items[v]!!.first.second * 9 / 10)) {
                    noApplyReady = false
                }
            }
            if (noApplyReady) return
        }
        val absorpMissing = getAbsorpMissing(entity).first
        val amplifier = getAbsorpMissing(entity).second
        //println("current entity amplifier: " + amplifier.toString())
        //println("amplifier to apply: " + (amountCanApply-1).toString())
        if (amountCanApply - 1 >= amplifier && amountCanApply > 0){
            //println("Time: " + (time).toString() + "map: " + items.toString())
            entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CUSTOM_ABSORPTION, maxDuration, amountCanApply-1),null)
            if (abs(amountCanApply.toFloat() - (entity.absorptionAmount + absorpMissing)) <= 0.01) {
                for (v in items.keys){
                    if (items[v]!!.first.first > 0) {
                        items[v] = Pair(Pair(items[v]!!.first.first, items[v]!!.first.second), time)
                    }
                }
                lastApplyAmountAttempt = 0
                lastApplyDurationAttempt = 0
            } else {
                //println("failed!")
                lastApplyAmountAttempt = amountCanApply
                lastApplyDurationAttempt = maxDuration
            }
        }
    }

    fun TrinketComponent.initializeTrinkets(amount:Int, time:Long, duration: Int,durDiscount: Int, entity: LivingEntity) {
        val trinketsArray = allEquipped
        if (items.isEmpty() || items.size < trinketsArray.size) {
            for (i in trinketsArray.indices) {
                val stack = trinketsArray[i].right
                val nbt = stack.orCreateNbt
                var id: Int
                if (nbt!!.contains(NbtKeys.IMBUE_ID.str())) {
                    id = readNbt(NbtKeys.IMBUE_ID.str(), nbt)
                } else {
                    id = entity.getNewId()
                    writeNbt(NbtKeys.IMBUE_ID.str(),id,nbt)
                }
                val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING,stack)
                if(!items.containsKey(id)) {
                    items[id] = Pair(Pair(amount + shieldLevel, (duration - durDiscount * shieldLevel)), time)
                }
                var totalAbsorpSoFar = 0
                for (v in items.keys) {
                    totalAbsorpSoFar += items[v]!!.first.first
                }
            }
            entity.removeStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
            applyNewShielding(amount, duration, entity, true)
        }
    }

    private fun getAbsorpMissing(entity: PlayerEntity): Pair<Float, Int>{
        val entityInstance : StatusEffectInstance? = entity.getStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
        val hasStatus = entity.hasStatusEffect(RegisterStatus.CUSTOM_ABSORPTION)
        var amplifier = 0
        var absorpMissing = 0.0F
        if (hasStatus && entityInstance != null) {
            amplifier = entityInstance.amplifier
            val absorp = entity.absorptionAmount
            absorpMissing = (amplifier + 1) - absorp
        }
        return Pair(absorpMissing, amplifier)
    }
    private fun writeNbt(key: String, id: Int, nbt: NbtCompound){
        nbt.putInt(key,id)
    }
    private fun readNbt(key: String, nbt: NbtCompound): Int {
        return nbt.getInt(key)
    }
    private fun LivingEntity.getNewId():Int{
        return (random.nextFloat() * 1000000000.0F).toInt()
    }
}