package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FragmentAid {

    companion object{
        val INSTANCE = FragmentAid()
        private val searchArray = intArrayOf(0,1,-1,2,-2,3,-3)
    }

    fun summon(world: World,pos: BlockPos, shouldEnrage: Boolean){
        world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.HOSTILE, 1.0F, 1.0F)
        for(i in 1..3) {
            val spawnPos = findSpawnPos(world,pos,4,1)
            if (spawnPos == BlockPos.ORIGIN) continue

            val sardonyxFragmentEntity = RegisterEntity.SARDONYX_FRAGMENT.create(world) ?: continue
            sardonyxFragmentEntity.setEnraged(shouldEnrage, false)
            sardonyxFragmentEntity.refreshPositionAndAngles(spawnPos.x + 0.5,spawnPos.y + 0.5, spawnPos.z + 0.5,0f,0f)
            world.spawnEntity(sardonyxFragmentEntity)
        }
    }

    private fun findSpawnPos(world: World, startPos: BlockPos, radius: Int, heightNeeded: Int, blockNeeded: Block = Blocks.AIR, tries: Int = 8): BlockPos{
        for (i in 1..AiConfig.entities.sardonyxElemental.fragmentsSpawned.get()){
            val xPos = startPos.x + world.random.nextBetween(-radius,radius)
            val yPos = startPos.up().y
            val zPos = startPos.z + world.random.nextBetween(-radius,radius)
            for (j in searchArray){
                val testPos = BlockPos(xPos,yPos + j,zPos)
                if (world.getBlockState(testPos).isOf(blockNeeded)){
                    if (heightNeeded > 1){
                        var found2 = true
                        for (k in 1 until heightNeeded){
                            if (!world.getBlockState(testPos.up(k)).isOf(blockNeeded)){
                                found2 = false
                                break
                            }
                        }
                        if (!found2) continue
                    }

                }
                return testPos
            }
        }
        return BlockPos.ORIGIN
    }

}