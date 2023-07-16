package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AbundanceExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun setFireBlockState(world: World, pos: BlockPos) {
        val block = when(AI.aiRandom().nextInt(4)) {
            0 -> Blocks.POPPY
            1 -> Blocks.OXEYE_DAISY
            2 -> Blocks.DANDELION
            else -> Blocks.AZURE_BLUET
        }
        val state = block.defaultState
        if (state.canPlaceAt(world, pos)){
            world.setBlockState(pos, state)
        }
        world.setBlockState(pos,Blocks.MOSS_CARPET.defaultState)
    }
}