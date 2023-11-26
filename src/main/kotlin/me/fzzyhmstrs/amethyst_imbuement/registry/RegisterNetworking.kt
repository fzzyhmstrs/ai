package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.item_util.ScepterLike
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.spells.ResonateAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.SmitingBlowAugment
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

object RegisterNetworking {

    val KNOCK_BACK_PLAYER = AI.identity("knock_back_packet")
    val UPDATE_ACTIVE_SPELL = AI.identity("update_active_spell")

    fun sendPlayerKnockback(player: ServerPlayerEntity,direction: Vec3d, strength: Double){
        val buf = PacketByteBufs.create()
        buf.writeVector3f(direction.toVector3f())
        buf.writeDouble(strength)
        ServerPlayNetworking.send(player, KNOCK_BACK_PLAYER,buf)
    }

    fun registerServer(){
        RegisterKeybindServer.registerServer()
        AltarOfExperienceScreenHandler.registerServer()
        ImbuingTableScreenHandler.registerServer()
        RecipeUtil.registerServer()
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ACTIVE_SPELL){server,player,_,buf,_ ->
            val spell = buf.readString()
            server.execute {
                val stack = player.mainHandStack
                if (stack.item !is ScepterLike) return@execute
                val spellId = Identifier.tryParse(spell) ?: return@execute
                val spellEnchant = Registries.ENCHANTMENT.get(spellId) as? ScepterAugment ?: return@execute
                val nbt = stack.nbt ?: return@execute
                ScepterHelper.updateActiveAugment(stack, nbt, player, spell, spellEnchant)
            }

        }
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
