package me.fzzyhmstrs.amethyst_imbuement.enchantment

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.block.Block
import net.minecraft.block.OperatorBlock
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World

class VeinMinerEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight,EnchantmentTarget.DIGGER,*slot) {

    override fun getMinPower(level: Int): Int {
        return 30 + (level - 1) * 20
    }

    override fun getMaxPower(level: Int): Int {
        return this.getMinPower(level) + 30
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    override fun canAccept(other: Enchantment): Boolean {
        return super.canAccept(other) && other !== Enchantments.FORTUNE
    }

    override fun isAcceptableItem(stack: ItemStack?): Boolean {
        return super.isAcceptableItem(stack) && checkEnabled()
    }

    companion object{

        private val blockMap: MutableMap<Int,MutableList<BlockPos>> = mutableMapOf()
        private val rawList: MutableList<BlockPos> = mutableListOf()
        private const val maxDistance = 6

        fun veinMine(world: World, pos: BlockPos,block: Block, miner: ServerPlayerEntity, level: Int){
            val blockBudget = 4 * level - 1
            blockMap[0] = mutableListOf()
            blockMap[0]?.add(pos)
            //build map of blocks
            for (currentDistance in 0..maxDistance){
                if (blockMap.containsKey(currentDistance)) {
                    val distanceList = blockMap[currentDistance]
                    if (distanceList != null) {
                        for (bp in distanceList){
                            blockChecker(world,bp,pos,block,currentDistance)
                        }
                    }
                    if (rawList.size >= blockBudget) break
                } else {
                    continue
                }
            }

            var remainingBlockBudget = blockBudget
            for (priority in 1..maxDistance) {
                val priorityList = blockMap[priority]
                if (priorityList != null) {
                    priorityList.shuffle(AI.aiKotlinRandom())
                    for (bp2 in priorityList){
                        tryBreakBlock(bp2,world,miner)
                        remainingBlockBudget--
                        if (remainingBlockBudget <= 0) break
                    }
                }
            }

            rawList.clear()
            blockMap.clear()
        }

        private fun blockChecker(world: World,startingBlockPos: BlockPos,originBlockPos: BlockPos, referenceBlock: Block, minDistance: Int){
            for (x in -1..1){
                for (y in -1..1){
                    for (z in -1..1){
                        val checkBp = startingBlockPos.add(x,y,z)
                        val distance = checkDistance(checkBp,originBlockPos)
                        if (distance > minDistance) {
                            val checkBs = world.getBlockState(checkBp)
                            if (checkBs.isAir) continue
                            if (checkBs.isOf(referenceBlock)){
                                if (!rawList.contains(checkBp)){
                                    if (!blockMap.containsKey(distance)){
                                        blockMap[distance] = mutableListOf()
                                    }
                                    blockMap[distance]?.add(checkBp)
                                    rawList.add(checkBp)
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun checkDistance(bp1: BlockPos, bp2:BlockPos): Int{
            return bp1.getManhattanDistance(Vec3i(bp2.x,bp2.y,bp2.z))
        }

        private fun tryBreakBlock(pos: BlockPos, world: World, player: ServerPlayerEntity): Boolean {
            val blockState = world.getBlockState(pos)
            if (!player.mainHandStack.item.canMine(blockState, world, pos, player) || !CommonProtection.canBreakBlock(world,pos,player.gameProfile,player)) {
                return false
            }
            val blockEntity = world.getBlockEntity(pos)
            val block = blockState.block
            if (block is OperatorBlock && !player.isCreativeLevelTwoOp) {
                world.updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL)
                return false
            }
            block.onBreak(world, pos, blockState, player)
            val bl = world.removeBlock(pos, false)
            if (bl) {
                block.onBroken(world, pos, blockState)
            }
            if (player.isCreative) {
                return true
            }
            val itemStack = player.mainHandStack
            val itemStack2 = itemStack.copy()
            val bl2 = player.canHarvest(blockState)
            if (bl && bl2) {
                block.afterBreak(world, player, pos, blockState, blockEntity, itemStack2)
            }
            return true
        }
    }

}