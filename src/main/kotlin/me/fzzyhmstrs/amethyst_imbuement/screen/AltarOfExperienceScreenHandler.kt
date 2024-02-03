package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.*
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
class AltarOfExperienceScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext,
    private val storedXp: PropertyDelegate,
    private val maxXp: PropertyDelegate
):  ScreenHandler(RegisterHandler.ALTAR_OF_EXPERIENCE_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
        ArrayPropertyDelegate(2),
        ArrayPropertyDelegate(2)
    )

    private val playInventory = playerInventory
    private val random = Random()
    private val seed = Property.create()
    private var xp = Property.create()
    var displayXpMax = 0
    var displayXpStored = 0
    private val maxPossibleXp = updateMaxXp(0,16)
    //private var matchBut: Optional<ImbuingRecipe>? = Optional.empty()

    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, RegisterBlock.ALTAR_OF_EXPERIENCE)
    }

    fun getSeed(): Int {
        return this.seed.get()
    }

    fun getSyncedStoredXp(): Int{
        val divisor = storedXp.get(0)
        val remainder = storedXp.get(1)
        return divisor * Short.MAX_VALUE + remainder
    }

    fun setSyncedStoredXp(amount: Int){
        val divisor = amount / Short.MAX_VALUE
        val remainder = amount % Short.MAX_VALUE
        storedXp.set(0,divisor)
        storedXp.set(1,remainder)
        displayXpStored = amount
    }

    fun getSyncedMaxXp(): Int{
        val divisor = maxXp.get(0)
        val remainder = maxXp.get(1)
        return divisor * Short.MAX_VALUE + remainder
    }

    fun setSyncedMaxXp(amount: Int){
        val divisor = amount / Short.MAX_VALUE
        val remainder = amount % Short.MAX_VALUE
        maxXp.set(0,divisor)
        maxXp.set(1,remainder)
        displayXpMax = amount
    }

    fun getPlayerXp(): Int{
        return xp.get()
    }

    private fun updateMaxXp(candles: Int, wardingCandles: Int): Int{
        val base = AiConfig.blocks.altar.baseLevels.get()
        val perCandle = AiConfig.blocks.altar.candleLevelsPer.get()
        val level = base + perCandle * candles + 2 * perCandle * wardingCandles
        return ((4.5 * level * level) - (162.5 * level) + 2220).toInt()
    }



    private fun checkCandles(world: World, pos: BlockPos): Pair<Int,Int>{
        var candles = 0
        var wardingCandles = 0
        for (offset in blocks){
            if (world.getBlockState(pos.add(offset.first, 0, offset.second)).isIn(RegisterTag.WARDING_CANDLES_TAG)) {
                wardingCandles++
            } else if (world.getBlockState(pos.add(offset.first, 0, offset.second)).isIn(BlockTags.CANDLES)) {
                candles++
            }
        }
        return Pair(candles,wardingCandles)
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        context.run { world: World, pos: BlockPos ->
            val (candleTotal, wardingCandleTotal) = checkCandles(world,pos)
            setSyncedMaxXp(updateMaxXp(candleTotal,wardingCandleTotal))
        }
        val syncedMaxXp = getSyncedMaxXp()
        val xpLeft = syncedMaxXp - getSyncedStoredXp()
        val currentXp = getPlayerLvlXp(player)
        if (id == 0){
            if (currentXp <= 50 || xpLeft <= 50){
                return false
            }
        }else if (id == 1){
            if (currentXp == 0 || xpLeft == 0){
                return false
            }
        } else if (id == 2) {
            if (getSyncedStoredXp() == 0){
                return false
            }
        } else if (id == 4){
            if (currentXp <= 5000 || xpLeft <= 5000){
                return false
            }
        }else if (id == 5){
            if (currentXp == 0 || xpLeft == 0){
                return false
            }
        }else if (id == 6) {
            if (getSyncedStoredXp() == 0){
                return false
            }
        }else if (id == 7) {
            if (getSyncedStoredXp() <= 5000){
                return false
            }
        } else {
            if (getSyncedStoredXp() <= 50){
                return false
            }
        }
        context.run { world: World, pos: BlockPos ->
            when (id) {
                0 -> {
                    var xpTemp = getSyncedStoredXp()
                    if (currentXp >= 500) {
                        xpTemp += if (xpLeft < 500){
                            addExperience(player,-xpLeft)
                            if (syncedMaxXp == maxPossibleXp && player is ServerPlayerEntity){
                                RegisterCriteria.CANDLELIT_MAX.trigger(player)
                            }
                            xpLeft
                        } else {
                            addExperience(player,-500)
                            500
                        }

                    } else {
                        xpTemp += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                    setSyncedStoredXp(xpTemp)
                }
                1 -> {
                    var xpTemp = getSyncedStoredXp()
                    if (currentXp >= 50) {
                        xpTemp += if (xpLeft < 50){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-50)
                            50
                        }
                    } else {
                        xpTemp += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                    setSyncedStoredXp(xpTemp)
                }
                2 -> {
                    val xpTemp = getSyncedStoredXp()
                    if (xpTemp < 50){
                        addExperience(player,xpTemp)
                        setSyncedStoredXp(0)
                    } else {
                        addExperience(player,50)
                        setSyncedStoredXp(xpTemp - 50)
                    }
                }
                3 -> {
                    val xpTemp = getSyncedStoredXp()
                    if (xpTemp < 500){
                        addExperience(player,xpTemp)
                        setSyncedStoredXp(0)
                    } else {
                        addExperience(player,500)
                        setSyncedStoredXp(xpTemp - 500)
                    }
                }
                4 -> {
                    var xpTemp = getSyncedStoredXp()
                    xpTemp += if (xpLeft < currentXp){
                        addExperience(player,-xpLeft)
                        if (syncedMaxXp == maxPossibleXp && player is ServerPlayerEntity){
                            RegisterCriteria.CANDLELIT_MAX.trigger(player)
                        }
                        xpLeft
                    } else {
                        addExperience(player,-currentXp)
                        currentXp
                    }
                    setSyncedStoredXp(xpTemp)
                }
                5 -> {
                    var xpTemp = getSyncedStoredXp()
                    if (currentXp >= 5000) {
                        xpTemp += if (xpLeft < 5000){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-5000)
                            5000
                        }
                    } else {
                        xpTemp += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                    setSyncedStoredXp(xpTemp)
                }
                6 -> {
                    val xpTemp = getSyncedStoredXp()
                    if (xpTemp < 5000){
                        addExperience(player,xpTemp)
                        setSyncedStoredXp(0)
                    } else {
                        addExperience(player,5000)
                        setSyncedStoredXp(xpTemp - 5000)
                    }
                }
                7 -> {
                    addExperience(player,getSyncedStoredXp())
                    setSyncedStoredXp(0)
                }
                else -> {}
            }
            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.BLOCKS,
                1.0f,
                world.random.nextFloat() * 0.1f + 0.9f
            )
            xp.set(getPlayerLvlXp(player))
            if (player is ServerPlayerEntity){
                RegisterCriteria.ALTAR_USE.trigger(player)
            }
            //sendXpUpdates()
            sendContentUpdates()
        }
        return true
    }

    private fun addExperience(player: PlayerEntity, experience: Int){
        if (AiConfig.blocks.altar.customXpMethod.get()){
            addCustomExperience(experience, player)
        } else {
            player.addExperience(experience)
        }
    }

    private fun addCustomExperience(experience: Int, player: PlayerEntity) {
        player.addScore(experience)
        player.experienceProgress += experience.toFloat() / player.nextLevelExperience.toFloat()
        player.totalExperience = MathHelper.clamp(player.totalExperience + experience, 0, Int.MAX_VALUE)
        while (player.experienceProgress < 0.0f) {
            val f: Float = player.experienceProgress * player.nextLevelExperience.toFloat()
            if (player.experienceLevel > 0) {
                player.addExperienceLevels(-1)
                player.experienceProgress = 1.0f + f / player.nextLevelExperience.toFloat()
                continue
            }
            player.addExperienceLevels(-1)
            player.experienceProgress = 0.0f
        }
        while (player.experienceProgress >= 1.0f) {
            player.experienceProgress = (player.experienceProgress - 1.0f) * player.nextLevelExperience.toFloat()
            player.addExperienceLevels(1)
            player.experienceProgress /= player.nextLevelExperience.toFloat()
        }
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    private fun getPlayerLvlXp(player: PlayerEntity): Int{
        val lvl = min(player.experienceLevel,20000)
        val nextLvlXp = player.nextLevelExperience
        val progress = player.experienceProgress
        val progressXp = (nextLvlXp * progress).toInt()
        val lvlXp = if(lvl < 17){
            (lvl*lvl + 6*lvl)
        } else if (lvl < 32){
            (2.5*lvl*lvl - 40.5*lvl + 360).toInt()
        } else {
            (4.5*lvl*lvl - 162.5*lvl + 2220).toInt()
        }
        return max(lvlXp + progressXp,0)
    }

    init{
        //add the player inventory
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        //add the player hotbar
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
        addProperties(storedXp)
        addProperties(maxXp)
        addProperty(xp)
        val player = playerInventory.player
        val totalXp = getPlayerLvlXp(player)
        xp.set(totalXp)
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        if (context != ScreenHandlerContext.EMPTY) {
            context.run { world: World, pos: BlockPos ->
                val (c,wc) = checkCandles(world, pos)
                if (wc == 16 && player is ServerPlayerEntity){
                    RegisterCriteria.CANDLELIT_ALTAR.trigger(player)
                }
                val e = updateMaxXp(c, wc)
                setSyncedMaxXp(e)
                sendContentUpdates()
            }
        }
    }

    private fun sendXpUpdates(){
        val player = playInventory.player
        if (player is ServerPlayerEntity) {
            val buf = PacketByteBufs.create()
            buf.writeInt(syncId)
            buf.writeInt(xp.get())
            buf.writeInt(displayXpMax)
            buf.writeInt(displayXpStored)

            ServerPlayNetworking.send(player, XP_UPDATE, buf)
        }
    }

    fun requestXpUpdates(){
        val buf = PacketByteBufs.create()
        ClientPlayNetworking.send(XP_REQUEST,buf)
    }

    companion object {

        private val XP_UPDATE = Identifier(AI.MOD_ID,"xp_update")
        private val XP_REQUEST  = Identifier(AI.MOD_ID,"xp_request")

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(XP_UPDATE) {client,_,buf,_ ->
                val player = client.player?:return@registerGlobalReceiver
                val syncID = buf.readInt()
                val handler = player.currentScreenHandler
                if (handler.syncId != syncID) return@registerGlobalReceiver
                if (handler !is AltarOfExperienceScreenHandler) return@registerGlobalReceiver
                handler.xp.set(buf.readInt())
                handler.displayXpMax = buf.readInt()
                handler.displayXpStored = buf.readInt()
            }
        }

        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(XP_REQUEST){server,player,_,_,_ ->
                val handler = player.currentScreenHandler
                if (handler !is AltarOfExperienceScreenHandler) return@registerGlobalReceiver
                //handler.sendXpUpdates()
                server.execute {
                    handler.sendContentUpdates()
                }
            }
        }

        private val blocks: List<Pair<Int,Int>> = listOf(
            Pair(-2,-2),
            Pair(-2,-1),
            Pair(-2,-0),
            Pair(-2,1),
            Pair(-2,2),
            Pair(2,-2),
            Pair(2,-1),
            Pair(2,-0),
            Pair(2,1),
            Pair(2,2),
            Pair(-1,-2),
            Pair(-1,2),
            Pair(1,-2),
            Pair(1,2),
            Pair(0,-2),
            Pair(0,2)
        )
    }

}