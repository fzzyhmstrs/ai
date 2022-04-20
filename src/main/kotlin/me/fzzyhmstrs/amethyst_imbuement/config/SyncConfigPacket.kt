package me.fzzyhmstrs.amethyst_imbuement.config

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier

object SyncConfigPacket {

    val SYNC_CONFIG_PACKET = Identifier("sync_config_packet")

    fun registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_CONFIG_PACKET) { _, _, buf, _ ->
            AiConfig.readFromServer(buf)
        }
    }

    fun registerServer() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player = handler.player
            val buf = PacketByteBufs.create()
            AiConfig.writeToClient(buf)
            ServerPlayNetworking.send(player, SYNC_CONFIG_PACKET, buf)
        }
    }
}