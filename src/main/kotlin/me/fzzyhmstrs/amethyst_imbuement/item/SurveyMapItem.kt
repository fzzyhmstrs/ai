package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*

class SurveyMapItem(settings: Item.Settings): Item(settings) {
  
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val nbt = stack.orCreateNbt
        if (nbt.contains("map_origin")) {
            val pos = Nbt.readBlockPos("map_origin",nbt)
            tooltip.add(TranslatableText("map.survey.origin",pos.x,pos.y,pos.z).formatted(Formatting.ITALIC))
        }
    }
    
    
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val nbt = stack.orCreateNbt
        val id = Nbt.getItemStackId(stack)
        if (id == -1L) return TypedActionResult.fail(stack)
        val results = surveyResults[id]?: return TypedActionResult.fail(stack)
        /////////
        return TypedActionResult.fail(stack)
    }
  
  
    companion object{
        private val surveyResults: MutableMap<Long, SurveyResults> = mutableMapOf()
        private val surveySprites: MutableMap<String,SurveySprite> = mutableMapOf()
        private val FALLBACK = SurveySprite("fallback",SurveySpriteProvider(0,0,8,8))
        
        fun buildMap(id: Long, world: World, startPos: BlockPos, mapStack: ItemStack){}
        
        class SurveyResults(){
            var terrainData: Map<SpritePos,SurveySprite> = mapOf()
            var structureData: Map<SpritePos,SurveySprite> = mapOf()
            
            fun buildResults(world: World, startPos: BlockPos){
                for (i in -15..16){
                    for (j in -15..16){
                        
                    
                    }
                }
                buildStructureResults(world, startPos)
            }
            
            fun toNbt(stack: ItemStack){
                val nbt = stack.orCreateNbt
                val list = NbtList()
                for (entry in terrainData){
                    val compound = NbtCompound()
                    Nbt.writeIntNbt("x",entry.key.x,compound)
                    Nbt.writeIntNbt("y",entry.key.y,compound)
                    Nbt.writeStringNbt("id",entry.value.id,compound)
                    list.add(compound)
                }
                nbt.put("terrain",list)
                val list2 = NbtList()
                for (entry in structureData){
                    val compound = NbtCompound()
                    Nbt.writeIntNbt("x",entry.key.x,compound)
                    Nbt.writeIntNbt("y",entry.key.y,compound)
                    Nbt.writeStringNbt("id",entry.value.id,compound)
                    list2.add(compound)
                }
                nbt.put("structures",list2)
            }
            
            fun fromNbt(stack: ItemStack){
                val nbt = stack.orCreateNbt
                if (nbt.contains("terrain")){
                    val list: NbtList = nbt.getList("terrain",10)
                    val tempTerrainMap: MutableMap<SpritePos,SurveySprite> = mutableMapOf()
                    list.forEach{
                        val compound = it as NbtCompound
                        val x = Nbt.readIntNbt("x",compound)
                        val y = Nbt.readIntNbt("y",compound)
                        val id = Nbt.readStringNbt("id",compound)
                        val pos = SpritePos(x,y)
                        val sprite = surveySprites[id]?:FALLBACK
                        tempTerrainMap[pos] = sprite
                    }
                    terrainData = tempTerrainMap
                }
                if (nbt.contains("structures")){
                    val list: NbtList = nbt.getList("structures",10)
                    val tempStructuresMap: MutableMap<SpritePos,SurveySprite> = mutableMapOf()
                    list.forEach{
                        val compound = it as NbtCompound
                        val x = Nbt.readIntNbt("x",compound)
                        val y = Nbt.readIntNbt("y",compound)
                        val id = Nbt.readStringNbt("id",compound)
                        val pos = SpritePos(x,y)
                        val sprite = surveySprites[id]?:FALLBACK
                        tempStructuresMap[pos] = sprite
                    }
                    structureData = tempStructuresMap
                }
            
            }
            
            private fun buildStructureResults(world: World, startPos: BlockPos){}
        
        }
        
        class SurveySprite(val id: String, provider: SurveySpriteProvider){
            
            private val spritesList: MutableList<SurveySpriteProvider> = mutableListOf(provider)
            
            fun getSPriteProvider(x: Int, y: Int): SurveySpriteProvider{
                val random = Random((x + 32* y).toLong())
                val index = random.nextInt(spritesList.size)
                val provider = spritesList[index]
                return provider.bake(x,y)
            }
            fun withSpriteProvider(u: Int, v: Int): SurveySprite{
                spritesList.add(SurveySpriteProvider(u,v,8,8))
                return this
            }
            fun withSpriteProvider(u: Int, v: Int,  w: Int, h: Int): SurveySprite{
                spritesList.add(SurveySpriteProvider(u,v,w,h))
                return this
            }
            fun withSpriteProvider(u: Int, v: Int,  w: Int, h: Int, hOff: Int): SurveySprite{
                spritesList.add(SurveySpriteProvider(u,v,w,h,hOff))
                return this
            }
            fun register(){
                surveySprites[id] = this
            }
            
        }
        
        class SurveySpriteProvider(val u: Int, val v: Int, val w: Int, val h: Int, private val hOff: Int = 0){
            private val xOff = w/2
            private val yOff = h/2
            
            var texX = 0
            var texY = 0
            
            fun bake(x: Int, y: Int): SurveySpriteProvider{
                getSpriteTexXY(x,y,256,256)
                return this
            }
            
            private fun getSpriteTexXY(x: Int, y: Int, width: Int, height: Int){
                val x1 = x - xOff
                if (x1 < 0){
                    texX = 0
                } else {
                    val x2 = x + xOff
                    if (x2 > width){
                        texX = width - w
                    } else {
                        texX = x1
                    }
                }
                val y1 = y - yOff + hOff
                if (y1 < 0){
                    texY = 0
                } else {
                    val y2 = y + yOff - hOff
                    if (y2 > height){
                        texY = height - h
                    } else {
                        texY = y1
                    }
                }
            }
        }
        
        class SpritePos(val x: Int, val y: Int): Comparable<SpritePos>{
            private val spriteIndex = x + 256 * y
            
            override fun compareTo(other: SpritePos): Int{
                val otherIndex = other.spriteIndex
                return spriteIndex - otherIndex
            }
        
        }
    }
}
