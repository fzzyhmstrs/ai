package me.fzzyhmstrs.amethyst_imbuement.util

import me.emafire003.dev.coloredglowlib.util.Color
import net.minecraft.block.Block
import net.minecraft.block.Blocks

object GlowColorUtil {

    private val oreColorMap: MutableMap<Block,Color> = mutableMapOf()
    private val oreRainbowList: MutableList<Block> = mutableListOf()
    private val defaultColor = Color(255,255,255)

    init{
        applyBuiltInColors()
        applyBuiltinRainbows()
    }

    fun oreIsRainbow(block: Block): Boolean{
        return oreRainbowList.contains(block)
    }

    fun oreGlowColor(block: Block): Color{
        return oreColorMap.getOrDefault(block, defaultColor)
    }

    private fun applyBuiltInColors(){
        oreColorMap[Blocks.DIAMOND_ORE] = Color(30,208,214)
        oreColorMap[Blocks.DEEPSLATE_DIAMOND_ORE] = Color(30,208,214)
        oreColorMap[Blocks.COAL_ORE] = Color(42,42,42)
        oreColorMap[Blocks.DEEPSLATE_COAL_ORE] = Color(42,42,42)
        oreColorMap[Blocks.COPPER_ORE] = Color(224,115,77)
        oreColorMap[Blocks.DEEPSLATE_COPPER_ORE] = Color(224,115,77)
        oreColorMap[Blocks.EMERALD_ORE] = Color(23,221,98)
        oreColorMap[Blocks.DEEPSLATE_EMERALD_ORE] = Color(23,221,98)
        oreColorMap[Blocks.GOLD_ORE] = Color(252,238,78)
        oreColorMap[Blocks.DEEPSLATE_GOLD_ORE] = Color(252,238,78)
        oreColorMap[Blocks.IRON_ORE] = Color(216,175,147)
        oreColorMap[Blocks.DEEPSLATE_IRON_ORE] = Color(216,175,147)
        oreColorMap[Blocks.LAPIS_ORE] = Color(16,52,189)
        oreColorMap[Blocks.DEEPSLATE_LAPIS_ORE] = Color(16,52,189)
        oreColorMap[Blocks.REDSTONE_ORE] = Color(255,0,0)
        oreColorMap[Blocks.DEEPSLATE_REDSTONE_ORE] = Color(255,0,0)
        oreColorMap[Blocks.NETHER_GOLD_ORE] = Color(252,238,78)
        oreColorMap[Blocks.NETHER_QUARTZ_ORE] = Color(166,120,241)
        oreColorMap[Blocks.AMETHYST_CLUSTER] = Color(166,120,241)
        oreColorMap[Blocks.BUDDING_AMETHYST] = Color(166,120,241)
        oreColorMap[Blocks.LARGE_AMETHYST_BUD] = Color(166,120,241)
        oreColorMap[Blocks.MEDIUM_AMETHYST_BUD] = Color(166,120,241)
        oreColorMap[Blocks.SMALL_AMETHYST_BUD] = Color(166,120,241)
        oreColorMap[Blocks.ANCIENT_DEBRIS] = Color(92,53,44)
    }

    private fun applyBuiltinRainbows(){
        //oreRainbowList.add(Blocks.ANCIENT_DEBRIS)
    }

    //to-do
    /*private fun readColorJson(): MutableMap<Block,Color>{
        val colorMap : MutableMap<Block,Color> = mutableMapOf()
        return colorMap
    }*/


}