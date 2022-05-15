package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.*
import net.minecraft.screen.slot.Slot
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
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
    private val xp = Property.create()
    var experienceStored = IntArray(1)
    var experienceMax = IntArray(1)
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
        return this.xp.get()
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
        for (i in -1..1){
            for (j in -1..1){
                if (i == 0 && j == 0 || !world.isAir(pos.add(i, 0, j))) continue
                if (world.getBlockState(pos.add(i * 2, 0, j * 2)).isOf(Blocks.CANDLE)) {
                    candles++
                } else if (world.getBlockState(pos.add(i * 2, 0, j * 2)).isOf(RegisterBlock.WARDING_CANDLE)) {
                    wardingCandles++
                }
                if (world.getBlockState(pos.add(i, 0, j * 2)).isOf(Blocks.CANDLE)) {
                    candles++
                } else if (world.getBlockState(pos.add(i, 0, j * 2)).isOf(RegisterBlock.WARDING_CANDLE)) {
                    wardingCandles++
                }
                if (world.getBlockState(pos.add(i * 2, 0, j)).isOf(Blocks.CANDLE)) {
                    candles++
                } else if (world.getBlockState(pos.add(i * 2, 0, j)).isOf(RegisterBlock.WARDING_CANDLE)) {
                    wardingCandles++
                }
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
            if (experienceStored[0] == 0){
                return false
            }
        } else {
            if (experienceStored[0] <= 50){
                return false
            }
        }
        context.run { world: World, pos: BlockPos ->
            val (candleTotal, wardingCandleTotal) = checkCandles(world,pos)
            val xpMax = updateMaxXp(candleTotal,wardingCandleTotal)
            experienceMax[0] = xpMax
            val xpLeft = xpMax - experienceStored[0]
            when (id) {
                0 -> {
                    if (currentXp >= 500) {
                        if (xpLeft < 500){
                            player.addExperience(-xpLeft)
                            experienceStored[0] += xpLeft
                        } else {
                            player.addExperience(-500)
                            experienceStored[0] += 500
                        }
                    } else {
                        if (xpLeft < currentXp){
                            player.addExperience(-xpLeft)
                            experienceStored[0] += xpLeft
                        } else {
                            player.addExperience(-currentXp)
                            experienceStored[0] += currentXp
                        }
                    }
                }
                1 -> {
                    if (currentXp >= 50) {
                        if (xpLeft < 50){
                            player.addExperience(-xpLeft)
                            experienceStored[0] += xpLeft
                        } else {
                            player.addExperience(-50)
                            experienceStored[0] += 50
                        }
                    } else {
                        if (xpLeft < currentXp){
                            player.addExperience(-xpLeft)
                            experienceStored[0] += xpLeft
                        } else {
                            player.addExperience(-currentXp)
                            experienceStored[0] += currentXp
                        }
                    }
                }
                2 -> {
                    if (experienceStored[0] < 50){
                        player.addExperience(experienceStored[0])
                        experienceStored[0] = 0
                    } else {
                        player.addExperience(50)
                        experienceStored[0] -= 50
                    }
                }
                3 -> {
                    if (experienceStored[0] < 500){
                        player.addExperience(experienceStored[0])
                        experienceStored[0] = 0
                    } else {
                        player.addExperience(500)
                        experienceStored[0] -= 500
                    }
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
            setSyncedStoredXp(experienceStored[0])
            xp.set(getPlayerLvlXp(player))
            sendContentUpdates()
        }
        return true
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
        addProperty(xp).set(totalXp)
        addProperty(seed).set(playerInventory.player.enchantmentTableSeed)
        addProperty(Property.create(this.experienceStored, 0))
        addProperty(Property.create(this.experienceMax, 0))
        if (context != ScreenHandlerContext.EMPTY) {
            context.run { world: World, pos: BlockPos ->
                val (c,wc) = checkCandles(world, pos)
                val e = updateMaxXp(c, wc)
                experienceMax[0] = e
                experienceStored[0] = getSyncedStoredXp()
                sendContentUpdates()
            }
        }
    }


}