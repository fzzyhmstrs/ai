package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.config.SyncConfigPacket
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PlaceItemAugment
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.util.Identifier

object RegisterEvent {

    private val TICKER_EVENT = Identifier(AI.MOD_ID, "ticker_event")
    private val QUEUE_TICK_EVENT = Identifier(AI.MOD_ID, "queue_tick_event")
    val ticker_armor = Ticker(30)
    val ticker_jewelry = Ticker(30)

    fun registerAll(){
        registerServerTick()
        SyncConfigPacket.registerServer()
        ScepterObject.registerServer()
        PlaceItemAugment.registerServer()
    }

    fun registerClient(){
        SyncConfigPacket.registerClient()
        ScepterItem.registerClient()
    }

    private fun registerServerTick(){
        ServerTickEvents.START_SERVER_TICK.register(TICKER_EVENT) {
            ticker_armor.tickUp()
            ticker_jewelry.tickUp()
            ScepterObject.tickModifiers()
        }
        ServerTickEvents.END_SERVER_TICK.register(QUEUE_TICK_EVENT) {
            if (BaseAugment.checkEffectsQueue()){
                BaseAugment.applyEffects()
            }
        }

    }

    class Ticker(private val reset: Int = 20){
        private var tick = 1
        private var ready = false

        fun tickUp(){
            if (tick == reset) {
                tick = 1
                ready = true
                return
            }
            ready = false
            tick++
        }

        fun isReady(): Boolean{
            return ready
        }
    }


}