package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts

import me.fzzyhmstrs.amethyst_core.boost.TagAugmentBoost
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.DyeColor

class DyeBoost(private val color: DyeColor, dyes: TagKey<Item>): TagAugmentBoost(AI.identity("${color.getName()}_dye_boost"),dyes) {
    fun getDyeColor(): DyeColor{
        return color
    }
}