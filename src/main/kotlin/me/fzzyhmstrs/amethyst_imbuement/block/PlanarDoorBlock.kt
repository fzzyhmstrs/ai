package me.fzzyhmstrs.amethyst_imbuement.block

import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.FluidTags
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class PlanarDoorBlock(settings:Settings):BlockWithEntity(settings), Waterloggable {

    companion object{
        public val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
    }

    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?): ItemStack {
        return ItemStack.EMPTY.copy()
    }

    @Deprecated("Deprecated in Java")
    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (world !is ServerWorld) return
        if (PlanarDoorAugment.getAndUpdateDoorStatus(entity, pos)) return
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is PlanarDoorBlockEntity) return
        if (!((entity is PlayerEntity && AiConfig.entities.isEntityPvpTeammate(blockEntity.owner,entity,RegisterEnchantment.PLANAR_DOOR)) 
              || entity is AnimalEntity 
              || entity is MinecartEntity 
              || entity is ItemEntity)) {
            return
        }
        val newPos = blockEntity.partnerBlockPos
        //put the check for just-teleported here
        val newDim = blockEntity.getPartnerWorld(world) ?: return
        PlanarDoorAugment.addEntityTeleported(entity,newPos)
        val p = pewPos.centerPos.add(0.0,-0.5,0.0)
        //Add the just-teleported bit
        entity.teleport(newDim, p.x, p.y, p.z, setOf(), entity.yaw, entity.pitch)
        newDim.playSound(null,newPos,SoundEvents.BLOCK_PORTAL_TRAVEL,SoundCategory.BLOCKS,0.2f,1.0f)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PlanarDoorBlockEntity(RegisterEntity.PLANAR_DOOR_BLOCK_ENTITY, pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) checkType(
            type, RegisterEntity.PLANAR_DOOR_BLOCK_ENTITY
        ) { wrld: World, pos: BlockPos, state2: BlockState, blockEntity: PlanarDoorBlockEntity ->
            PlanarDoorBlockEntity.tick(
                wrld,
                pos,
                state2,
                blockEntity
            )
        } else null
    }
    
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos
        return super.getPlacementState(ctx)?.with(WATERLOGGED,world.getFluidState(pos).isIn(FluidTags.WATER))
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
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.isOf(newState.block)) {
            return
        }
        (world.getBlockEntity(pos) as? PlanarDoorBlockEntity).clearPartner(world)
        super.onStateReplaced(state, world, pos, newState, moved)
    }


    @Deprecated("Deprecated in Java", ReplaceWith("(context.stack.isEmpty || !context.stack.isOf(this.asItem()))"))
    override fun canReplace(state: BlockState?, context: ItemPlacementContext): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        return VoxelShapes.empty()
    }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun canMobSpawnInside(state: BlockState): Boolean {
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
}
