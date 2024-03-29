package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.item_util.ScepterLike
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.weapon.SniperBowItem
import me.fzzyhmstrs.amethyst_imbuement.screen.SpellRadialHud
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
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
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.util.*

@Environment(value = EnvType.CLIENT)
object RegisterKeybind {

    private val VEIN_MINER_PACKET = Identifier(AI.MOD_ID, "vein_miner_packet")
    @Environment(value = EnvType.CLIENT)
    private var veinMinerKeyPressed: Boolean = false
    @Environment(value = EnvType.CLIENT)
    internal var radialMenuJustClosed = false
    //private var scepterRadialMenuPressed: Int = 0
    @Environment(value = EnvType.CLIENT)
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

    val SCEPTER_RADIAL_MENU: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.scepter_radial_menu_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_LEFT_ALT,  // The keycode of the key
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
                        val activeEnchantName = FzzyPort.ENCHANTMENT.get(Identifier(activeEnchant))?.getName(1)?: AcText.literal(activeEnchant)
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
            //println("ALT pressed" + SCEPTER_RADIAL_MENU.isPressed)
            var scepterWasPressed = 0
            while (SCEPTER_RADIAL_MENU.wasPressed()){
                scepterWasPressed++
            }
            if (scepterWasPressed > 0){
                if (client.currentScreen == null) {
                    if (client.player?.mainHandStack?.item is ScepterLike && !radialMenuJustClosed)
                        client.setScreen(SpellRadialHud)
                } else if (client.currentScreen is SpellRadialHud)
                    client.setScreen(null)
                radialMenuJustClosed = false
            }
            /*if (SCEPTER_RADIAL_MENU.isPressed){
                if (scepterRadialMenuPressed < 5){
                    scepterRadialMenuPressed = 5
                    if (MinecraftClient.getInstance().currentScreen == null)
                        MinecraftClient.getInstance().setScreen(SpellRadialHud)
                }
            } else {
                if (scepterRadialMenuPressed > 0){
                    scepterRadialMenuPressed--
                    if (scepterRadialMenuPressed <= 0)
                        if (MinecraftClient.getInstance().currentScreen is SpellRadialHud)
                            MinecraftClient.getInstance().setScreen(null)
                }
            }*/
        })
    }

    private fun writeBuf(uuid: UUID, state: Boolean): PacketByteBuf{
        val buf = PacketByteBufs.create()
        buf.writeUuid(uuid)
        buf.writeBoolean(state)
        return buf
    }

}

