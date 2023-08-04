package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.item.ScepterLike
import me.fzzyhmstrs.amethyst_core.scepter.ScepterHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.util.*

object RegisterKeybind {

    private val VEIN_MINER_PACKET = AI.identity( "vein_miner_packet")
    @Environment(value = EnvType.SERVER)
    private val veinMiners: MutableMap<UUID,Boolean> = mutableMapOf()
    @Environment(value = EnvType.CLIENT)
    private var veinMinerKeyPressed: Boolean = false
    private val CATEGORY = "key.categories.amethyst_imbuement"

    private val SCEPTER_ACTIVE_AUGMENT: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.spell_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_BACKSLASH,  // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
        )
    )

    val VEIN_MINER: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.vein_mine_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_GRAVE_ACCENT,  // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
        )
    )

    private val SPYGLASS_CHANGE: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.sniper_bow_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_EQUAL,  // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
        )
    )

    private val SCEPTER_UP: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.scepter_up_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_UNKNOWN,  // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
        )
    )

    private val SCEPTER_DOWN: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.scepter_down_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_UNKNOWN,  // The keycode of the key
            CATEGORY // The translation key of the keybinding's category.
        )
    )

    fun registerAll(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while (SCEPTER_ACTIVE_AUGMENT.wasPressed()) {
                val stack = client.player?.getStackInHand(Hand.MAIN_HAND)?: ItemStack.EMPTY
                if (stack.item is ScepterLike) {
                    val nbt = stack.orCreateNbt
                    if (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
                        val activeEnchant = nbt.getString(NbtKeys.ACTIVE_ENCHANT.str())
                        val activeEnchantName = Registries.ENCHANTMENT.get(Identifier(activeEnchant))?.getName(1)?: AcText.literal(activeEnchant)
                        client.player?.sendMessage(AcText.translatable("scepter.active_spell_key").append(activeEnchantName), true)
                    } else {
                        client.player?.sendMessage(AcText.translatable("scepter.spells_not_activated"), true)

                    }
                }
            }
            while (SPYGLASS_CHANGE.wasPressed()) {
                if (client.player?.isUsingSpyglass == true) {
                    SniperBowItem.changeScope(true)
                }
            }
            var scepterChange = 0
            while (SCEPTER_UP.wasPressed()) {
                scepterChange++
            }
            while (SCEPTER_DOWN.wasPressed()) {
                scepterChange--
            }
            if (scepterChange != 0){
                ScepterHelper.sendScepterUpdateFromClient(scepterChange < 0)
            }
            if (VEIN_MINER.isPressed){
                if (!veinMinerKeyPressed){
                    veinMinerKeyPressed = true
                    val uuid = client.player?.uuid
                    if (uuid != null) {
                        ClientPlayNetworking.send(VEIN_MINER_PACKET, writeBuf(uuid, veinMinerKeyPressed))
                    }
                }
            } else {
                if (veinMinerKeyPressed){
                    veinMinerKeyPressed = false
                    val uuid = client.player?.uuid
                    if (uuid != null) {
                        ClientPlayNetworking.send(VEIN_MINER_PACKET, writeBuf(uuid, veinMinerKeyPressed))
                    }
                }
            }
        })
    }

    private fun writeBuf(uuid: UUID, state: Boolean): PacketByteBuf{
        val buf = PacketByteBufs.create()
        buf.writeUuid(uuid)
        buf.writeBoolean(state)
        return buf
    }

}

