package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.item_util.AugmentScepterItem
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class BookOfLoreItem(settings: Settings) : AbstractAugmentBookItem(settings) {
    override val loreTier: LoreTier = LoreTier.LOW_TIER

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
