@file:Suppress("REDUNDANT_ELSE_IN_WHEN")

package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.max

object ScepterObject: AugmentDamage {

    private val scepters: MutableMap<Int,MutableMap<String,Long>> = mutableMapOf()
    private val augmentStats: MutableMap<String,AugmentDatapoint> = mutableMapOf()
    private val bookOfLoreListT1: MutableList<String> = mutableListOf()
    private val bookOfLoreListT2: MutableList<String> = mutableListOf()
    private val bookOfLoreListT12: MutableList<String> = mutableListOf()
    private val activeAugment: MutableMap<Int,String> = mutableMapOf()
    private val augmentApplied: MutableMap<Int,Int> = mutableMapOf()
    private val persistentEffect: MutableMap<Int,PersistentEffectData> = mutableMapOf()
    private val persistentEffectNeed: MutableMap<Int,Int> = mutableMapOf()
    private var lastActiveEnchant = ""

    fun initializeScepter(stack: ItemStack, world: World){
        val id : Int
        val scepterNbt = stack.orCreateNbt
        if (!scepterNbt.contains(NbtKeys.SCEPTER_ID.str())){
            id = world.getNewId()
            writeNbt(NbtKeys.SCEPTER_ID.str(),id,scepterNbt)
        } else {
            id = readNbt(NbtKeys.SCEPTER_ID.str(),scepterNbt)
        }
        if(!scepters.containsKey(id)){
            scepters[id] = mutableMapOf()
        }
        if(!scepterNbt.contains(NbtKeys.ACTIVE_ENCHANT.str())){
            ScepterItem.writeDefaultNbt(NbtKeys.ACTIVE_ENCHANT.str(),scepterNbt)
            if(EnchantmentHelper.getLevel(RegisterEnchantment.MAGIC_MISSILE,stack) == 0) {
                stack.addEnchantment(RegisterEnchantment.MAGIC_MISSILE,1)
            }
        }
        activeAugment[id] = readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(),scepterNbt)
        augmentApplied[id] = -1
        if(scepters[id]?.isNotEmpty() == true){ //break out of initialization if the scepter has already been initialized and isn't changed
            if (scepters[id]!!.size == stack.enchantments.size){
                return
            }
        }
        for (nbtEl in stack.enchantments){ //iterate through all the enchantments stored on the items as nbt (all of them lol)
            val nbt = nbtEl as NbtCompound
            val identifier = EnchantmentHelper.getIdFromNbt(nbt)
            if (identifier != null) {
                val stringId = identifier.path
                if(scepters[id]?.containsKey(stringId) == true) continue
                scepters[id]?.put(stringId, world.time-1000000L)
            }
        }
    }

    fun useScepter(activeEnchantId: String, stack: ItemStack, user: PlayerEntity, world: World): Int?{
        if (world !is ServerWorld){return null}
        val scepterNbt = stack.orCreateNbt
        if (!scepterNbt.contains(NbtKeys.SCEPTER_ID.str())){
            initializeScepter(stack,world)
        }
        val id = readNbt(NbtKeys.SCEPTER_ID.str(),scepterNbt)
        if(!scepters.containsKey(id)){
            initializeScepter(stack,world)
        }
        if(scepters[id]!!.containsKey(activeEnchantId)){
            val cooldown = augmentStats[activeEnchantId]?.cooldown
            val time = user.world.time
            //println("$activeEnchantId last cast ${scepters[id]!![activeEnchantId]} with cooldown $cooldown, now cast at $time")
            if (cooldown != null && scepters[id]!![activeEnchantId] != null){
                return if (time - cooldown >= scepters[id]!![activeEnchantId] as Long){ //checks that enough time has passed since last usage
                    scepters[id]!![activeEnchantId] = user.world.time
                    incrementScepterStats(scepterNbt,activeEnchantId)
                    cooldown
                } else {
                    null
                }
            }
        } else {
            initializeScepter(stack,world)
            //println("new enchant initialized with cooldown: ${augmentStats[activeEnchantId]?.cooldown}")
            incrementScepterStats(scepterNbt,activeEnchantId)
            return augmentStats[activeEnchantId]?.cooldown
        }

        return null
    }

    fun checkManaCost(cost: Int, stack: ItemStack, world: World, user: PlayerEntity): Boolean{
        return (checkCanUseHandler(stack, world, user, cost, ""))
    }

    fun applyManaCost(cost: Int, stack: ItemStack, world: World, user: PlayerEntity){
        damageHandler(stack,world,user,cost,"")
    }

    private fun incrementScepterStats(scepterNbt: NbtCompound, activeEnchantId: String){
        val spellKey = augmentStats[activeEnchantId]?.type?.name ?: return
        if(spellKey == SpellType.NULL.name) return
        val statLvl = readNbt(spellKey + "_lvl",scepterNbt)
        val statXp = readNbt(spellKey + "_xp",scepterNbt) + 1
        writeNbt(spellKey + "_xp",statXp,scepterNbt)
        if(checkXpForLevelUp(statXp,statLvl)){
            writeNbt(spellKey + "_lvl",statLvl+1,scepterNbt)
        }
    }



    fun getScepterStat(scepterNbt: NbtCompound, activeEnchantId: String): Pair<Int,Int>{
        val spellKey = augmentStats[activeEnchantId]?.type?.name ?: return Pair(1,0)
        val statLvl = readNbt(spellKey + "_lvl",scepterNbt)
        val statXp = readNbt(spellKey + "_xp",scepterNbt)
        return Pair(statLvl,statXp)
    }

    fun checkScepterStat(scepterNbt: NbtCompound, activeEnchantId: String): Boolean{
        if (!augmentStats.containsKey(activeEnchantId)) return false
        val minLvl = augmentStats[activeEnchantId]?.minLvl?:return false
        val curLvl = getScepterStat(scepterNbt,activeEnchantId).first
        return (curLvl >= minLvl)
    }

    fun updateScepterActiveEnchant(stack: ItemStack, user: PlayerEntity, up: Boolean){
        if (stack.item !is ScepterItem) return
        var nbt = stack.orCreateNbt
        if (!nbt.contains(NbtKeys.SCEPTER_ID.str())){
            initializeScepter(stack,user.world)
        }
        val id = readNbt(NbtKeys.SCEPTER_ID.str(),nbt)
        if(!scepters.containsKey(id)){
            initializeScepter(stack,user.world)
        }
        if (!nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())){
            initializeScepter(stack,user.world)
        }
        nbt = stack.orCreateNbt
        val activeEnchant = if (augmentApplied[id] == -1) {
            readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
        } else {
            activeAugment[id]?:"magic_missile"
        }
        if(scepters[id]?.isEmpty() == true){
            initializeScepter(stack, user.world)
        }
        if (scepters[id]!!.size != stack.enchantments.size){
            initializeScepter(stack, user.world)
        }
        val nbtEls = stack.enchantments
        var matchIndex = 0
        val augEls: MutableList<Int> = mutableListOf()
        for (i in 0..nbtEls.lastIndex){
            val identifier = EnchantmentHelper.getIdFromNbt(nbtEls[i] as NbtCompound)
            val enchantCheck = Registry.ENCHANTMENT.get(identifier)?: RegisterEnchantment.DECAYED
            if(enchantCheck is ScepterAugment) {
                augEls.add(i)
            }
            if (activeEnchant == identifier?.path){
                matchIndex = i
            }
        }
        val newIndex = if (augEls.size != 0) {
            val augElIndex = augEls.indexOf(matchIndex)
            if (augElIndex == (augEls.lastIndex) && up) {
                augEls[0]
            } else if (augElIndex == 0 && !up) {
                augEls[augEls.lastIndex]
            } else if (up) {
                augEls[augElIndex + 1]
            } else {
                augEls[augElIndex - 1]
            }
        } else {
            0
        }
        val nbtTemp = nbtEls[newIndex] as NbtCompound
        val newActiveEnchant = EnchantmentHelper.getIdFromNbt(nbtTemp)?.path
        activeAugment[id] = newActiveEnchant?: return
        val tempActiveEnchant = activeAugment[id]
        val currentTime = user.world.time
        val lastUsed: Long = if(scepters[id]?.containsKey(tempActiveEnchant) == true){scepters[id]!![tempActiveEnchant]?:currentTime} else{currentTime}
        val timeSinceLast = currentTime - lastUsed
        val cooldown = augmentStats[tempActiveEnchant]?.cooldown?:20
        if(timeSinceLast >= cooldown){
            augmentApplied[id] = 0
        } else{
            augmentApplied[id] = (cooldown.toLong() - timeSinceLast).toInt()
        }
        val message = "New active spell: " + TranslatableText("enchantment.amethyst_imbuement.$newActiveEnchant").string
        MinecraftClient.getInstance().player?.sendChatMessage(message)
    }

    fun activeEnchantHelper(stack: ItemStack, activeEnchant: String): String{
        val nbt: NbtCompound = stack.nbt?:return activeEnchant
        val id: Int = nbtChecker(nbt)?: return activeEnchant
        return if(!activeAugment.containsKey(id)){
            activeEnchant
        } else {
            return if (augmentApplied[id]==-1){
                activeEnchant
            } else if(activeAugment[id] != null) {
                var nxt: String = activeEnchant
                activeAugment[id]?.let { nxt = it }
                lastActiveEnchant = nxt
                augmentApplied[id] = -1
                writeStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nxt, nbt)
                nxt
            } else {
                activeEnchant
            }
        }
    }

    fun resetCooldown(stack: ItemStack, activeEnchantId: String){
        val nbt = stack.nbt?: return
        val id: Int = nbtChecker(nbt)?:return
        if(!scepters.containsKey(id)) return
        if (scepters[id]?.isEmpty() == true) return
        if(scepters[id]?.containsKey(activeEnchantId) != true) return
        val cd = augmentStats[activeEnchantId]?.cooldown?:20
        scepters[id]?.set(activeEnchantId, (scepters[id]?.get(activeEnchantId) ?: 0) - cd - 2)
    }

    fun shouldRemoveCooldownChecker(id: Int): Int{
        val aa = augmentApplied.getOrDefault(id,-1)
        if(aa > 0){
            augmentApplied[id] = -2
        }
        return aa
        }

    fun persistentEffectTicker(id: Int){
        val curTick = persistentEffectNeed[id]?:return
        val delay = persistentEffect[id]?.delay?:return
        if (curTick >= delay){
            val aug = persistentEffect[id]?.augment?:return
            val world = persistentEffect[id]?.world?:return
            val user = persistentEffect[id]?.user?:return
            val blockPos = persistentEffect[id]?.blockPos?:return
            val entityList = persistentEffect[id]?.entityList?:return
            val level = persistentEffect[id]?.level?:return
            aug.persistentEffect(world, user,blockPos, entityList, level)
            if(persistentEffect[id]?.duration != null){
                persistentEffect[id]!!.duration -= delay
                if (persistentEffect[id]!!.duration <= 0){
                    persistentEffectNeed[id] = -1
                    return
                } else {
                    persistentEffectNeed[id] = 0
                }
            }

        } else {
            persistentEffectNeed[id] = curTick + 1
            return
        }
    }

    fun getPersistentTickerNeed(id:Int): Boolean{
        val chk = persistentEffectNeed[id]?:-1
        return (chk >= 0)
    }
    fun setPersistentTickerNeed(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int,blockPos: BlockPos, augment: MiscAugment, delay: Int, duration: Int){
        val stack = user.getStackInHand(Hand.MAIN_HAND)
        val nbt = stack.orCreateNbt
        val id = nbtChecker(nbt)?:return
        persistentEffect[id] = PersistentEffectData(world,user,entityList,level,blockPos,augment,delay,duration)
        persistentEffectNeed[id] = 0
    }

    fun scepterTickNbtCheck(stack: ItemStack): Int{
        val nbt = stack.nbt ?: return -1
        return nbtChecker(nbt)?:return -1
    }

    fun transferNbt(stack1: ItemStack,stack2: ItemStack){
        val nbt1 = stack1.nbt ?: return
        val nbt2 = stack2.orCreateNbt
        for(nbtKey in nbt1.keys){
            if(nbtKey == ItemStack.ENCHANTMENTS_KEY){
                continue
            }
            nbt2.put(nbtKey,nbt1[nbtKey])
        }
    }

    fun bookOfLoreNbtGenerator(tier:LoreTier = LoreTier.ANY_TIER): NbtCompound{
        val nbt = NbtCompound()
        val aug =  when (tier){
            LoreTier.ANY_TIER->{
                getRandBookOfLoreAugment(bookOfLoreListT12)
            }
            LoreTier.LOW_TIER->{
                getRandBookOfLoreAugment(bookOfLoreListT1)
            }
            LoreTier.HIGH_TIER->{
                getRandBookOfLoreAugment(bookOfLoreListT2)
            }
            else->{
                "magic_missile"
            }
        }
        nbt.putString(NbtKeys.LORE_KEY.str(),aug)
        return nbt
    }

    fun registerAugmentStat(id: String, type: SpellType,cooldown: Int, cost: Int, minLvl: Int = 1, bookOfLoreTier: Int = 0, keyItem: Item = Items.GOLD_INGOT){
        if(!augmentStats.containsKey(id)){
            augmentStats[id] = AugmentDatapoint(type,cooldown,cost, minLvl, bookOfLoreTier, keyItem)
        }
        if (bookOfLoreTier > 0) {
            if (!bookOfLoreListT12.contains(id)){
                bookOfLoreListT12.add(id)
            }
        }
        if (bookOfLoreTier == 1) {
            if (!bookOfLoreListT1.contains(id)){
                bookOfLoreListT1.add(id)
            }
        } else if (bookOfLoreTier == 2) {
            if (!bookOfLoreListT2.contains(id)){
                bookOfLoreListT2.add(id)
            }
        }
    }

    fun getAugmentType(id: String): SpellType {
        if(!augmentStats.containsKey(id)) return SpellType.NULL
        return augmentStats[id]?.type?:SpellType.NULL
    }

    fun getAugmentItem(id: String): Item {
        if(!augmentStats.containsKey(id)) return Items.GOLD_INGOT
        return augmentStats[id]?.keyItem?:Items.GOLD_INGOT
    }

    fun getAugmentMinLvl(id: String): Int {
        if(!augmentStats.containsKey(id)) return 1
        return augmentStats[id]?.minLvl?:1
    }

    fun getAugmentManaCost(id: String, reduction: Int = 0): Int{
        if(!augmentStats.containsKey(id)) return (10 - reduction)
        val cost = (augmentStats[id]?.cost?.minus(reduction)) ?: (10 - reduction)
        return max(1,cost)
    }

    fun setLastActiveEnchant(newActiveEnchant: String){
        lastActiveEnchant = newActiveEnchant
    }

    fun xpToNextLevel(xp: Int,lvl: Int): Int{
        val xpNext = (2 * lvl * lvl + 40 * lvl)
        return (xpNext - xp + 1)
    }

    fun spawnProjectileEntity(world: World, entity: LivingEntity, projectile: ProjectileEntity, soundEvent: SoundEvent): Boolean{
        val bl = world.spawnEntity(projectile)
        world.playSound(
            null,
            entity.blockPos,
            soundEvent,
            SoundCategory.PLAYERS,
            1.0f,
            world.getRandom().nextFloat() * 0.4f + 0.8f)
        return bl
    }



    private data class AugmentDatapoint(val type: SpellType,val cooldown: Int,
                                        val cost: Int, val minLvl: Int,
                                        val bookOfLoreTier: Int, val keyItem: Item){
        //hi
    }
    private data class PersistentEffectData(val world: World, val user: LivingEntity,
                                            val entityList: MutableList<Entity>, val level: Int, val blockPos: BlockPos,
                                            val augment: MiscAugment, var delay: Int, var duration: Int){
        //hi
    }
    private fun nbtChecker(nbt: NbtCompound): Int?{
        return if (!nbt.contains(NbtKeys.SCEPTER_ID.str())){
            null
        }else{
            readNbt(NbtKeys.SCEPTER_ID.str(),nbt)
        }
    }
    private fun writeNbt(key: String, input: Int, nbt: NbtCompound){
        nbt.putInt(key,input)
    }
    private fun writeStringNbt(key: String, input: String, nbt: NbtCompound){
        nbt.putString(key,input)
    }
    private fun readNbt(key: String, nbt: NbtCompound): Int {
        return nbt.getInt(key)
    }
    private fun readStringNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }
    private fun World.getNewId():Int{
        return (random.nextFloat() * 1000000000.0F).toInt()
    }
    private fun checkXpForLevelUp(xp:Int,lvl:Int): Boolean{
        return (xp > (2 * lvl * lvl + 40 * lvl))
    }
    private fun getRandBookOfLoreAugment(list: MutableList<String>): String{
        if (list.isEmpty()) return "magic_missile"
        val rndMax = list.size
        val rndIndex = MinecraftClient.getInstance().world?.random?.nextInt(rndMax) ?: return "magic_missile"
        return list[rndIndex]
    }

}


