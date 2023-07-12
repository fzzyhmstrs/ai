package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.DyeBoost
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.util.DyeColor

object RegisterBoost {

    val WHITE_DYE_BOOST = DyeBoost(DyeColor.WHITE, ConventionalItemTags.WHITE_DYES)
    val LIGHT_GRAY_DYE_BOOST = DyeBoost(DyeColor.LIGHT_GRAY, ConventionalItemTags.LIGHT_GRAY_DYES)
    val GRAY_DYE_BOOST = DyeBoost(DyeColor.GRAY, ConventionalItemTags.GRAY_DYES)
    val BLACK_DYE_BOOST = DyeBoost(DyeColor.BLACK, ConventionalItemTags.BLACK_DYES)
    val BROWN_DYE_BOOST = DyeBoost(DyeColor.BROWN, ConventionalItemTags.BROWN_DYES)
    val RED_DYE_BOOST = DyeBoost(DyeColor.RED, ConventionalItemTags.RED_DYES)
    val ORANGE_DYE_BOOST = DyeBoost(DyeColor.ORANGE, ConventionalItemTags.ORANGE_DYES)
    val YELLOW_DYE_BOOST = DyeBoost(DyeColor.YELLOW, ConventionalItemTags.YELLOW_DYES)
    val LIME_DYE_BOOST = DyeBoost(DyeColor.LIME, ConventionalItemTags.LIME_DYES)
    val GREEN_DYE_BOOST = DyeBoost(DyeColor.GREEN, ConventionalItemTags.GREEN_DYES)
    val CYAN_DYE_BOOST = DyeBoost(DyeColor.CYAN, ConventionalItemTags.CYAN_DYES)
    val LIGHT_BLUE_DYE_BOOST = DyeBoost(DyeColor.LIGHT_BLUE, ConventionalItemTags.LIGHT_BLUE_DYES)
    val BLUE_DYE_BOOST = DyeBoost(DyeColor.BLUE, ConventionalItemTags.BLUE_DYES)
    val PURPLE_DYE_BOOST = DyeBoost(DyeColor.PURPLE, ConventionalItemTags.PURPLE_DYES)
    val MAGENTA_DYE_BOOST = DyeBoost(DyeColor.MAGENTA, ConventionalItemTags.MAGENTA_DYES)
    val PINK_DYE_BOOST = DyeBoost(DyeColor.PINK, ConventionalItemTags.PINK_DYES)

    fun registerAll(){

    }

}