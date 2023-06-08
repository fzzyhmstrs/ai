@file:Suppress("PrivatePropertyName")

package me.fzzyhmstrs.amethyst_imbuement.renderer

import com.google.common.collect.ImmutableMap
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.model.CrystallineGolemEntityModel
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class BaseHamsterArmorFeatureRenderer(context: FeatureRendererContext<out BaseHamsterEntity, out BaseHamsterEntityModel>)
: 
FeatureRenderer<out BaseHamsterEntity, out BaseHamsterEntityModel>(context) 
{

    object HamsterArmorTextureIdsHolder: SimpleSynchronousResourceReloadListener {
        
        private var hamsterTextures: Map<String,Identifier> = mapOf()
        private val fallback = Identifier(AI.MOD_ID, )
        
        fun getHamsterTexture(stack: ItemStack): Identifier{
            val item = stack.item
            if (item is ArmorItem){
                val armorStr = item.material.name
                return hamsterTextures.getOrDefault(armorStr) {fallback}
            }
            return fallback
        }
        
        private fun loadHamsterArmorIds(manager: ResourceManager){
            val map: MutableMap<String, Identifier> = mutableMapOf()
            resourceManager.findResources("hamster_armor_textures") { path -> path.path.endsWith(".json") }
            .forEach { (id,resource) ->
                try{
                    val reader = resource.reader
                    val json = JsonParser.parseReader(reader).asJsonObject
                    val jsonTextures = json.get("textures")
                    if (jsonTextures != null){
                        if (jsonTextures.isJsonObject){
                            for (jsonEntry in jsonTextures.asJsonObject().entrySet()){
                                val jsonArmorStr = jsonEntry.key
                                val jsonHamsterStr = jsonEntry.value
                                if (!jsonHamsterStr.isJsonPrimitive){
                                    println("Hamster texture ID $jsonHamsterStr isn't properly formatted as a string in hamster texture file $id")
                                    continue
                                }
                                val hamsterStr = jsonHamsterStr.asString
                                val hamsterStrId = Identifier.tryParse(hamsterStr)
                                if (hamsterStrId == null){
                                    println("Hamster texture ID $jsonHamsterStr isn't a valid Identifier in hamster texture file $id")
                                    continue
                                }
                                map[jsonArmorStr] = hamsterStrId
                            }
                        
                        } else {
                            println("Hamster texture file $id isn't formatted properly as a JsonObject")
                        }
                    } else {
                        println("Hamster texture file $id doesn't have required element 'textures'")
                    }
                } catch (e: Exception){
                    println("Error while loading hamster texture file $id!")
                    e.printStackTrace()
                }
            }
            hamsterTextures = map
        }
        
        override fun reload(manager: ResourceManager) {
            loadHamsterArmorIds(manager)
        }

        override fun getFabricId(): Identifier {
            return Identifier(AI.MOD_ID,"hamster_armor_loader")
        }
        
        fun registerAll(){
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this)
        }
    }
  
    override fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int,
        hamster: BaseHamsterEntity,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        if (hamster.isInvisible) {
            return
        }

        renderModel(
            this.contextModel,
            identifier,
            matrixStack,
            vertexConsumerProvider,
            i,
            crystalGolemEntity,
            1.0f,
            1.0f,
            1.0f
        )
    }
}
