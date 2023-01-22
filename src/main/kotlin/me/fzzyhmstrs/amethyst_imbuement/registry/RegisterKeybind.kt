package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
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
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW
import java.util.*

@Suppress("PrivatePropertyName")
object RegisterKeybind {

    private val VEIN_MINER_PACKET = Identifier(AI.MOD_ID, "vein_miner_packet")
    @Environment(value = EnvType.CLIENT)
    private var veinMinerKeyPressed: Boolean = false

    private val SCEPTER_ACTIVE_AUGMENT: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.spell_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_BACKSLASH,  // The keycode of the key
            KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        )
    )

    private val VEIN_MINER: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.vein_mine_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_GRAVE_ACCENT,  // The keycode of the key
            KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        )
    )

    private val SPYGLASS_CHANGE: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.sniper_bow_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_EQUAL,  // The keycode of the key
            KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        )
    )

    fun registerAll(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while (SCEPTER_ACTIVE_AUGMENT.wasPressed()) {
                val stack = client.player?.getStackInHand(Hand.MAIN_HAND)?: ItemStack.EMPTY
                if (stack.item is ScepterItem) {
                    val nbt = stack.orCreateNbt
                    if (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
                        val activeEnchant = nbt.getString(NbtKeys.ACTIVE_ENCHANT.str())
                        val activeEnchantName = Registry.ENCHANTMENT.get(Identifier(activeEnchant))?.getName(1)?: AcText.literal(activeEnchant)
                        client.player?.sendMessage(AcText.translatable("scepter.active_spell_key").append(activeEnchantName), true)
                        client.player?.addScoreboardTag(activeEnchant)
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

