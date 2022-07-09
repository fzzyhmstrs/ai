package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier
import java.util.*

object RegisterKeybindServer {

    private val VEIN_MINER_PACKET = Identifier(AI.MOD_ID, "vein_miner_packet")
    @Environment(value = EnvType.SERVER)
    private val veinMiners: MutableMap<UUID,Boolean> = mutableMapOf()

    fun registerServer(){
        ServerPlayNetworking.registerGlobalReceiver(VEIN_MINER_PACKET) { _, _, _, buf, _ ->
            val uuid = buf.readUuid()
            val state = buf.readBoolean()
            veinMiners[uuid] = state
        }
    }

    fun checkForVeinMine(uuid: UUID): Boolean{
        return veinMiners[uuid]?:false
    }
}