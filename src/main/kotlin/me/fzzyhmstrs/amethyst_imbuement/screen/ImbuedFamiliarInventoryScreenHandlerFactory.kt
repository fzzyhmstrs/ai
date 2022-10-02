package me.fzzyhmstrs.amethyst_imbuement.screen

import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry

class ImbuedFamiliarInventoryScreenHandlerFactory(
    private val familiarEntity: ImbuedFamiliarEntity,
    private val modDamage: Double = 0.0,
    private val modHealth: Double = 0.0,
    private val invSlots: Int = 3,
    private val level: Int = 1): ExtendedScreenHandlerFactory {

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ImbuedFamiliarInventoryScreenHandler(syncId,familiarEntity,familiarEntity.id, player.inventory,
            ScreenHandlerContext.create(familiarEntity.world, familiarEntity.blockPos))
    }

    override fun getDisplayName(): Text {
        return familiarEntity.name
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeDouble(modDamage)
        buf.writeDouble(modHealth)
        buf.writeInt(invSlots)
        buf.writeInt(level)
        buf.writeInt(familiarEntity.followMode.index)
        buf.writeInt(familiarEntity.followMode.index)
        buf.writeInt(familiarEntity.id)
    }

}