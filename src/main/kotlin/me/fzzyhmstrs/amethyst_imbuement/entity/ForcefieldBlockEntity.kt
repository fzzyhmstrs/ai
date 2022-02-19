package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ForcefieldBlockEntity(pos: BlockPos, state: BlockState, _waterState: Boolean = false) : BlockEntity(RegisterEntity.FORCEFIELD_BLOCK_ENTITY, pos, state) {

    private var maxAge: Int = 0
    private var age: Int = 0
    private val baseAge = 2400
    private val waterState = _waterState

    companion object{
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: ForcefieldBlockEntity) {
            if (blockEntity.maxAge == 0){
                val rnd0 = world.random.nextInt(4)
                blockEntity.maxAge = if (rnd0 != 0) {
                    blockEntity.baseAge
                } else {
                    val rnd1 = world.random.nextInt(10)
                    val rnd2 = world.random.nextInt(10-rnd1)
                    blockEntity.baseAge - 20 * rnd2
                }
            }
            blockEntity.age++
            if (blockEntity.age >= blockEntity.maxAge){
                Block.replace(state, Blocks.AIR.defaultState, world, pos, Block.SKIP_DROPS)
                if (blockEntity.waterState) {
                    world.setBlockState(pos,Blocks.WATER.defaultState)
                }
                blockEntity.markRemoved()
            }
        }
    }

}