package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.chest.ChestHelper
import me.fzzyhmstrs.amethyst_imbuement.entity.block.GildedLockboxBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.item.GlisteringKeyItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.enums.ChestType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.mob.PiglinBrain
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.stat.Stat
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class GildedLockboxBlock(
    settings: Settings) : AbstractChestBlock<GildedLockboxBlockEntity>(settings, { RegisterEntity.GILDED_LOCKBOX_BLOCK_ENTITY }),
    GlisteringKeyItem.GlisteringKeyUnlockable {

    companion object{
        private val SHAPE = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
    }

    init{
        defaultState = defaultState.with(EnderChestBlock.FACING, Direction.NORTH).with(ChestBlock.CHEST_TYPE, ChestType.SINGLE).with(EnderChestBlock.WATERLOGGED, false)

    }

    override fun onPlaced(
        world: World,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if (itemStack.hasCustomName() && blockEntity is GildedLockboxBlockEntity) {
            (blockEntity).customName = itemStack.name
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is GildedLockboxBlockEntity){
            if (!blockEntity.unlocked) {
                player.sendMessage(AcText.translatable(this.translationKey + ".locked"), true)
                world.playSound(null,pos,RegisterSound.LOCKED_BOOK,SoundCategory.BLOCKS,1f,1f)
                return ActionResult.PASS
            }
        } else {
            return ActionResult.FAIL
        }
        val namedScreenHandlerFactory = createScreenHandlerFactory(state, world, pos)
        if (namedScreenHandlerFactory != null) {
            player.openHandledScreen(namedScreenHandlerFactory)
            player.incrementStat(this.getOpenStat())
            PiglinBrain.onGuardedBlockInteracted(player, true)
        }
        return ActionResult.CONSUME
    }

    private fun getOpenStat(): Stat<Identifier> {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST)
    }

    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape? {
        return SHAPE
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("BlockRenderType.ENTITYBLOCK_ANIMATED", "net.minecraft.block.BlockRenderType")
    )
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.ENTITYBLOCK_ANIMATED
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val fluidState = ctx.world.getFluidState(ctx.blockPos)
        return (defaultState.with(EnderChestBlock.FACING, ctx.horizontalPlayerFacing.opposite) as BlockState).with(
            EnderChestBlock.WATERLOGGED,
            fluidState.fluid === Fluids.WATER
        ) as BlockState
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (world.isClient) checkType(
            type, RegisterEntity.GILDED_LOCKBOX_BLOCK_ENTITY
        ) { wrld: World, pos: BlockPos, state2: BlockState, blockEntity: GildedLockboxBlockEntity ->
            GildedLockboxBlockEntity.tick(
                wrld,
                pos,
                state2,
                blockEntity
            )
        } else null
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.with(EnderChestBlock.FACING, rotation.rotate(state.get(EnderChestBlock.FACING))) as BlockState",
        "net.minecraft.block.EnderChestBlock",
        "net.minecraft.block.EnderChestBlock",
        "net.minecraft.block.BlockState"
    )
    )
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        return state.with(EnderChestBlock.FACING, rotation.rotate(state.get(EnderChestBlock.FACING))) as BlockState
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.rotate(mirror.getRotation(state.get(EnderChestBlock.FACING)))",
        "net.minecraft.block.EnderChestBlock"
    )
    )
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        return state.rotate(mirror.getRotation(state.get(EnderChestBlock.FACING)))
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(EnderChestBlock.FACING, EnderChestBlock.WATERLOGGED, ChestBlock.CHEST_TYPE)
    }

    @Deprecated("Deprecated in Java")
    override fun getFluidState(state: BlockState): FluidState? {
        return if (state.get(EnderChestBlock.WATERLOGGED)) {
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
        if (state.get(EnderChestBlock.WATERLOGGED)) {
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
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is GildedLockboxBlockEntity) {
            if (!blockEntity.unlocked) {
                super.onStateReplaced(state, world, pos, newState, moved)
                return
            }
            ItemScatterer.spawn(world, pos, blockEntity as Inventory)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun canPathfindThrough(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        type: NavigationType?
    ): Boolean {
        return false
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return GildedLockboxBlockEntity(RegisterEntity.GILDED_LOCKBOX_BLOCK_ENTITY, pos, state)
    }

    override fun getBlockEntitySource(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        ignoreBlocked: Boolean
    ): DoubleBlockProperties.PropertySource<out ChestBlockEntity> {
        return ChestHelper.getBlockEntitySource(state, world, pos, ignoreBlocked)
    }

    override fun unlock(world: World, blockPos: BlockPos, stack: ItemStack?) {
        val blockEntity = world.getBlockEntity(blockPos)
        (blockEntity as? GildedLockboxBlockEntity)?.unlock(world, blockPos, stack)
    }

    override fun consumeItem(): Boolean {
        return true
    }
}