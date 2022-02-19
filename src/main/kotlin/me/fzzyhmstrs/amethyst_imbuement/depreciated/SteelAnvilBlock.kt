package me.fzzyhmstrs.amethyst_imbuement.depreciated

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.screen.SteelAnvilScreenHandler2
import net.minecraft.block.AnvilBlock
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("PrivatePropertyName")
class SteelAnvilBlock(settings: Settings): AnvilBlock(settings) {
    private val TITLE: Text = TranslatableText("container.repair")

    override fun createScreenHandlerFactory(
        state: BlockState?,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId: Int, inventory: PlayerInventory, _: PlayerEntity ->
            SteelAnvilScreenHandler2(
                syncId,
                inventory,
                ScreenHandlerContext.create(world, pos)
            )
        }, this.TITLE)
    }

/*
companion object {
    fun getLandingState(fS: BlockState): BlockState? {
        if (fS.isOf(RegisterBlock.STEEL_ANVIL)) {
            return RegisterBlock.STEEL_ANVIL_CHIPPED.defaultState.with(FACING, fS.get(FACING)) as BlockState
        }
        return if (fS.isOf(RegisterBlock.STEEL_ANVIL_CHIPPED)) {
            RegisterBlock.STEEL_ANVIL_DAMAGED.defaultState.with(FACING, fS.get(FACING)) as BlockState
        } else null
    }
}*/
}
