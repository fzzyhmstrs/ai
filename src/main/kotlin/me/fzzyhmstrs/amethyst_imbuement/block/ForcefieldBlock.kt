package me.fzzyhmstrs.amethyst_imbuement.block

import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

@Suppress("PrivatePropertyName", "DEPRECATION", "DeprecatedCallableAddReplaceWith")
class ForcefieldBlock(settings: Settings) : Block(settings) {

    fun getWaterState(waterState: Boolean): BlockState{
        return if (waterState) {
            defaultState.with(WATER_SPAWN, true)
        } else {
            defaultState.with(WATER_SPAWN, false)
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val bs =  super.getPlacementState(ctx)
        val pos = ctx.blockPos
        val bs2: BlockState = ctx.world.getBlockState(pos)
        return if (bs != null) {
            if (bs2.isOf(Blocks.WATER)) {
                bs.with(WATER_SPAWN, true)
            } else {
                bs.with(WATER_SPAWN, false)
            }
        } else {
            if (bs2.isOf(Blocks.WATER)) {
                defaultState.with(WATER_SPAWN, true)
            } else {
                defaultState.with(WATER_SPAWN, false)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        replace(state, Blocks.AIR.defaultState, world, pos, SKIP_DROPS)
        if (state.get(WATER_SPAWN)) {
            world.setBlockState(pos,Blocks.WATER.defaultState)
        }
        super.scheduledTick(state, world, pos, random)
    }

    override fun isTransparent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun isSideInvisible(state: BlockState?, stateFrom: BlockState, direction: Direction?): Boolean {
        return if (stateFrom.isOf(this)) {
            true
        } else super.isSideInvisible(state, stateFrom, direction)
    }

    override fun canMobSpawnInside(state: BlockState): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        type: NavigationType
    ): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    @Deprecated("Deprecated in Java")
    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        if (context is EntityShapeContext){
            val entity = context.entity
            if (entity is PlayerEntity){
                return VoxelShapes.empty()
            }
            if (entity is ProjectileEntity){
                println(entity.owner)
                if (entity.owner is PlayerEntity) {
                    return VoxelShapes.empty()
                }
            }
        }
        return if (collidable) state.getOutlineShape(world, pos) else VoxelShapes.empty()
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WATER_SPAWN)
    }

    companion object{
        private val WATER_SPAWN: BooleanProperty = Properties.WATERLOGGED
    }

}