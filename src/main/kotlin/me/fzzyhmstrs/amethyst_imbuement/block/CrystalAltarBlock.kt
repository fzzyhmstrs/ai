package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.screen.CrystalAltarScreenHandler
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.block.BlockState
import net.minecraft.block.CraftingTableBlock
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("PrivatePropertyName")
class CrystalAltarBlock(settings: Settings): CraftingTableBlock(settings) {
    private val SCREEN_TITLE: Text = AcText.translatable("container.crystal_altar")

    @Deprecated("Deprecated in Java")
    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory, _: PlayerEntity? ->
            CrystalAltarScreenHandler(
                syncId,
                inventory,
                ScreenHandlerContext.create(world, pos)
            )
        }, SCREEN_TITLE)
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
        player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE)
        return ActionResult.CONSUME
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