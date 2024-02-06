@file:Suppress("PropertyName")

package me.fzzyhmstrs.amethyst_imbuement

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.*
import me.fzzyhmstrs.amethyst_imbuement.renderer.feature.BaseHamsterArmorFeatureRenderer
import me.fzzyhmstrs.amethyst_imbuement.util.LoggerUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

val LOGGER = LoggerUtil.getLogger()

object AI: ModInitializer {
    const val MOD_ID = "amethyst_imbuement"
    private val random = Random.createThreadSafe()
    private val kotlinRandom = kotlin.random.Random(System.currentTimeMillis())

    val slots: Array<EquipmentSlot> = arrayOf(EquipmentSlot.HEAD,EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

    override fun onInitialize() {
        RegisterTag.registerAll()
        RegisterBlock.registerAll()
        RegisterEnchantment.registerAll()
        RegisterArmor.registerAll()
        RegisterItem.registerAll()
        RegisterTool.registerAll()
        RegisterScepter.registerAll()
        RegisterPotion.registerAll()
        RegisterLoot.registerAll()
        RegisterEntity.registerAll()
        RegisterHandler.registerAll()
        RegisterStatus.registerAll()
        RegisterVillager.registerAll()
        RegisterRecipe.registerAll()
        RegisterCriteria.registerAll()
        RegisterParticle.registerParticleTypes()
        RegisterSound.registerAll()
        RegisterWorldgen.registerAll()
        AiConfig.initConfig()
        RegisterModifier.registerAll()
        RegisterNetworking.registerServer()
    }

    fun aiRandom(): Random{
        return random
    }
    fun aiKotlinRandom(): kotlin.random.Random{
        return kotlinRandom
    }

    fun identity(name: String): Identifier {
        return Identifier(MOD_ID,name)
    }
}

@Environment(value = EnvType.CLIENT)
object AIClient: ClientModInitializer{

    override fun onInitializeClient() {
        RegisterRenderer.registerAll()
        RegisterScreen.registerAll()
        RegisterKeybind.registerAll()
        RegisterItemModel.registerAll()
        RegisterParticle.registerParticleFactories()
        BaseHamsterArmorFeatureRenderer.HamsterArmorTextureIdsHolder.registerClient()
        RegisterNetworking.registerClient()
        RegisterCommandClient.registerClient()
        AiConfig.registerClient()
    }

    fun aiRandom(): Random{
        return Random.createThreadSafe()
    }
}

object AIPreLaunch: PreLaunchEntrypoint{

    override fun onPreLaunch() {
        MixinExtrasBootstrap.init()
    }

}
