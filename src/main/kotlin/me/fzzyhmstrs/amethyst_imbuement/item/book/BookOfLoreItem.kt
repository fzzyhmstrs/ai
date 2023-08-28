package me.fzzyhmstrs.amethyst_imbuement.item.book

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.amethyst_imbuement.screen.KnowledgeBookScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

open class BookOfLoreItem(settings: Settings) : AbstractAugmentBookItem(settings), BookOfKnowledge {
    override val loreTier: LoreTier = LoreTier.LOW_TIER
    override val bindingUV: Pair<Int, Int> = Pair(45,184)

    override fun useAfterWriting(
        stack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        val nbt = stack.nbt
        if (nbt != null){
            if (!nbt.contains(NbtKeys.LORE_KEY.str())) return super.useAfterWriting(stack, world, user, hand)
            val bolaId = Identifier(nbt.getString(NbtKeys.LORE_KEY.str()))
            if (Registries.ENCHANTMENT.get(bolaId) == null) return super.useAfterWriting(stack, world, user, hand)
            val bola = bolaId.toString()
            val type = AugmentHelper.getAugmentType(bola)
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                if (nbt.getString(NbtKeys.LORE_TYPE.str()) != type.str()){
                    nbt.putString(NbtKeys.LORE_TYPE.str(),type.str())
                }
            } else {
                nbt.putString(NbtKeys.LORE_TYPE.str(),type.str())
            }

        }
        if (world.isClient){
            world.playSound(user,user.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN,SoundCategory.NEUTRAL,0.7f,1.0f)
            MinecraftClient.getInstance().setScreen(KnowledgeBookScreen(stack))
            return TypedActionResult.success(stack)
        }
        return super.useAfterWriting(stack, world, user, hand)
    }

    override fun getRandomBookAugment(list: List<String>, user: PlayerEntity, hand: Hand): String {
        val stack = if (hand == Hand.MAIN_HAND) user.offHandStack else user.mainHandStack
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
