package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.amethyst_imbuement.screen.KnowledgeBookScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class BookOfMythosItem(settings: Settings) : AbstractAugmentBookItem(settings), BookOfKnowledge {
    override val loreTier: LoreTier = LoreTier.HIGH_TIER
    override val bindingUV: Pair<Int, Int> = Pair(63,184)

    override fun useAfterWriting(
        stack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient){
            world.playSound(user,user.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN,SoundCategory.NEUTRAL,0.7f,1.0f)
            MinecraftClient.getInstance().setScreen(KnowledgeBookScreen(stack))
            return TypedActionResult.success(stack)
        }
        return super.useAfterWriting(stack, world, user, hand)
    }

    override fun getRandomBookAugment(list: List<String>, user: PlayerEntity, hand: Hand): String {
        val stack = if (hand == Hand.MAIN_HAND){
            user.offHandStack
        } else {
            user.mainHandStack
        }
        if (stack.isIn(RegisterTag.ALL_FURY_SCEPTERS_TAG)){
            for (i in 1..2){
                val aug = super.getRandomBookAugment(list, user, hand)
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.FURY){
                    return aug
                }
            }
        } else if (stack.isIn(RegisterTag.ALL_WIT_SCEPTERS_TAG)){
            for (i in 1..2){
                val aug = super.getRandomBookAugment(list, user, hand)
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.WIT){
                    return aug
                }
            }
        } else if (stack.isIn(RegisterTag.ALL_GRACE_SCEPTERS_TAG)){
            for (i in 1..2){
                val aug = super.getRandomBookAugment(list, user, hand)
                val type = AugmentHelper.getAugmentType(aug)
                if (type == SpellType.GRACE){
                    return aug
                }
            }
        }

        return super.getRandomBookAugment(list, user, hand)
    }
}
