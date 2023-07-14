package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.boost.AugmentBoost
import me.fzzyhmstrs.amethyst_core.boost.ItemAugmentBoost
import me.fzzyhmstrs.amethyst_core.registry.BoostRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.DyeBoost
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.TntBoost
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.item.Items
import net.minecraft.util.DyeColor

object RegisterBoost {

    private val regBoost: MutableSet<AugmentBoost> = mutableSetOf()


    val HEARTSTONE_BOOST = ItemAugmentBoost(AI.identity("heartstone_boost"), RegisterItem.HEARTSTONE)
    val ENCHANTED_GOLDEN_APPLE_BOOST = ItemAugmentBoost(AI.identity("enchanted_golden_apple_boost"), Items.ENCHANTED_GOLDEN_APPLE)
    val TNT_BOOST = TntBoost().also { regBoost.add(it) }


    val WHITE_DYE_BOOST = DyeBoost(DyeColor.WHITE, ConventionalItemTags.WHITE_DYES).also { regBoost.add(it) }
    val LIGHT_GRAY_DYE_BOOST = DyeBoost(DyeColor.LIGHT_GRAY, ConventionalItemTags.LIGHT_GRAY_DYES).also { regBoost.add(it) }
    val GRAY_DYE_BOOST = DyeBoost(DyeColor.GRAY, ConventionalItemTags.GRAY_DYES).also { regBoost.add(it) }
    val BLACK_DYE_BOOST = DyeBoost(DyeColor.BLACK, ConventionalItemTags.BLACK_DYES).also { regBoost.add(it) }
    val BROWN_DYE_BOOST = DyeBoost(DyeColor.BROWN, ConventionalItemTags.BROWN_DYES).also { regBoost.add(it) }
    val RED_DYE_BOOST = DyeBoost(DyeColor.RED, ConventionalItemTags.RED_DYES).also { regBoost.add(it) }
    val ORANGE_DYE_BOOST = DyeBoost(DyeColor.ORANGE, ConventionalItemTags.ORANGE_DYES).also { regBoost.add(it) }
    val YELLOW_DYE_BOOST = DyeBoost(DyeColor.YELLOW, ConventionalItemTags.YELLOW_DYES).also { regBoost.add(it) }
    val LIME_DYE_BOOST = DyeBoost(DyeColor.LIME, ConventionalItemTags.LIME_DYES).also { regBoost.add(it) }
    val GREEN_DYE_BOOST = DyeBoost(DyeColor.GREEN, ConventionalItemTags.GREEN_DYES).also { regBoost.add(it) }
    val CYAN_DYE_BOOST = DyeBoost(DyeColor.CYAN, ConventionalItemTags.CYAN_DYES).also { regBoost.add(it) }
    val LIGHT_BLUE_DYE_BOOST = DyeBoost(DyeColor.LIGHT_BLUE, ConventionalItemTags.LIGHT_BLUE_DYES).also { regBoost.add(it) }
    val BLUE_DYE_BOOST = DyeBoost(DyeColor.BLUE, ConventionalItemTags.BLUE_DYES).also { regBoost.add(it) }
    val PURPLE_DYE_BOOST = DyeBoost(DyeColor.PURPLE, ConventionalItemTags.PURPLE_DYES).also { regBoost.add(it) }
    val MAGENTA_DYE_BOOST = DyeBoost(DyeColor.MAGENTA, ConventionalItemTags.MAGENTA_DYES).also { regBoost.add(it) }
    val PINK_DYE_BOOST = DyeBoost(DyeColor.PINK, ConventionalItemTags.PINK_DYES).also { regBoost.add(it) }

    fun registerAll(){
        for (boost in regBoost) {
            BoostRegistry.register(boost)
        }
    }

}