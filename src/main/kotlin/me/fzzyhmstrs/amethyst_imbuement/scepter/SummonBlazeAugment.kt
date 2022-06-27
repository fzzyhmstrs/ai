package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.FireAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items

class SummonBlazeAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot),
    FireAugment {
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,0,0,0,imbueLevel,LoreTier.LOW_TIER, Items.EMERALD)
    }

}