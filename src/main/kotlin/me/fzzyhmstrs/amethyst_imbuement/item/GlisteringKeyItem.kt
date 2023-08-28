package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class GlisteringKeyItem(settings: Settings)
    : 
    CustomFlavorItem(settings)
{

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.fail(stack)
        val offhandStack = user.getStackInHand(if(hand == Hand.MAIN_HAND) Hand.OFF_HAND else Hand.MAIN_HAND)
        val offhandItem = offhandStack.item
        if (offhandItem !is GlisteringKeyUnlockable) return TypedActionResult.fail(stack)
        offhandItem.unlock(world,user.blockPos,offhandStack)
        if (offhandItem.consumeItem()){
            stack.decrement(1)
        }
        world.playSound(null,user.blockPos,RegisterSound.UNLOCK,SoundCategory.PLAYERS,0.8f,1.3f)
        return TypedActionResult.success(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val block = context.world.getBlockState(context.blockPos).block
        if (block !is GlisteringKeyUnlockable) return ActionResult.FAIL
        block.unlock(context.world,context.blockPos, null)
        if (block.consumeItem()){
            context.stack.decrement(1)
        }
        context.world.playSound(null,context.blockPos,RegisterSound.UNLOCK,SoundCategory.PLAYERS,1.0f,1.0f)
        return ActionResult.SUCCESS
    }

    interface GlisteringKeyUnlockable {
        fun unlock(world: World, blockPos: BlockPos, stack: ItemStack?)
        fun consumeItem(): Boolean
    }

}
