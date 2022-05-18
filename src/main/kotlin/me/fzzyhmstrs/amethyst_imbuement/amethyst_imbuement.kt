@file:Suppress("PropertyName")

package me.fzzyhmstrs.amethyst_imbuement

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.config.SyncConfigPacket
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.*
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PlaceItemAugment
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.EquipmentSlot
import kotlin.random.Random

object AI: ModInitializer {
    const val MOD_ID = "amethyst_imbuement"
    val slots: Array<EquipmentSlot> = arrayOf(EquipmentSlot.HEAD,EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

    override fun onInitialize() {
        AiConfig.initConfig()
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
        RegisterEvent.registerAll()
        RegisterModifier.registerAll()
    }

    fun aiRandom(): Random{
        return Random(System.currentTimeMillis())
    }
}

@Environment(value = EnvType.CLIENT)
object AIClient: ClientModInitializer{

    override fun onInitializeClient() {
        RegisterRenderer.registerAll()
        RegisterScreen.registerAll()
        RegisterKeybind.registerAll()
        RegisterEvent.registerClient()
    }

}