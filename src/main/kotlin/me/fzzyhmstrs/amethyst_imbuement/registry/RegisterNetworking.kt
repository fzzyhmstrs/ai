package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.spells.ResonateAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.SmitingBlowAugment
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.util.math.Vec3d

object RegisterNetworking {

    val KNOCK_BACK_PLAYER = AI.identity("knock_back_packet")

    fun sendPlayerKnockback(direction: Vec3d, strength: Double){
        val buf = PacketByteBufs.create()
        buf.writeVector3f(direction.toVector3f())
    }

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        AltarOfExperienceScreenHandler.registerServer()
        ImbuingTableScreenHandler.registerServer()
        RecipeUtil.registerServer()
    }
    fun registerClient(){
        ImbuingTableScreenHandler.registerClient()
        AltarOfExperienceScreenHandler.registerClient()
        ResonateAugment.registerClient()
        SmitingBlowAugment.registerClient()
        BookOfTalesItem.registerClient()
        //ImbuingRecipeBookScreen.registerClientReceiver()
        ClientPlayNetworking.registerGlobalReceiver(KNOCK_BACK_PLAYER) { client, _, buf, _ ->
            val player = client.player ?: return@registerGlobalReceiver
            val direction = Vec3d(buf.readVector3f())
            val strength = buf.readDouble()
            player.takeKnockback(strength,direction.x,direction.z)
        }
    }
}
