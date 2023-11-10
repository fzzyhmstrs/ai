package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.entity.block.WitchesBookshelfBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class WitchesBookshelfBlock(settings: Settings?) : BlockWithEntity(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return WitchesBookshelfBlockEntity(pos, state)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("BlockRenderType.MODEL", "net.minecraft.block.BlockRenderType"))
    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        val blockEntity = world.getBlockEntity(pos)
        if (itemStack.hasCustomName() && blockEntity is ChestBlockEntity) {
            blockEntity.customName = itemStack.name
        }
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
        if (blockEntity is Inventory) {
            ItemScatterer.spawn(world, pos, blockEntity as Inventory?)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        return ActionResult.CONSUME
    }

    @Deprecated("Deprecated in Java")
    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is WitchesBookshelfBlockEntity) {
            val text = (blockEntity as Nameable).displayName
            return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory, player: PlayerEntity ->
                blockEntity.createMenu(syncId,inventory, player)
            }, text)
        }
        return null
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val dir = ctx.horizontalPlayerFacing
        return super.getPlacementState(ctx)?.with(ChestBlock.FACING,dir.opposite)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(ChestBlock.FACING)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.with(ChestBlock.FACING, rotation.rotate(state.get(ChestBlock.FACING))) as BlockState",
        "net.minecraft.block.ChestBlock",
        "net.minecraft.block.ChestBlock",
        "net.minecraft.block.BlockState"
    )
    )
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        return state.with(ChestBlock.FACING, rotation.rotate(state.get(ChestBlock.FACING))) as BlockState
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "state.rotate(mirror.getRotation(state.get(ChestBlock.FACING)))",
        "net.minecraft.block.ChestBlock"
    )
    )
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        return state.rotate(mirror.getRotation(state.get(ChestBlock.FACING)))
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

    @Deprecated("Deprecated in Java", ReplaceWith("true"))
    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "ScreenHandler.calculateComparatorOutput(ChestBlock.getInventory(this, state, world, pos, false))",
        "net.minecraft.screen.ScreenHandler",
        "net.minecraft.block.ChestBlock"
    )
    )
    override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos) as? WitchesBookshelfBlockEntity as? Inventory)
    }
}