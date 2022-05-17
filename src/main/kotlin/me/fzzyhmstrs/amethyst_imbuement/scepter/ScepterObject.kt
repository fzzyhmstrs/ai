@file:Suppress("REDUNDANT_ELSE_IN_WHEN")

package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentModifier
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentDamage
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*
import kotlin.math.max

object ScepterObject: AugmentDamage {

    private val scepters: MutableMap<Int,MutableMap<String,Long>> = mutableMapOf()
    private val augmentStats: MutableMap<String, AugmentDatapoint> = mutableMapOf()
    private val bookOfLoreListT1: MutableList<String> = mutableListOf()
    private val bookOfLoreListT2: MutableList<String> = mutableListOf()
    private val bookOfLoreListT12: MutableList<String> = mutableListOf()
    private val activeAugment: MutableMap<Int,String> = mutableMapOf()
    private val augmentApplied: MutableMap<Int,Int> = mutableMapOf()
    private val persistentEffect: MutableMap<Int, PersistentEffectData> = mutableMapOf()
    private val persistentEffectNeed: MutableMap<Int,Int> = mutableMapOf()
    const val fallbackAugment = AI.MOD_ID+":magic_missile"
    private val SCEPTER_SYNC_PACKET = Identifier(AI.MOD_ID,"scepter_sync_packet")

