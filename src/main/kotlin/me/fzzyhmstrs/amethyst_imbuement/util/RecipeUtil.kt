package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntList
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.*

object RecipeUtil {

    private val playerFavoritesMap: MutableMap<UUID,List<ItemStack>> = mutableMapOf()

    val FAVORITES_CHECK = Identifier(AI.MOD_ID,"faves_check")
    val FAVORITES_SEND = Identifier(AI.MOD_ID,"faves_send")
    val FAVORITES_SAVE = Identifier(AI.MOD_ID,"faves_save")

    fun registerServer(){
        ServerPlayNetworking.registerGlobalReceiver(FAVORITES_CHECK){server,player,_,buf,_ ->
            val uuid = buf.readUuid()
            val sentBuf = PacketByteBufs.create()
            if (playerFavoritesMap.containsKey(uuid)){
                val list = playerFavoritesMap.getOrDefault(uuid, listOf())
                if (list.isEmpty()){
                    sentBuf.writeBoolean(false)
                    server.execute {
                        ServerPlayNetworking.send(player, FAVORITES_SEND, sentBuf)
                    }
                } else {
                    sentBuf.writeBoolean(true)
                    sentBuf.writeShort(list.size)
                    list.forEach {
                        sentBuf.writeItemStack(it)
                    }
                    server.execute {
                        ServerPlayNetworking.send(player, FAVORITES_SEND, sentBuf)
                    }
                }
            } else {
                sentBuf.writeBoolean(false)
                server.execute {
                    ServerPlayNetworking.send(player, FAVORITES_SEND, sentBuf)
                }
            }

        }
        ServerPlayNetworking.registerGlobalReceiver(FAVORITES_SAVE){_,_,_,buf,_ ->
            val uuid = buf.readUuid()
            val list: MutableList<ItemStack> = mutableListOf()
            val size = buf.readShort()
            for (i in 1..size){
                list.add(buf.readItemStack())
            }
            playerFavoritesMap[uuid] = list
        }

        ResourceConditions.register(Identifier(AI.MOD_ID,"enabled")) {json ->
            try{
                AiConfig.resources.isEnabled(json.getAsJsonPrimitive("id").asString)
            } catch (e: Exception){
                false
            }
        }
    }

    fun hasFavorites(uuid: UUID): Boolean{
        return playerFavoritesMap.containsKey(uuid)
    }

    fun getFavorites(uuid: UUID): List<ItemStack>{
        return playerFavoritesMap[uuid]?: listOf()
    }

    fun setFavorites(uuid: UUID, list: List<ItemStack>){
        playerFavoritesMap[uuid] = list
    }

