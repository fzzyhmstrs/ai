package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW

@Suppress("PrivatePropertyName")
object RegisterKeybind {

    private val SCEPTER_ACTIVE_AUGMENT: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.spell_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_BACKSLASH,  // The keycode of the key
            KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        )
    )

    val VEIN_MINER: KeyBinding = KeyBindingHelper.registerKeyBinding(
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
                        val activeEnchant = readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(),nbt)
                        val activeEnchantName = Registry.ENCHANTMENT.get(Identifier(activeEnchant))?.getName(1)?:LiteralText(activeEnchant)
                        client.player?.sendMessage(TranslatableText("scepter.active_spell_key").append(activeEnchantName), true)
                        client.player?.addScoreboardTag(activeEnchant)
                    } else {
                        client.player?.sendMessage(TranslatableText("scepter.spells_not_activated"), true)

                    }
                }
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while (SPYGLASS_CHANGE.wasPressed()) {
                if (client.player?.isUsingSpyglass == true) {
                    SniperBowItem.changeScope(true)
                }
            }
        })
    }

    private fun readStringNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }

}