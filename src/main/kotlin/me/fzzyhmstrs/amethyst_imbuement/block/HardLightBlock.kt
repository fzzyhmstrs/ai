package me.fzzyhmstrs.amethyst_imbuement.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties

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


}