    fun initializeScepter(stack: ItemStack, world: World){
        val id : Int
        val scepterNbt = stack.orCreateNbt
        if (!scepterNbt.contains(NbtKeys.SCEPTER_ID.str())){
            var idTemp = world.getNewId()
            while (scepters.containsKey(idTemp)){
                idTemp = world.getNewId()
            }
            id = idTemp
            writeNbt(NbtKeys.SCEPTER_ID.str(),id,scepterNbt)
        } else {
            id = readNbt(NbtKeys.SCEPTER_ID.str(),scepterNbt)
        }
        if(!scepters.containsKey(id)){
            scepters[id] = mutableMapOf()
        }
        if(!scepterNbt.contains(NbtKeys.ACTIVE_ENCHANT.str())){
            ScepterItem.writeDefaultNbt(stack)
            if(EnchantmentHelper.getLevel(RegisterEnchantment.MAGIC_MISSILE,stack) == 0) {
                stack.addEnchantment(RegisterEnchantment.MAGIC_MISSILE,1)
            }
        }
        activeAugment[id] = readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(),scepterNbt)
        augmentApplied[id] = -1
        if(scepters[id]?.isNotEmpty() == true){ //break out of initialization if the scepter has already been initialized and isn't changed
            if (scepters[id]?.size == stack.enchantments.size){
                return
            }
        }
        for (nbtEl in stack.enchantments){ //iterate through all the enchantments stored on the items as nbt (all of them lol)
            val nbt = nbtEl as NbtCompound
            val identifier = EnchantmentHelper.getIdFromNbt(nbt)
            if (identifier != null) {
                val stringId = identifier.toString()
                if(scepters[id]?.containsKey(stringId) == true) continue
                scepters[id]?.put(stringId, world.time-1000000L)
            }
        }
    }

    fun useScepter(activeEnchantId: String, stack: ItemStack, user: PlayerEntity, world: World, cdMod: Int = 0): Int?{
        if (world !is ServerWorld){return null}
        val scepterNbt = stack.orCreateNbt
        if (!scepterNbt.contains(NbtKeys.SCEPTER_ID.str())){
            initializeScepter(stack,world)
        }
        val id = readNbt(NbtKeys.SCEPTER_ID.str(),scepterNbt)
        if(!scepters.containsKey(id)){
            initializeScepter(stack,world)
        }
        if(scepters[id]?.containsKey(activeEnchantId) == true){
            val enchantCheck = Registry.ENCHANTMENT.get(Identifier(activeEnchantId))?:return null
            if (EnchantmentHelper.getLevel(enchantCheck,stack) == 0){
                fixActiveEnchantWhenMissing(stack, world)
                return null
            }
            val cooldown = augmentStats[activeEnchantId]?.cooldown?.plus(cdMod)
            val time = user.world.time
            val lastUsed = scepters[id]?.get(activeEnchantId)
            if (cooldown != null && lastUsed != null){
                val cooldown2 = max(cooldown,1) // don't let cooldown be less than 1 tick
                return if (time - cooldown2 >= lastUsed){ //checks that enough time has passed since last usage
                    scepters[id]?.put(activeEnchantId, user.world.time)
                    //incrementScepterStats(scepterNbt,activeEnchantId, xpMods)
                    cooldown2
                } else {
                    null
                }
            }
        } else {
            initializeScepter(stack,world)
            //println("new enchant initialized with cooldown: ${augmentStats[activeEnchantId]?.cooldown}")
            //incrementScepterStats(scepterNbt,activeEnchantId, xpMods)
            return augmentStats[activeEnchantId]?.cooldown?.plus(cdMod)
        }

        return null
    }

    fun sendScepterUpdateFromClient(up: Boolean) {
        val buf = PacketByteBufs.create()
        buf.writeBoolean(up)
        ClientPlayNetworking.send(SCEPTER_SYNC_PACKET,buf)
    }

    fun registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(SCEPTER_SYNC_PACKET)
        { _: MinecraftServer,
          serverPlayerEntity: ServerPlayerEntity,
          _: ServerPlayNetworkHandler,
          packetByteBuf: PacketByteBuf,
          _: PacketSender ->
            val stack = serverPlayerEntity.getStackInHand(Hand.MAIN_HAND)
            val up = packetByteBuf.readBoolean()
            updateScepterActiveEnchant(stack,serverPlayerEntity,up)
        }
    }

    private fun updateScepterActiveEnchant(stack: ItemStack, user: PlayerEntity, up: Boolean){
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
        val activeEnchantCheck = if (augmentApplied[id] == -1) {
            readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
        } else {
            activeAugment[id] ?: (fallbackAugment)
        }

        val activeCheck = Registry.ENCHANTMENT.get(Identifier(activeEnchantCheck))
        val activeEnchant = if (activeCheck != null) {
            if (EnchantmentHelper.getLevel(activeCheck, stack) == 0) {
                fixActiveEnchantWhenMissing(stack, user.world)
                readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
            } else {
                activeEnchantCheck
            }
        } else {
            fixActiveEnchantWhenMissing(stack, user.world)
            readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
        }

        if(scepters[id]?.isEmpty() == true){
            initializeScepter(stack, user.world)
        }
        if (scepters[id]?.size != stack.enchantments.size){
            initializeScepter(stack, user.world)
        }

        val nbtEls = stack.enchantments
        var matchIndex = 0
        val augEls: MutableList<Int> = mutableListOf()
        for (i in 0..nbtEls.lastIndex){
            val identifier = EnchantmentHelper.getIdFromNbt(nbtEls[i] as NbtCompound)
            val enchantCheck = Registry.ENCHANTMENT.get(identifier)?: RegisterEnchantment.ATTUNED
            if(enchantCheck is ScepterAugment) {
                augEls.add(i)
            }
            if (activeEnchant == identifier?.toString()){
                matchIndex = i
            }
        }
        val newIndex = if (augEls.size != 0) {
            val augElIndex = if (augEls.indexOf(matchIndex) == -1){
                0
            } else {
                augEls.indexOf(matchIndex)
            }
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
        val newActiveEnchant = EnchantmentHelper.getIdFromNbt(nbtTemp)?.toString()
        activeAugment[id] = newActiveEnchant?: return
        val tempActiveEnchant = activeAugment[id]
        val currentTime = user.world.time
        val lastUsed: Long = if(scepters[id]?.containsKey(tempActiveEnchant) == true)
        {
            scepters[id]?.get(tempActiveEnchant)?:currentTime
        } else {
            currentTime
        }
        val timeSinceLast = currentTime - lastUsed
        val cooldown = augmentStats[tempActiveEnchant]?.cooldown?:20
        if(timeSinceLast >= cooldown){
            augmentApplied[id] = 0
        } else{
            augmentApplied[id] = (cooldown.toLong() - timeSinceLast).toInt()
        }
        val message = TranslatableText("scepter.new_active_spell").append(TranslatableText("enchantment.amethyst_imbuement.${Identifier(newActiveEnchant).path}"))
        user.sendMessage(message,false)
    }

    private fun fixActiveEnchantWhenMissing(stack: ItemStack, world: World){
        val nbt = stack.orCreateNbt
        val newEnchant = EnchantmentHelper.get(stack).keys.firstOrNull()
        val identifier = if (newEnchant != null){
            Registry.ENCHANTMENT.getId(newEnchant)
        } else {
            stack.addEnchantment(RegisterEnchantment.MAGIC_MISSILE,1)
            Registry.ENCHANTMENT.getId(RegisterEnchantment.MAGIC_MISSILE)
        }
        if (identifier != null) {
            nbt.putString(NbtKeys.ACTIVE_ENCHANT.str(), identifier.toString())
        }
        initializeScepter(stack,world)
    }

    fun activeEnchantHelper(world: World,stack: ItemStack, activeEnchant: String): String{
        val nbt: NbtCompound = stack.nbt?:return activeEnchant
        val id: Int = nbtChecker(nbt) ?: return activeEnchant
        return if(!activeAugment.containsKey(id)){
            activeEnchant
        } else {
            return if (augmentApplied[id]==-1){
                activeEnchant
            } else if(activeAugment[id] != null) {
                var nxt: String = activeEnchant
                activeAugment[id]?.let { nxt = it }
                if (!world.isClient) {
                    augmentApplied[id] = -1
                    writeStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nxt, nbt)
                }
                nxt
            } else {
                activeEnchant
            }
        }
    }

    fun checkManaCost(cost: Int, stack: ItemStack, world: World, user: PlayerEntity): Boolean{
        return (checkCanUseHandler(stack, world, user, cost, ""))
    }

    fun applyManaCost(cost: Int, stack: ItemStack, world: World, user: PlayerEntity){
        damageHandler(stack,world,user,cost,"")
    }

    fun incrementScepterStats(scepterNbt: NbtCompound, activeEnchantId: String, xpMods: AugmentModifier.XpModifiers? = null){
        val spellKey = augmentStats[activeEnchantId]?.type?.name ?: return
        if(spellKey == SpellType.NULL.name) return
        val statLvl = readNbt(spellKey + "_lvl",scepterNbt)
        val statMod = xpMods?.getMod(spellKey) ?: 0
        val statXp = readNbt(spellKey + "_xp",scepterNbt) + statMod + 1
        writeNbt(spellKey + "_xp",statXp,scepterNbt)
        if(checkXpForLevelUp(statXp,statLvl)){
            writeNbt(spellKey + "_lvl",statLvl + 1,scepterNbt)
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

    fun getScepterStats(stack: ItemStack): IntArray {
        val nbt = stack.orCreateNbt
        return getStatsHelper(nbt)
    }

    fun resetCooldown(stack: ItemStack, activeEnchantId: String){
        val nbt = stack.nbt?: return
        val id: Int = nbtChecker(nbt) ?:return
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
            val effect = persistentEffect[id]?.effect?:return
            aug.persistentEffect(world, user,blockPos, entityList, level, effect)
            val dur = persistentEffect[id]?.duration
            if(dur != null){
                val newDur = dur - delay
                persistentEffect[id]?.duration = newDur
                if (newDur <= 0) {
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
    fun setPersistentTickerNeed(
        world: World, user: LivingEntity, 
        entityList: MutableList<Entity>, 
        level: Int,blockPos: BlockPos, 
        augment: MiscAugment, 
        delay: Int, duration: Int,
        effect: AugmentEffect){
        val stackTemp = user.getStackInHand(Hand.MAIN_HAND)
        val stack = if (stackTemp.item is ScepterItem){
            stackTemp
        } else {
            user.getStackInHand(Hand.OFF_HAND)
        }
        val nbt = stack.orCreateNbt
        val id = nbtChecker(nbt) ?:return
        persistentEffect[id] = PersistentEffectData(world,user,entityList,level,blockPos,augment,delay,duration, effect)
        persistentEffectNeed[id] = 0
    }

    fun scepterTickNbtCheck(stack: ItemStack): Int{
        val nbt = stack.nbt ?: return -1
        return nbtChecker(nbt) ?:return -1
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

    fun bookOfLoreNbtGenerator(tier: LoreTier = LoreTier.ANY_TIER): NbtCompound{
        val nbt = NbtCompound()
        val aug =  when (tier){
            LoreTier.ANY_TIER ->{
                getRandBookOfLoreAugment(bookOfLoreListT12)
            }
            LoreTier.LOW_TIER ->{
                getRandBookOfLoreAugment(bookOfLoreListT1)
            }
            LoreTier.HIGH_TIER ->{
                getRandBookOfLoreAugment(bookOfLoreListT2)
            }
            else->{
                fallbackAugment
            }
        }
        nbt.putString(NbtKeys.LORE_KEY.str(),aug)
        return nbt
    }

    fun registerAugmentStat(id: String, dataPoint: AugmentDatapoint, overwrite: Boolean = false){
        if(!augmentStats.containsKey(id) || overwrite){
            augmentStats[id] = dataPoint
            when (dataPoint.bookOfLoreTier){
                1 -> {
                    if (!bookOfLoreListT1.contains(id)){
                        bookOfLoreListT1.add(id)
                    }
                    if (!bookOfLoreListT12.contains(id)){
                        bookOfLoreListT12.add(id)
                    }
                }
                2 -> {
                    if (!bookOfLoreListT2.contains(id)){
                        bookOfLoreListT2.add(id)
                    }
                    if (!bookOfLoreListT12.contains(id)){
                        bookOfLoreListT12.add(id)
                    }
                }
                else -> {}
            }
        }
    }

    fun checkAugmentStat(id: String): Boolean{
        return augmentStats.containsKey(id)
    }

    fun getAugmentType(id: String): SpellType {
        if(!augmentStats.containsKey(id)) return SpellType.NULL
        return augmentStats[id]?.type?: SpellType.NULL
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
        val cost = (augmentStats[id]?.manaCost?.minus(reduction)) ?: (10 - reduction)
        return max(1,cost)
    }

    fun getAugmentCooldown(id: String): Int{
        if(!augmentStats.containsKey(id)) return (20)
        val cd = (augmentStats[id]?.cooldown) ?: 20
        return max(1,cd)
    }

    fun getAugmentImbueLevel(id: String): Int{
        if(!augmentStats.containsKey(id)) return (1)
        val cd = (augmentStats[id]?.imbueLevel) ?: 1
        return max(1,cd)
    }

    fun getAugmentTier(id: String): Int{
        if(!augmentStats.containsKey(id)) return (1)
        val cd = (augmentStats[id]?.bookOfLoreTier) ?: 1
        return max(1,cd)
    }

    fun xpToNextLevel(xp: Int,lvl: Int): Int{
        val xpNext = (2 * lvl * lvl + 40 * lvl)
        return (xpNext - xp + 1)
    }

    data class AugmentEffect(
        private var damage: Float = 0.0F,
        private var damagePerLevel: Float = 0.0F,
        private var damagePercent: Float = 0.0F,
        private var amplifier: Int = 0,
        private var amplifierPerLevel: Int = 0,
        private var duration: Int = 0,
        private var durationPerLevel: Int = 0,
        private var durationPercent: Int = 0,
        private var range: Double = 0.0,
        private var rangePerLevel: Double = 0.0
        private var rangePercent: Double = 0.0){

        fun plus(ae: AugmentEffect){
            damage += ae.damage
            damagePerLevel += ae.damagePerLevel
            damagePercent += ae.damagePercent
            amplifier += ae.amplifier
            amplifierPerLevel += amplifierPerLevel
            duration += ae.duration
            durationPerLevel += ae.durationPerLevel
            durationPercent += ae.durationPercent
            range += ae.range
            rangePerLevel += ae.rangePerLevel
            rangePercent += ae.rangePercent
        }
        fun damage(level: Int): Float{
            return max(0.0F,(damage + damagePerLevel * level) * (100.0F + damagePercent) / 100.0F)
        }
        fun amplifier(level: Int): Int{
            return max(0,amplifier + amplifierPerLevel * level)
        }
        fun duration(level: Int): Int{
            return max(0,(duration + durationPerLevel * level) * (100.0F + durationPercent) / 100.0F )
        }
        fun range(level: Int): Double{
            return max(1.0,(range + rangePerLevel * level) * (100.0F + rangePercent) / 100.0F)
        }
        fun withDamage(damage: Float = 0.0F, damagePerLevel: Float = 0.0F, damagePercent: Float = 0.0F): AugmentEffect{
            return this.copy(damage = damage, damagePerLevel = damagePerLevel, damagePercent = damagePercent)
        }
        fun withAmplifier(amplifier: Int = 0, amplifierPerLevel: Int = 0): AugmentEffect{
            return this.copy(amplifier = amplifier, amplifierPerLevel = amplifierPerLevel)
        }
        fun withDuration(duration: Int = 0, durationPerLevel: Int = 0, durationPercent: Int = 0): AugmentEffect{
            return this.copy(duration = duration, durationPerLevel = durationPerLevel, durationPercent = durationPercent)
        }
        fun withRange(range: Double = 0.0, rangePerLevel: Double = 0.0, rangePercent: Double = 0.0): AugmentEffect{
            return this.copy(range = range, rangePerLevel = rangePerLevel, rangePercent = rangePercent)
        }
    }
    data class AugmentDatapoint(val type: SpellType, val cooldown: Int,
                                val manaCost: Int, val minLvl: Int, val imbueLevel: Int,
                                val bookOfLoreTier: Int, val keyItem: Item){
        //hi
    }
    private data class PersistentEffectData(val world: World, val user: LivingEntity,
                                            val entityList: MutableList<Entity>, val level: Int, val blockPos: BlockPos,
                                            val augment: MiscAugment, var delay: Int, var duration: Int, effect: AugmentEffect){
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
        return (random.nextFloat() * 2000000000.0F).toInt()
    }
    private fun checkXpForLevelUp(xp:Int,lvl:Int): Boolean{
        return (xp > (2 * lvl * lvl + 40 * lvl))
    }
    private fun getRandBookOfLoreAugment(list: MutableList<String>): String{
        if (list.isEmpty()) return fallbackAugment
        val rndMax = list.size
        val rndIndex = AI.aiRandom().nextInt(rndMax)
        return list[rndIndex]
    }
    private fun getStatsHelper(nbt: NbtCompound): IntArray{
        val stats = intArrayOf(0,0,0,0,0,0)
        if(!nbt.contains("FURY_lvl")){
            nbt.putInt("FURY_lvl",1)
        }
        stats[0] = nbt.getInt("FURY_lvl")
        if(!nbt.contains("GRACE_lvl")){
            nbt.putInt("GRACE_lvl",1)
        }
        stats[1] = nbt.getInt("GRACE_lvl")
        if(!nbt.contains("WIT_lvl")){
            nbt.putInt("WIT_lvl",1)
        }
        stats[2] = nbt.getInt("WIT_lvl")
        if(!nbt.contains("FURY_xp")){
            nbt.putInt("FURY_xp",0)
        }
        stats[3] = nbt.getInt("FURY_xp")
        if(!nbt.contains("GRACE_xp")){
            nbt.putInt("GRACE_xp",0)
        }
        stats[4] = nbt.getInt("GRACE_xp")
        if(!nbt.contains("WIT_xp")){
            nbt.putInt("WIT_xp",0)
        }
        stats[5] = nbt.getInt("WIT_xp")
        return stats
    }

}


