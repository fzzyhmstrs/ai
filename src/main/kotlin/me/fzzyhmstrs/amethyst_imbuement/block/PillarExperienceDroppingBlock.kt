package me.fzzyhmstrs.amethyst_imbuement.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ExperienceDroppingBlock
import net.minecraft.block.PillarBlock
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.world.BlockView

class PillarExperienceDroppingBlock(settings: Settings, experience: IntProvider) : ExperienceDroppingBlock(settings, experience) {

    init {
        defaultState = defaultState.with(PillarBlock.AXIS, Direction.Axis.Y)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PillarBlock.changeRotation(state, rotation)", "net.minecraft.block.PillarBlock")
    )
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        return PillarBlock.changeRotation(state, rotation)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(PillarBlock.AXIS)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(PillarBlock.AXIS, ctx.side.axis) as BlockState
    }

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun canPathfindThrough(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        type: NavigationType?
    ): Boolean {
        return false
    }
}