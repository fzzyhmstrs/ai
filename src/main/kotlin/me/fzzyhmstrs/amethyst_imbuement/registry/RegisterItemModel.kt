package me.fzzyhmstrs.amethyst_imbuement.registry

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
@Deprecated("moving to amethyst_core")
object RegisterItemModel: SynchronousResourceReloader {

    private val fallbackId = ModelIdentifier("minecraft:trident_in_hand#inventory")
    private val modelIdMap: HashMap<Item, ModelIdentifierPerModes> = HashMap()
    private val entityModelMap: HashMap<Item,CustomItemEntityModelLoader> = HashMap()
    private val entityModelLoader: EntityModelLoader by lazy { MinecraftClient.getInstance().entityModelLoader }

    var GLISTERING_TRIDENT_MODEL: Model? = null

    override fun reload(manager: ResourceManager) {
        entityModelMap.forEach {
            it.value.reload()
        }
    }

    fun registerAll(){
        registerReloader()
        val modelsPerMode = ModelIdentifierPerModes(ModelIdentifier(AI.MOD_ID + ":glistering_trident#inventory"))
            .withHeld(ModelIdentifier(AI.MOD_ID + ":glistering_trident_in_hand#inventory"), true)
        registerItemModelId(RegisterItem.GLISTERING_TRIDENT, modelsPerMode)
        registerItemEntityModel(RegisterItem.GLISTERING_TRIDENT,
            GlisteringTridentItemEntityRenderer,
            RegisterRenderer.GLISTERING_TRIDENT,
            GlisteringTridentEntityModel::class.java)
    }

    fun registerItemModelId(item: Item, models: ModelIdentifierPerModes){
        if (modelIdMap.containsKey(item)){
            throw IllegalStateException("Item ${item.name} already present in ItemModelRegistry")
        }
        modelIdMap[item] = models
    }

    fun registerItemEntityModel(item: Item , renderer: BuiltinItemRendererRegistry.DynamicItemRenderer, layer: EntityModelLayer , classType : Class<out Model>){
        entityModelMap[item] = CustomItemEntityModelLoader(layer, classType)
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer)
    }

    private fun registerReloader(){
        ClientLifecycleEvents.CLIENT_STARTED.register{
                client: MinecraftClient -> (client.resourceManager as ReloadableResourceManagerImpl).registerReloader(this)
        }
    }

    fun itemHasCustomModel(item: Item): Boolean{
        return (modelIdMap.containsKey(item))
    }

    fun getModel(item: Item, mode: ModelTransformation.Mode): ModelIdentifier{
        return modelIdMap[item]?.getIdFromMode(mode) ?: fallbackId
    }

    fun getEntityModelLoader(item: Item): CustomItemEntityModelLoader{
        return entityModelMap[item]?:throw NoSuchElementException("Item ${item.name} not present in model loader registry.")
    }

    class ModelIdentifierPerModes(private val defaultId: ModelIdentifier){
        private val modeMap: EnumMap<ModelTransformation.Mode,ModelIdentifier> = EnumMap(ModelTransformation.Mode::class.java)

        fun with(mode: ModelTransformation.Mode, modelId: ModelIdentifier, needsRegistration: Boolean = false): ModelIdentifierPerModes{
            if (needsRegistration){
                registerIdWithModalLoading(modelId)
            }
            modeMap[mode] = modelId
            return this
        }
        fun withGuiGroundFixed(modelId: ModelIdentifier, needsRegistration: Boolean = false): ModelIdentifierPerModes{
            if (needsRegistration){
                registerIdWithModalLoading(modelId)
            }
            modeMap[ModelTransformation.Mode.GUI] = modelId
            modeMap[ModelTransformation.Mode.FIXED] = modelId
            modeMap[ModelTransformation.Mode.GROUND] = modelId
            return this
        }
        fun withFirstHeld(modelId: ModelIdentifier, needsRegistration: Boolean = false): ModelIdentifierPerModes {
            if (needsRegistration){
                registerIdWithModalLoading(modelId)
            }
            modeMap[ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND] = modelId
            modeMap[ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND] = modelId
            return this
        }
        fun withThirdHeld(modelId: ModelIdentifier, needsRegistration: Boolean = false): ModelIdentifierPerModes {
            if (needsRegistration){
                registerIdWithModalLoading(modelId)
            }
            modeMap[ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND] = modelId
            modeMap[ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND] = modelId
            return this
        }
        fun withHeld(modelId: ModelIdentifier, needsRegistration: Boolean = false): ModelIdentifierPerModes{
            if (needsRegistration){
                registerIdWithModalLoading(modelId)
            }
            modeMap[ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND] = modelId
            modeMap[ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND] = modelId
            modeMap[ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND] = modelId
            modeMap[ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND] = modelId
            return this
        }

        fun getIdFromMode(mode: ModelTransformation.Mode): ModelIdentifier{
            return modeMap[mode] ?: defaultId
        }

        companion object{
            fun registerIdWithModalLoading(id: ModelIdentifier){
                ModelLoadingRegistry.INSTANCE.registerModelProvider { _: ResourceManager?, out: Consumer<Identifier?> ->
                    out.accept(id)

                }
            }
        }
    }

    class CustomItemEntityModelLoader(private val layer: EntityModelLayer, private val classType: Class<out Model>){

        private lateinit var model: Model

        fun reload(){
            model = internalReload()
        }

        fun getModel(): Model{
            if (!this::model.isInitialized){
                model = internalReload()
            }
            return model
        }

        private fun internalReload(): Model{
            val constructor = classType.getConstructor(ModelPart::class.java)
            val modelPart = entityModelLoader.getModelPart(layer)
            return constructor.newInstance(modelPart)
        }
    }
}