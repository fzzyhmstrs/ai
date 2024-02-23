package me.fzzyhmstrs.amethyst_imbuement.item.book

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.compat.ModCompatHelper
import me.fzzyhmstrs.amethyst_imbuement.item.GlisteringKeyItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class GlisteringTomeItem(settings: Settings): Item(settings), GlisteringKeyItem.GlisteringKeyUnlockable {
    override fun use(world: World, playerEntity: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (!world.isClient) {
            if (playerEntity.getStackInHand(hand)?.nbt?.getBoolean("unlocked") == true){
                RegisterCriteria.UNLOCK_BOOK.trigger(playerEntity as ServerPlayerEntity)
            }
            ModCompatHelper.openBookGui(playerEntity as ServerPlayerEntity, Identifier(AI.MOD_ID,"glistering_tome"))
            return TypedActionResult.success(playerEntity.getStackInHand(hand))
        }
        return TypedActionResult.consume(playerEntity.getStackInHand(hand))
    }

    override fun unlock(world: World, blockPos: BlockPos, stack: ItemStack?) {
        stack?.orCreateNbt?.putBoolean("unlocked", true)
    }

    override fun consumeItem(): Boolean {
        return false
    }
}
