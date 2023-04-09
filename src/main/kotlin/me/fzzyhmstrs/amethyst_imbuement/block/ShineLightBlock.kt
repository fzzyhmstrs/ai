package me.fzzyhmstrs.amethyst_imbuement.block

import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.tag.FluidTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class ShineLightBlock(settings:Settings):Block(settings), Waterloggable {

    private val SHAPE = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0)
    companion object{
        private val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private val PERSISTENT: BooleanProperty = Properties.PERSISTENT
    }


    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED, PERSISTENT)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos
        return super.getPlacementState(ctx)?.with(WATERLOGGED,world.getFluidState(pos).isIn(FluidTags.WATER))?.with(PERSISTENT,true)
    }

    fun getShineState(water: Boolean): BlockState{
        return this.defaultState.with(PERSISTENT,false).with(WATERLOGGED,water)
    }

    @Deprecated("Deprecated in Java")
    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else super.getFluidState(state)
    }

    @Deprecated("Deprecated in Java")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: WorldAccess,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState? {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PistonBehavior.DESTROY", "net.minecraft.block.piston.PistonBehavior")
    )
    override fun getPistonBehavior(state: BlockState?): PistonBehavior {
        return PistonBehavior.DESTROY
    }

    @Deprecated("Deprecated in Java", ReplaceWith("(context.stack.isEmpty || !context.stack.isOf(this.asItem()))"))
    override fun canReplace(state: BlockState?, context: ItemPlacementContext): Boolean {
        return (context.stack.isEmpty || !context.stack.isOf(this.asItem()))
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        if (state.get(PERSISTENT)) return SHAPE
        val entity = if (context is EntityShapeContext) context.entity else return SHAPE
        if (entity !is LivingEntity) return SHAPE
        val stack1 = entity.mainHandStack
        val stack2 = entity.offHandStack
        if (stack1.item is BlockItem || stack2.item is BlockItem) return VoxelShapes.empty()
        return SHAPE
    }

    override fun isTranslucent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun canMobSpawnInside(): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java", ReplaceWith("VoxelShapes.empty()", "net.minecraft.util.shape.VoxelShapes"))
    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val d = pos.x.toDouble() + 0.6 - (random.nextFloat() * 0.2f).toDouble()
        val e = pos.y.toDouble() + 0.6 - (random.nextFloat() * 0.2f).toDouble()
        val f = pos.z.toDouble() + 0.6 - (random.nextFloat() * 0.2f).toDouble()
        if (random.nextInt(2) == 0) {
            world.addParticle(
                ParticleTypes.END_ROD,
                d,
                e ,
                f,
                random.nextGaussian() * 0.005,
                random.nextGaussian() * 0.005,
                random.nextGaussian() * 0.005
            )
        }
    }
}