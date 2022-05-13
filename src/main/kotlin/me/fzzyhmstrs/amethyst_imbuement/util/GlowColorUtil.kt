package me.fzzyhmstrs.amethyst_imbuement.util

import me.emafire003.dev.coloredglowlib.util.Color
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object GlowColorUtil {

    fun oreGlowColor(block: Block): Color{
        val oreId = Registry.BLOCK.getId(block).toString()
        val colorString = AiConfig.colors.modColorMap[oreId]?:AiConfig.colors.defaultColorMap[oreId]?:"#FFFFFF"
        return parseColor(colorString)
    }
    private fun parseColor(colorString: String): Color{
        return if(Color.isHexColor(colorString)){
            Color.translateFromHEX(colorString)
        } else {
            Color.getWhiteColor()
        }
    }
    fun oreIsRainbow(block: Block): Boolean{
        val oreId = Registry.BLOCK.getId(block).toString()
        return AiConfig.colors.defaultRainbowList.contains(oreId) || AiConfig.colors.modRainbowList.contains(oreId)
    }
}