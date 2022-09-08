package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.*
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*

@Suppress("SENSELESS_COMPARISON", "unused")
class AltarOfExperienceScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext,
    private val propertyDelegate: PropertyDelegate
):  ScreenHandler(RegisterHandler.ALTAR_OF_EXPERIENCE_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory) : this(
        syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
        ArrayPropertyDelegate(1)
    )

    private val playInventory = playerInventory
    private val random = Random()
    private val seed = Property.create()
    private var xp = 0
    var xpStored = 0
    var xpMax = 0
    //private var matchBut: Optional<ImbuingRecipe>? = Optional.empty()


    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(this.context, player, RegisterBlock.ALTAR_OF_EXPERIENCE)
    }

    fun getSeed(): Int {
        return this.seed.get()
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
    }

    private fun getSyncedStoredXp(): Int{
        return propertyDelegate.get(0)
    }

    private fun setSyncedStoredXp(amount: Int){
        propertyDelegate.set(0,amount)
    }

    fun getPlayerXp(): Int{
        return xp
    }

    private fun updateMaxXp(candles: Int, wardingCandles: Int): Int{
        val base = AiConfig.altars.altarOfExperienceBaseLevels
        val perCandle = AiConfig.altars.altarOfExperienceCandleLevelsPer
        val level = base + perCandle * candles + 2 * perCandle * wardingCandles
        return ((4.5 * level * level) - (162.5 * level) + 2220).toInt()
    }

    private fun checkCandles(world: World, pos: BlockPos): Pair<Int,Int>{
        var candles = 0
        var wardingCandles = 0
        for (offset in blocks){
            if (world.getBlockState(pos.add(offset.first, 0, offset.second)).isIn(BlockTags.CANDLES)) {
                candles++
            } else if (world.getBlockState(pos.add(offset.first, 0, offset.second)).isOf(RegisterBlock.WARDING_CANDLE)) {
                wardingCandles++
            }
        }
        return Pair(candles,wardingCandles)
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        val currentXp = getPlayerLvlXp(player)
        if (id == 0){
            if (currentXp <= 50){
                return false
            }
        }else if (id == 1){
            if (currentXp == 0){
                return false
            }
        } else if (id == 2) {
            if (xpStored == 0){
                return false
            }
        } else if (id == 4){
            if (currentXp <= 5000){
                return false
            }
        }else if (id == 5){
            if (currentXp == 0){
                return false
            }
        }else if (id == 6) {
            if (xpStored == 0){
                return false
            }
        }else if (id == 7) {
            if (xpStored <= 5000){
                return false
            }
        } else {
            if (xpStored <= 50){
                return false
            }
        }
        context.run { world: World, pos: BlockPos ->
            val (candleTotal, wardingCandleTotal) = checkCandles(world,pos)
            xpMax = updateMaxXp(candleTotal,wardingCandleTotal)
            val xpLeft = xpMax - xpStored
            when (id) {
                0 -> {
                    if (currentXp >= 500) {
                        xpStored += if (xpLeft < 500){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-500)
                            500
                        }
                    } else {
                        xpStored += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                }
                1 -> {
                    if (currentXp >= 50) {
                        xpStored += if (xpLeft < 50){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-50)
                            50
                        }
                    } else {
                        xpStored += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                }
                2 -> {
                    if (xpStored < 50){
                        addExperience(player,xpStored)
                        xpStored = 0
                    } else {
                        addExperience(player,50)
                        xpStored -= 50
                    }
                }
                3 -> {
                    if (xpStored < 500){
                        addExperience(player,xpStored)
                        xpStored = 0
                    } else {
                        addExperience(player,500)
                        xpStored -= 500
                    }
                }
                4 -> {
                    xpStored += if (xpLeft < currentXp){
                        addExperience(player,-xpLeft)
                        xpLeft
                    } else {
                        addExperience(player,-currentXp)
                        currentXp
                    }
                }
                5 -> {
                    if (currentXp >= 5000) {
                        xpStored += if (xpLeft < 5000){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-5000)
                            5000
                        }
                    } else {
                        xpStored += if (xpLeft < currentXp){
                            addExperience(player,-xpLeft)
                            xpLeft
                        } else {
                            addExperience(player,-currentXp)
                            currentXp
                        }
                    }
                }
                6 -> {
                    if (xpStored < 5000){
                        addExperience(player,xpStored)
                        xpStored = 0
                    } else {
                        addExperience(player,5000)
                        xpStored -= 5000
                    }
                }
                7 -> {
                    addExperience(player,xpStored)
                    xpStored = 0
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
            setSyncedStoredXp(xpStored)
            xp = getPlayerLvlXp(player)
            sendXpUpdates()
            sendContentUpdates()
        }
        return true
    }

    private fun addExperience(player: PlayerEntity, experience: Int){
        if (AiConfig.altars.altarOfExperienceCustomXpMethod){
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

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    private fun getPlayerLvlXp(player: PlayerEntity): Int{
        val lvl = player.experienceLevel
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
        return lvlXp + progressXp
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

        val totalXp = getPlayerLvlXp(playerInventory.player)
        xp = totalXp
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        if (context != ScreenHandlerContext.EMPTY) {
            context.run { world: World, pos: BlockPos ->
                val (c,wc) = checkCandles(world, pos)
                val e = updateMaxXp(c, wc)
                xpMax = e
                xpStored = getSyncedStoredXp()
                sendXpUpdates()
                sendContentUpdates()
            }
        }
    }

    private fun sendXpUpdates(){
        val player = playInventory.player
        if (player is ServerPlayerEntity) {
            val buf = PacketByteBufs.create()
            buf.writeInt(syncId)
            buf.writeInt(xp)
            buf.writeInt(xpStored)
            buf.writeInt(xpMax)

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
                handler.xp = buf.readInt()
                handler.xpStored = buf.readInt()
                handler.xpMax = buf.readInt()
            }
        }

        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(XP_REQUEST){_,player,_,_,_ ->
                val handler = player.currentScreenHandler
                if (handler !is AltarOfExperienceScreenHandler) return@registerGlobalReceiver
                handler.sendXpUpdates()
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