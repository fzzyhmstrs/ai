package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

class HardLightBlock(settings: Settings): Block(settings) {

    companion object{
        private val PERSISTENT: BooleanProperty = Properties.PERSISTENT
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(PERSISTENT)
    }

    fun getHardLightState(): BlockState{
        return this.defaultState.with(PERSISTENT,false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)?.with(PERSISTENT,true)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "if (state.isOf(RegisterBlock.HARD_LIGHT_BLOCK)) world.removeBlock(pos, false)",
        "me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock"
    )
    )
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (state.isOf(RegisterBlock.HARD_LIGHT_BLOCK))
            world.removeBlock(pos,false)
    }


}