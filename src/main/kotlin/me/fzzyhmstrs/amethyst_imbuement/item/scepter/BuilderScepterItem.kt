package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Oxidizable
import net.minecraft.block.PillarBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.HoneycombItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import java.util.*

class BuilderScepterItem(material: ScepterToolMaterial, settings: Settings
): CustomSpellToolItem(material, 1.5f,-3.0f, BlockTags.SHOVEL_MINEABLE, settings) {

    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return if (state.isIn(BlockTags.AXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE)) 6.0f else 1.0f
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        val world = context.world
        val blockPos = context.blockPos
        val playerEntity = context.player
        val blockState = world.getBlockState(blockPos)
        val optional = getStrippedState(blockState)
        val optional2 = Oxidizable.getDecreasedOxidationState(blockState)
        val optional3 =
            Optional.ofNullable(HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()[blockState.block]).map { block: Block ->
                block.getStateWithProperties(
                    blockState
                )
            }
        val itemStack = context.stack
        var optional4 = Optional.empty<BlockState>()
        if (optional.isPresent) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f)
            optional4 = optional
        } else if (optional2.isPresent) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f)
            world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_SCRAPED, blockPos, 0)
            optional4 = optional2
        } else if (optional3.isPresent) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f)
            world.syncWorldEvent(playerEntity, WorldEvents.WAX_REMOVED, blockPos, 0)
            optional4 = optional3
        }
        if (optional4.isPresent) {
            if (playerEntity is ServerPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger(playerEntity as ServerPlayerEntity?, blockPos, itemStack)
            }
            world.setBlockState(
                blockPos,
                optional4.get(),
                Block.NOTIFY_ALL or Block.REDRAW_ON_MAIN_THREAD
            )
            world.emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                blockPos,
                GameEvent.Emitter.of(playerEntity, optional4.get())
            )
            if (playerEntity != null) {
                itemStack.damage(1, playerEntity) { p: PlayerEntity ->
                    p.sendToolBreakStatus(
                        context.hand
                    )
                }
            }
            return ActionResult.success(world.isClient)
        }
        return ActionResult.PASS
    }

    private fun getStrippedState(state: BlockState): Optional<BlockState> {
        return Optional.ofNullable(AxeItemAccessor.getStrippedBlocks()[state.block]).map { block: Block ->
            block.defaultState
                .with(
                    PillarBlock.AXIS,
                    state.get(PillarBlock.AXIS)
                ) as BlockState
        }
    }

}