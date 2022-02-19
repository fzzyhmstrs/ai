@file:Suppress("PropertyName")

package me.fzzyhmstrs.amethyst_imbuement

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.LiteralText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.lwjgl.glfw.GLFW


@Suppress("UNUSED")
object AI: ModInitializer {
    private const val MOD_ID = "amethyst_imbuement"
    var instance: MinecraftClient = MinecraftClient.getInstance()
    val slots: Array<EquipmentSlot> = arrayOf(EquipmentSlot.HEAD,EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

    override fun onInitialize() {
        RegisterArmor.registerAll()
        RegisterItem.registerAll()
        RegisterBlock.registerAll()
        RegisterEnchantment.registerAll()
        RegisterLoot.registerAll()
        RegisterEntity.registerAll()
        RegisterHandler.registerAll()
        RegisterStatus.registerAll()
        RegisterVillager.registerAll()

        Registry.register(Registry.RECIPE_SERIALIZER, ImbuingRecipeSerializer.ID, ImbuingRecipeSerializer)
        Registry.register(Registry.RECIPE_TYPE, Identifier("amethyst_imbuement", ImbuingRecipe.Type.ID), ImbuingRecipe.Type)
        Registry.register(Registry.RECIPE_SERIALIZER, AltarRecipeSerializer.ID, AltarRecipeSerializer)
        Registry.register(Registry.RECIPE_TYPE, Identifier("amethyst_imbuement", AltarRecipe.Type.ID), AltarRecipe.Type)
    }
}

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
@Environment(value = EnvType.CLIENT)
class AIClient: ClientModInitializer{


    val SCEPTER_ACTIVE_AUGMENT: KeyBinding? = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.amethyst_imbuement.spell_key",  // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_GRAVE_ACCENT,  // The keycode of the key
            KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        )
    )

    override fun onInitializeClient() {
        RegisterRenderer.registerAll()
        RegisterScreen.registerAll()

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while (SCEPTER_ACTIVE_AUGMENT?.wasPressed() == true) {
                val stack = client.player?.getStackInHand(Hand.MAIN_HAND)?: ItemStack.EMPTY
                if (stack.item is ScepterItem) {
                    val nbt = stack.orCreateNbt
                    if (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
                        val activeEnchant = readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(),nbt)
                        client.player?.sendMessage(LiteralText("Active spell is: $activeEnchant"), true)
                        client.player?.addScoreboardTag(activeEnchant)
                    } else {
                        client.player?.sendMessage(LiteralText("Spells not activated yet..."), true)

                    }
                }
            }
        })
    }

    private fun readStringNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }

}