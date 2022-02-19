package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class GoldenHeartItem(settings: Settings) : Item(settings) {

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip?.add(TranslatableText("item.amethyst_imbuement.golden_heart.tooltip1").formatted(Formatting.WHITE, Formatting.ITALIC))
    }
/* don't need this, but keeping it for reference
    override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack> {
        user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F)
        return TypedActionResult.success(user.getStackInHand(hand))
    }
 */
}