package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.ItemModelRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentEntityModel
import me.fzzyhmstrs.amethyst_imbuement.model.GlisteringTridentItemEntityRenderer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.Model
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Item
import net.minecraft.resource.ReloadableResourceManagerImpl
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.NoSuchElementException
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty0
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf

@Environment(value = EnvType.CLIENT)
object RegisterItemModel {

    fun registerAll(){
        val modelsPerMode = ItemModelRegistry.ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID + ":glistering_trident#inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID + ":glistering_trident_in_hand#inventory"), true)
        ItemModelRegistry.registerItemModelId(RegisterItem.GLISTERING_TRIDENT, modelsPerMode)
        ItemModelRegistry.registerItemEntityModel(RegisterItem.GLISTERING_TRIDENT,
            GlisteringTridentItemEntityRenderer,
            RegisterRenderer.GLISTERING_TRIDENT,
            GlisteringTridentEntityModel::class.java)
    }
}