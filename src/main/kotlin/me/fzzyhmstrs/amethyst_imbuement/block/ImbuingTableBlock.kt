package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuingTableBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingTableScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.EnchantingTableBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.Nameable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

@Suppress("SpellCheckingInspection", "unused", "PrivatePropertyName")
class ImbuingTableBlock(settings: Settings): EnchantingTableBlock(settings) {

    private val SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0)

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (world.isClient) {
            checkType(
                type, RegisterEntity.IMBUING_TABLE_BLOCK_ENTITY
            ) { wrld: World, pos: BlockPos, state2: BlockState, blockEntity: ImbuingTableBlockEntity ->
                ImbuingTableBlockEntity.tick(
                    wrld,
                    pos,
                    state2,
                    blockEntity
                )
            }
        } else {
            null
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ImbuingTableBlockEntity(pos, state)
    }
    override fun onPlaced(
        world: World,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if (itemStack.hasCustomName() && blockEntity is ImbuingTableBlockEntity) {
            (blockEntity).customName = itemStack.name
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.isOf(newState.block)) {
            return
        }
        val entity = world.getBlockEntity(pos)
        if (entity is ImbuingTableBlockEntity){
            ItemScatterer.spawn(world,pos,entity.inventory)
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
        if (blockEntity is ImbuingTableBlockEntity) {
            val text = (blockEntity as Nameable).displayName
            return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory, _: PlayerEntity ->
                blockEntity.setInUse(inventory.player.uuid)
                ImbuingTableScreenHandler(
                    syncId,
                    blockEntity,
                    inventory,
                    blockEntity.inventory,
                    ScreenHandlerContext.create(world, pos)
                )
            }, text)
        }
        return null
    }

    override fun randomDisplayTick(state: BlockState?, world: World, pos: BlockPos, random: Random) {
        super.randomDisplayTick(state, world, pos, random)
        for (i in -2..2) {
            var j = -2
            block1@ while (j <= 2) {
                if (i > -2 && i < 2 && j == -1) {
                    j = 2
                }
                if (random.nextInt(16) != 0) {
                    ++j
                    continue
                }
                for (k in 0..1) {
                    val blockPos = pos.add(i, k, j)
                    if (!world.getBlockState(blockPos).isOf(Blocks.BOOKSHELF)) continue
                    if (!world.isAir(pos.add(i / 2, 0, j / 2))) {
                        ++j
                        continue@block1
                    }
                    world.addParticle(
                        ParticleTypes.ENCHANT,
                        pos.x.toDouble() + 0.5,
                        pos.y.toDouble() + 2.0,
                        pos.z.toDouble() + 0.5,
                        (i.toFloat() + random.nextFloat()).toDouble() - 0.5,
                        (k.toFloat() - random.nextFloat() - 1.0f).toDouble(),
                        (j.toFloat() + random.nextFloat()).toDouble() - 0.5
                    )
                }
                ++j
            }
        }
    }
}