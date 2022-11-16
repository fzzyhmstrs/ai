@file:Suppress("PropertyName")

package me.fzzyhmstrs.amethyst_imbuement

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.*
import me.fzzyhmstrs.amethyst_imbuement.screen.AltarOfExperienceScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import me.fzzyhmstrs.amethyst_imbuement.util.LoggerUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.minecraft.entity.EquipmentSlot
import org.slf4j.LoggerFactory
import java.util.logging.Logger
import kotlin.random.Random

val LOGGER = LoggerUtil.getLogger()

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
        RegisterKeybindServer.registerServer()
        AltarOfExperienceScreenHandler.registerServer()
        RegisterModifier.registerAll()
        RegisterNetworking.registerServer()
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
        RegisterItemModel.registerAll()
        ImbuingTableScreenHandler.registerClient()
        AltarOfExperienceScreenHandler.registerClient()
        RegisterNetworking.registerClient()
    }

    fun aiRandom(): Random{
        return Random(System.currentTimeMillis())
    }
}

object AIPreLaunch: PreLaunchEntrypoint{

    override fun onPreLaunch() {
        MixinExtrasBootstrap.init()
    }

}