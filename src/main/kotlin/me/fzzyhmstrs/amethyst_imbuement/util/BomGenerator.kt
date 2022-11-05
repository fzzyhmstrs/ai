package me.fzzyhmstrs.amethyst_imbuement.util

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.screen.slot.Slot
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry

object BomGenerator {

    fun generate(slots: DefaultedList<Slot>): Bom {
        val map = Int2ObjectOpenHashMap<IdStack>(50,0.75f)
        val list = mutableListOf<Int>()
        for (i in 0 until slots.size) {
            val slot = slots[i]
            val stack = slot.stack

            if (stack.isEmpty) continue
            val id = Registry.ITEM.getRawId(stack.item)
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
            println("index: $index")
            var found = false
            for (it in ids.listIterator()) {
                println("id to match: $it")
                if (slotBom.ids.contains(it)){
                    for ((slot,stack) in sortedSlots) {
                        println("slot to check: $slot")
                        println("stack to check: $stack")
                        if (stack.id == it){
                            println(">>>>>>>>>>>>>>>> index: $index, slot: $slot, item id: $it")
                            val itemsUsed = usedItems.getOrDefault(slot,-1)
                            println("Items used so far: $usedItems")
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

    fun truncate(input: Int2IntOpenHashMap): Int2IntOpenHashMap{
        val truncated = Int2IntOpenHashMap(input)
        input.forEach { (key,slot) ->
            if (key == slot){
                truncated.remove(key)
            }
        }
        return truncateAfterTrim(truncated)
    }
    private fun truncateAfterTrim(input: Int2IntOpenHashMap): Int2IntOpenHashMap{
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