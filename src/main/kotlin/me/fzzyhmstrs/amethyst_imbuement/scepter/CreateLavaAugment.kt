package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class CreateLavaAugment: PlaceItemAugment(ScepterTier.TWO,1, Items.LAVA_BUCKET){
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,200,50,
            11,imbueLevel,10, LoreTier.LOW_TIER, Items.LAVA_BUCKET)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_BUCKET_EMPTY_LAVA
    }
}