    internal fun buildOutputProvider(recipe: ImbuingRecipe): StackProvider{
        val augment = recipe.getAugment()
        if (augment != ""){
            val stack = ItemStack(Items.ENCHANTED_BOOK)
            val enchant = Registries.ENCHANTMENT.get(Identifier(augment))?:return EmptyStackProvider()
            EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(enchant,1))
            return SingleStackProvider(stack)
        }
        val output = recipe.getOutput()
        return SingleStackProvider(output)
    }

    internal fun buildInputProviders(recipe: ImbuingRecipe): List<StackProvider>{
        val list: MutableList<StackProvider> = mutableListOf()
        recipe.getInputs().forEachIndexed { index, it ->
            if (index == 6 && recipe.getAugment() != ""){
                val ingredient = recipe.getCenterIngredient()
                list.add(StackProvider.getProvider(ingredient))
            } else {
                list.add(StackProvider.getProvider(it))
            }
        }
        return list
    }

    internal class MultiStackProvider(private val stacks: Array<ItemStack>): StackProvider{
        var timer = -1L
        var currentIndex = 0

        override fun getStack(): ItemStack {
            val time = System.currentTimeMillis()
            if (timer == -1L){
                timer = time
            } else if (time - timer >= 1000L){
                currentIndex++
                if (currentIndex >= stacks.size){
                    currentIndex = 0
                }
                timer = time
            }
            return stacks[currentIndex]
        }
    }
    internal class SingleStackProvider(private val stack: ItemStack): StackProvider{
        override fun getStack(): ItemStack {
            return stack
        }

    }
    internal class EmptyStackProvider: StackProvider{
        override fun getStack(): ItemStack {
            return ItemStack.EMPTY
        }
    }
    internal interface StackProvider{
        fun getStack(): ItemStack
        companion object{
            fun getProvider(ingredient: Ingredient): StackProvider{
                if (ingredient.isEmpty){
                    return EmptyStackProvider()
                }
                val stacks = ingredient.matchingStacks
                if (stacks.size == 1){
                    return SingleStackProvider(stacks[0])
                } else if (stacks.isEmpty()){
                    return EmptyStackProvider()
                }
                return MultiStackProvider(stacks)
            }
        }
    }

    fun ingredientFromJson(json: JsonObject?): Ingredient{
        return if (json == null) Ingredient.EMPTY else Ingredient.fromJson(json)
    }

    fun generate(slots: DefaultedList<Slot>): Bom {
        val map = Int2ObjectOpenHashMap<IdStack>(50,0.75f)
        val list = mutableListOf<Int>()
        for (i in 0 until slots.size) {
            val slot = slots[i]
            val stack = slot.stack

            if (stack.isEmpty) continue
            val id = Registries.ITEM.getRawId(stack.item)
            map[i] = IdStack(id,stack.count)
            list.add(id)
        }
        return Bom(map, IntList.of(*list.toIntArray()))
    }

    fun test(slotBom: Bom, recipeBom: Int2ObjectOpenHashMap<IntList>): Result {
        val sortedBom = recipeBom.toSortedMap()
        val sortedSlots = slotBom.slots.toSortedMap()
        //map of slot -> how many used from that slot
        val usedItems = Int2IntOpenHashMap()
        val matchedItems = Int2IntOpenHashMap()
        var passed = true
        sortedBom.forEach { (index, ids) ->
            var found = false
            for (it in ids.listIterator()) {
                if (slotBom.ids.contains(it)){
                    for ((slot,stack) in sortedSlots) {
                        if (stack.id == it){
                            val itemsUsed = usedItems.getOrDefault(slot,-1)
                            if (itemsUsed == -1){
                                usedItems[slot] = 1
                                matchedItems[index] = slot
                                found = true
                                break
                            } else {
                                if (itemsUsed < stack.count){
                                    usedItems[slot] = itemsUsed + 1
                                    matchedItems[index] = slot
                                    found = true
                                    break
                                }else{
                                    continue
                                }
                            }
                        }
                    }
                    if (found){
                        break
                    }
                }
            }
            if (!found){
                passed = false
                matchedItems[index] = -1
            }
        }
        return Result(passed,matchedItems)
    }

    fun truncate(input: Int2IntOpenHashMap): Int2IntOpenHashMap {
        val truncated = Int2IntOpenHashMap(input)
        input.forEach { (key,slot) ->
            if (key == slot){
                truncated.remove(key)
            }
        }
        return truncateAfterTrim(truncated)
    }
    private fun truncateAfterTrim(input: Int2IntOpenHashMap): Int2IntOpenHashMap {
        val truncated = Int2IntOpenHashMap(input)
        input.forEach { (key,slot) ->
            input.keys.forEach{ index ->
                if (slot == index){
                    truncated[key] = truncated[index]
                    truncated.remove(index)
                    return truncateAfterTrim(truncated)
                }
            }
        }
        return truncated
    }

    class Bom(val slots: Int2ObjectOpenHashMap<IdStack>, val ids: IntList)

    class Result(val passed: Boolean, val matches: Int2IntOpenHashMap){
        override fun toString(): String {
            return "[passed: $passed | matches: $matches]"
        }
        companion object{
            val EMPTY = Result(false, Int2IntOpenHashMap())
        }
    }

    class IdStack(val id: Int, var count: Int){
        override fun toString(): String {
            return "{id: $id, count: $count}"
        }
    }

}