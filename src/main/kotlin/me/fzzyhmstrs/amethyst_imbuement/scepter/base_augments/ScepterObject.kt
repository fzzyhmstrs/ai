@file:Suppress("REDUNDANT_ELSE_IN_WHEN")

package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEvent
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.amethyst_imbuement.util.*
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
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
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.max

object ScepterObject: AugmentDamage {

    private val scepters: MutableMap<Int,MutableMap<String,Long>> = mutableMapOf()
    private val augmentStats: MutableMap<String, AugmentDatapoint> = mutableMapOf()
    private val activeAugment: MutableMap<Int,String> = mutableMapOf()
    private val augmentApplied: MutableMap<Int,Int> = mutableMapOf()
    private val persistentEffect: MutableMap<Int, PersistentEffectData> = mutableMapOf()
    private val persistentEffectNeed: MutableMap<Int,Int> = mutableMapOf()
    private val augmentModifiers: MutableMap<Int,MutableList<Identifier>> = mutableMapOf()
    private val activeScepterModifiers: MutableMap<Int, CompiledAugmentModifier.CompiledModifiers> = mutableMapOf()
    private val scepterHealTickers: MutableMap<Int,RegisterEvent.Ticker> = mutableMapOf()
    const val fallbackAugment = AI.MOD_ID+":magic_missile"
    private val SCEPTER_SYNC_PACKET = Identifier(AI.MOD_ID,"scepter_sync_packet")
    private val DUSTBIN = Dustbin({ dirt -> gatherActiveScepterModifiers(dirt) },-1)
    val BLANK_EFFECT = AugmentEffect()
    val BLANK_XP_MOD = XpModifiers()


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
        if (scepterNbt.contains(NbtKeys.MODIFIERS.str())){
            initializeModifiers(id,scepterNbt, stack)
        }
        DUSTBIN.markDirty(id)
        activeAugment[id] = readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(),scepterNbt)
        augmentApplied[id] = -1
        DUSTBIN.clean()
        if(scepters[id]?.isNotEmpty() == true){ //break out of initialization if the scepter has already been initialized and isn't changed
            if (scepters[id]?.size == stack.enchantments.size){
                return
            }
        }
        if (!scepterHealTickers.containsKey(id)){
            val item = stack.item
            if (item is ScepterItem) {
                scepterHealTickers[id] = RegisterEvent.Ticker(item.getRepairTime())
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

    fun useScepter(activeEnchantId: String, stack: ItemStack, user: PlayerEntity, world: World, cdMod: Double = 0.0): Int?{
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
            //cooldown modifier is a percentage modifier, so 20% will boost cooldown by 20%. -20% will take away 20% cooldown
            val cooldown = (augmentStats[activeEnchantId]?.cooldown?.times(100.0+ cdMod)?.div(100.0))?.toInt()
            val time = user.world.time
            val lastUsed = scepters[id]?.get(activeEnchantId)
            if (cooldown != null && lastUsed != null){
                val cooldown2 = max(cooldown,1) // don't let cooldown be less than 1 tick
                return if (time - cooldown2 >= lastUsed){ //checks that enough time has passed since last usage
                    scepters[id]?.put(activeEnchantId, user.world.time)
                    cooldown2
                } else {
                    null
                }
            }
        } else {
            initializeScepter(stack,world)
            return (augmentStats[activeEnchantId]?.cooldown?.times(100.0+ cdMod)?.div(100.0))?.toInt()
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
            val enchantCheck = Registry.ENCHANTMENT.get(identifier)?: Enchantments.VANISHING_CURSE
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
        DUSTBIN.markDirty(id)
        val message = Text.translatable("scepter.new_active_spell").append(Text.translatable("enchantment.amethyst_imbuement.${Identifier(newActiveEnchant).path}"))
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
        val id: Int = nbtChecker(nbt)
        return if(!activeAugment.containsKey(id)){
            initializeScepter(stack, world)
            activeEnchant
        } else {
            return if (augmentApplied[id] == -1){
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

    fun incrementScepterStats(scepterNbt: NbtCompound, activeEnchantId: String, xpMods: XpModifiers? = null){
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

    fun isAcceptableScepterItem(augment:ScepterAugment, stack: ItemStack, player: PlayerEntity): Boolean {
        val nbt = stack.orCreateNbt
        return ScepterObject.checkScepterStat(
            nbt,
            Registry.ENCHANTMENT.getId(augment)?.toString() ?: ""
        ) || player.abilities.creativeMode
    }

    fun resetCooldown(stack: ItemStack, activeEnchantId: String){
        val nbt = stack.nbt?: return
        val id: Int = nbtChecker(nbt)
        if(!scepters.containsKey(id)) return
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
        augment: PersistentAugment,
        delay: Int, duration: Int,
        effect: AugmentEffect){
        val stackTemp = user.getStackInHand(Hand.MAIN_HAND)
        val stack = if (stackTemp.item is ScepterItem){
            stackTemp
        } else {
            user.getStackInHand(Hand.OFF_HAND)
        }
        val nbt = stack.orCreateNbt
        val id = nbtChecker(nbt)
        if (!scepters.containsKey(id)) return
        persistentEffect[id] = PersistentEffectData(world,user,entityList,level,blockPos,augment,delay,duration, effect)
        persistentEffectNeed[id] = 0
    }

    fun scepterTickNbtCheck(stack: ItemStack): Int{
        val nbt = stack.nbt ?: return -1
        return nbtChecker(nbt)
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
        val aug = getRandBookOfLoreAugment(tier.list())
        nbt.putString(NbtKeys.LORE_KEY.str(),aug)
        return nbt
    }
    private fun getRandBookOfLoreAugment(list: List<String>): String{
        if (list.isEmpty()) return fallbackAugment
        val rndMax = list.size
        val rndIndex = AI.aiRandom().nextInt(rndMax)
        return list[rndIndex]
    }

    fun registerAugmentStat(id: String, dataPoint: AugmentDatapoint, overwrite: Boolean = false){
        if(!augmentStats.containsKey(id) || overwrite){
            augmentStats[id] = dataPoint
            dataPoint.bookOfLoreTier.addToList(id)
        }
    }
    fun registerAugmentStat(augment: ScepterAugment){
        val id = EnchantmentHelper.getEnchantmentId(augment)?.toString()?:throw NoSuchElementException("Enchantment ID for ${this.javaClass.canonicalName} not found!")
        val imbueLevel = if (checkAugmentStat(id)){
            getAugmentImbueLevel(id)
        } else {
            1
        }
        registerAugmentStat(id,configAugmentStat(augment,id,imbueLevel),true)
    }

    private fun configAugmentStat(augment: ScepterAugment,id: String,imbueLevel: Int = 1): AugmentDatapoint{
        val stat = augment.augmentStat(imbueLevel)
        val augmentConfig = AiConfig.AugmentStats()
        val type = stat.type
        augmentConfig.id = id
        augmentConfig.cooldown = stat.cooldown
        augmentConfig.manaCost = stat.manaCost
        augmentConfig.minLvl = stat.minLvl
        val tier = stat.bookOfLoreTier
        val item = stat.keyItem
        val augmentAfterConfig = AiConfig.configAugment(this.javaClass.simpleName + AiConfig.augmentVersion +".json",augmentConfig)
        return AugmentDatapoint(type,augmentAfterConfig.cooldown,augmentAfterConfig.manaCost,augmentAfterConfig.minLvl,imbueLevel,tier,item)
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

    fun getAugmentManaCost(id: String, reduction: Double = 0.0): Int{
        if(!augmentStats.containsKey(id)) return (10 * (100.0 + reduction) / 100.0).toInt()
        val cost = (augmentStats[id]?.manaCost?.times(100.0 + reduction)?.div(100.0))?.toInt() ?: (10 * (100.0 + reduction) / 100.0).toInt()
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

    fun getAugmentTier(id: String): LoreTier {
        if (!augmentStats.containsKey(id)) return (LoreTier.NO_TIER)
        return (augmentStats[id]?.bookOfLoreTier) ?: LoreTier.NO_TIER
    }

    fun xpToNextLevel(xp: Int,lvl: Int): Int{
        val xpNext = (2 * lvl * lvl + 40 * lvl)
        return (xpNext - xp + 1)
    }

    @Deprecated("moving to amethyst_core")
    data class AugmentDatapoint(val type: SpellType = SpellType.NULL, val cooldown: Int = 20,
                                val manaCost: Int = 20, val minLvl: Int = 1, val imbueLevel: Int = 1,
                                val bookOfLoreTier: LoreTier = LoreTier.NO_TIER, val keyItem: Item = Items.AIR)
    private data class PersistentEffectData(val world: World, val user: LivingEntity,
                                            val entityList: MutableList<Entity>, val level: Int, val blockPos: BlockPos,
                                            val augment: PersistentAugment, var delay: Int, var duration: Int, val effect: AugmentEffect)

    fun addModifier(modifier: Identifier, stack: ItemStack): Boolean{
        val nbt = stack.orCreateNbt
        val id: Int = nbtChecker(nbt)
        return addModifier(modifier, id, nbt)
    }
    fun addModifierForREI(modifier: Identifier, stack: ItemStack){
        val nbt = stack.orCreateNbt
        addModifierToNbt(modifier, nbt)
    }
    private fun addModifier(modifier: Identifier, scepter: Int, nbt: NbtCompound): Boolean{
        if (!augmentModifiers.containsKey(scepter)) {
            augmentModifiers[scepter] = mutableListOf()
        }
        val highestModifier = checkDescendant(modifier,scepter)
        if (highestModifier != null){
            val mod = RegisterModifier.ENTRIES.get(modifier)
            return if (mod?.hasDescendant() == true){
                val highestDescendantPresent = checkModifierLineage(mod,scepter)
                if (highestDescendantPresent < 0){
                    false
                } else {
                    val lineage = mod.getModLineage()
                    val newDescendant = lineage[highestDescendantPresent]
                    val currentGeneration = lineage[max(highestDescendantPresent - 1,0)]
                    augmentModifiers[scepter]?.add(newDescendant)
                    addModifierToNbt(newDescendant, nbt)
                    removeModifier(scepter, currentGeneration, nbt)
                    DUSTBIN.markDirty(scepter)
                    true
                }
            } else {
                false
            }
        }
        augmentModifiers[scepter]?.add(modifier)
        addModifierToNbt(modifier, nbt)
        DUSTBIN.markDirty(scepter)
        return true
    }
    private fun addModifier(modifier: Identifier,scepter: Int){
        if (!augmentModifiers.containsKey(scepter)) {
            augmentModifiers[scepter] = mutableListOf()
        }
        if (augmentModifiers[scepter]?.contains(modifier) != true) {
            augmentModifiers[scepter]?.add(modifier)
        }
        DUSTBIN.markDirty(scepter)
    }
    private fun removeModifier(scepter: Int, modifier: Identifier, nbt: NbtCompound){
        augmentModifiers[scepter]?.remove(modifier)
        DUSTBIN.markDirty(scepter)
        removeModifierFromNbt(modifier,nbt)
    }
    private fun addModifierToNbt(modifier: Identifier, nbt: NbtCompound){
        val newEl = NbtCompound()
        newEl.putString(NbtKeys.MODIFIER_ID.str(),modifier.toString())
        Nbt.addNbtToList(newEl,NbtKeys.MODIFIERS.str(),nbt)
    }
    private fun removeModifierFromNbt(modifier: Identifier, nbt: NbtCompound){
        Nbt.removeNbtFromList(NbtKeys.MODIFIERS.str(),nbt) { nbtEl: NbtCompound ->
            if (nbtEl.contains(NbtKeys.MODIFIER_ID.str())){
                val chk = Identifier(nbtEl.getString(NbtKeys.MODIFIER_ID.str()))
                chk == modifier
            } else {
                false
            }
        }
    }
    private fun initializeModifiers(id: Int, nbt: NbtCompound, stack: ItemStack){
        val nbtList = nbt.getList(NbtKeys.MODIFIERS.str(),10)
        for (el in nbtList){
            val compound = el as NbtCompound
            if (compound.contains(NbtKeys.MODIFIER_ID.str())){
                val modifier = compound.getString(NbtKeys.MODIFIER_ID.str())
                addModifier(Identifier(modifier),id)
            }
        }
        if (stack.hasEnchantments()){
            val enchants = stack.enchantments
            var attunedLevel = 0
            var nbtEl: NbtCompound
            for (el in enchants) {
                nbtEl = el as NbtCompound
                if (EnchantmentHelper.getIdFromNbt(nbtEl) == Identifier(AI.MOD_ID,"attuned")){
                    attunedLevel = EnchantmentHelper.getLevelFromNbt(nbtEl)
                    break
                }
            }
            if (attunedLevel > 0) {
                for (i in 1..attunedLevel) {
                    addModifier(RegisterModifier.LESSER_ATTUNED.modifierId, id, nbt)
                }
                val newEnchants = EnchantmentHelper.fromNbt(enchants)
                EnchantmentHelper.set(newEnchants,stack)
            }
        }
    }


    fun getModifiers(stack: ItemStack): List<Identifier>{
        val nbt = stack.orCreateNbt
        val id: Int = nbtChecker(nbt)
        if (!augmentModifiers.containsKey(id)) {
            if (stack.nbt?.contains(NbtKeys.MODIFIERS.str()) == true) {
                initializeModifiers(id,nbt, stack)
            }
        }
        return augmentModifiers[id] ?: listOf()
    }

    fun getActiveModifiers(stack: ItemStack): CompiledAugmentModifier.CompiledModifiers {
        val nbt = stack.orCreateNbt
        val id: Int = nbtChecker(nbt)
        return activeScepterModifiers[id] ?: AugmentModifierDefaults.BLANK_COMPILED_DATA
    }

    private fun checkDescendant(modifier: Identifier, scepter: Int): Identifier?{
        val mod = RegisterModifier.ENTRIES.get(modifier)
        val lineage = mod?.getModLineage() ?: return modifier
        var highestModifier: Identifier? = null
        lineage.forEach { identifier ->
            if (augmentModifiers[scepter]?.contains(identifier) == true){
                highestModifier = identifier
            }
        }
        return highestModifier
    }
    fun checkModifierLineage(modifier:Identifier, stack: ItemStack): Boolean{
        val mod = RegisterModifier.ENTRIES.get(modifier)
        return if (mod != null){
            checkModifierLineage(mod, stack)
        } else {
            false
        }
    }
    private fun checkModifierLineage(mod:AugmentModifier, stack: ItemStack): Boolean{
        val id: Int = nbtChecker(stack.orCreateNbt)
        return checkModifierLineage(mod, id) > 0
    }
    private fun checkModifierLineage(mod:AugmentModifier, id: Int): Int{
        val lineage = mod.getModLineage()
        //println("lineage: $lineage")
        val highestOrderDescendant = lineage.size
        var highestDescendantPresent = 1
        lineage.forEachIndexed { index, identifier ->
            if (augmentModifiers[id]?.contains(identifier) == true){
                highestDescendantPresent = index + 1
            }
        }
        return if(highestDescendantPresent < highestOrderDescendant){
            highestDescendantPresent
        } else {
            -1
        }
    }

    private fun gatherActiveScepterModifiers(scepter: Int){
        val activeEnchant =  Identifier(activeAugment[scepter]?:return)
        val list : MutableList<AugmentModifier> = mutableListOf()
        val compiledModifier = CompiledAugmentModifier()
        augmentModifiers[scepter]?.forEach { identifier ->
            val modifier = RegisterModifier.ENTRIES.get(identifier)
            if (modifier != null){
                if (!modifier.hasSpellToAffect()){
                    list.add(modifier)
                    compiledModifier.plus(modifier)
                } else {
                    if (modifier.checkSpellsToAffect(activeEnchant)){
                        list.add(modifier)
                        compiledModifier.plus(modifier)
                    }
                }
            }
        }
        activeScepterModifiers[scepter] = CompiledAugmentModifier.CompiledModifiers(list, compiledModifier)
    }

    fun tickModifiers(){
        if (DUSTBIN.isDirty()){
            DUSTBIN.clean()
        }
    }

    fun tickTicker(id: Int): Boolean{
        scepterHealTickers[id]?.tickUp()
        return (scepterHealTickers[id]?.isReady() == true)
    }

    private fun nbtChecker(nbt: NbtCompound): Int{
        return readNbt(NbtKeys.SCEPTER_ID.str(),nbt)
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
    private fun getNewId(): Int{
        var newId = (AI.aiRandom().nextFloat() * 2000000000.0F).toInt()
        while (scepters.containsKey(newId)){
            newId = (AI.aiRandom().nextFloat() * 2000000000.0F).toInt()
        }
        return newId
    }
    private fun checkXpForLevelUp(xp:Int,lvl:Int): Boolean{
        return (xp > (2 * lvl * lvl + 40 * lvl))
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


