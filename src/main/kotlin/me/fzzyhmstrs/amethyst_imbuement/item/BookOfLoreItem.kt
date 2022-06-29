package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentBookItem
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier

open class BookOfLoreItem(settings: Settings) : AbstractAugmentBookItem(settings) {
    override val loreTier: LoreTier = LoreTier.LOW_TIER
}
