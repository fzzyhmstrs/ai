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
    var veinMineActive: Boolean = false

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
        RegisterRecipe.registerAll()
    }
}

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
@Environment(value = EnvType.CLIENT)
class AIClient: ClientModInitializer{

    override fun onInitializeClient() {
        RegisterRenderer.registerAll()
        RegisterScreen.registerAll()
        RegisterKeybind.registerAll()
    }
}