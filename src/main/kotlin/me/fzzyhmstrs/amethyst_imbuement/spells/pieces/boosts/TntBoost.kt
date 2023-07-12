package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts

import me.fzzyhmstrs.amethyst_core.boost.ItemAugmentBoost
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.block.Blocks

class TntBoost: ItemAugmentBoost(AI.identity("tnt_boost"), Blocks.TNT.asItem()) {


}