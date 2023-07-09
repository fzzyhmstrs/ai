package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class CreateSpongeAugment: PlaceItemAugment(ScepterTier.ONE,1, Items.SPONGE){
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,30,8,
            5,imbueLevel,1, LoreTier.LOW_TIER, Items.SPONGE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GRASS_PLACE
    }

}