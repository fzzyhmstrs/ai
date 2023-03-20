package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.PlaceItemAugment
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class ShineAugment: PlaceItemAugment(ScepterTier.ONE,1,Items.TORCH) {

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,10,2,
            1,imbueLevel,1,LoreTier.NO_TIER, Items.TORCH)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_WOOD_PLACE
    }
}