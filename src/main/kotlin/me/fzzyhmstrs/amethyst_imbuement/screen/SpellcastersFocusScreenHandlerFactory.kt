package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class SpellcastersFocusScreenHandlerFactory(private val stack: ItemStack): ExtendedScreenHandlerFactory {
    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return SpellcastersFocusScreenHandler(syncId, inv,stack, ScreenHandlerContext.create(player.world,player.blockPos))
    }

    override fun getDisplayName(): Text {
        return AcText.translatable("container.spellcasters_focus")
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeByte(player.inventory.getSlotWithStack(stack))
    }
}