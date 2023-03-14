package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items

class SummonBlazeAugment: SummonEntityAugment(ScepterTier.TWO,1){
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,0,0,
            0,imbueLevel,0,LoreTier.LOW_TIER, Items.EMERALD)
    }

}