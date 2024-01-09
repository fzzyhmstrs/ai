@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings
import me.fzzyhmstrs.amethyst_imbuement.item.armor.ShimmeringArmorItem
import me.fzzyhmstrs.amethyst_imbuement.item.armor.SoulwovenArmorItem
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item

object RegisterArmor {

    internal val regArmor: MutableList<Item> = mutableListOf()

    private fun <T: ArmorItem> register(item: T, name: String): T{
        regArmor.add(item)
        return FzzyPort.ITEM.register(AI.identity(name), item)
    }
    
    val STEEL_HELMET = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.HELMET, AiItemSettings()), "steel_helmet")
    val STEEL_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "steel_chestplate")
    val STEEL_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.LEGGINGS,AiItemSettings()), "steel_leggings")
    val STEEL_BOOTS = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.BOOTS,AiItemSettings()), "steel_boots")
    val AMETRINE_HELMET = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.HELMET,AiItemSettings()), "ametrine_helmet")
    val AMETRINE_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "ametrine_chestplate")
    val AMETRINE_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.LEGGINGS,AiItemSettings()), "ametrine_leggings")
    val AMETRINE_BOOTS = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.BOOTS,AiItemSettings()),"ametrine_boots")
    val GARNET_HELMET = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.HELMET,AiItemSettings()), "garnet_helmet")
    val GARNET_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "garnet_chestplate")
    val GARNET_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.LEGGINGS,AiItemSettings()), "garnet_leggings")
    val GARNET_BOOTS = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.BOOTS,AiItemSettings()),"garnet_boots")
    val GLOWING_HELMET = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.HELMET,AiItemSettings()), "glowing_helmet")
    val GLOWING_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "glowing_chestplate")
    val GLOWING_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.LEGGINGS,AiItemSettings()), "glowing_leggings")
    val GLOWING_BOOTS = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.BOOTS,AiItemSettings()),"glowing_boots")
    val SHIMMERING_HELMET = register(ShimmeringArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.HELMET,AiItemSettings()), "shimmering_helmet")
    val SHIMMERING_CHESTPLATE = register(ShimmeringArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "shimmering_chestplate")
    val SHIMMERING_LEGGINGS = register(ShimmeringArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.LEGGINGS,AiItemSettings()), "shimmering_leggings")
    val SHIMMERING_BOOTS = register(ShimmeringArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.BOOTS,AiItemSettings()),"shimmering_boots")
    val SOULWOVEN_HELMET = register(SoulwovenArmorItem(AiConfig.materials.armor.soulwoven, ArmorItem.Type.HELMET,AiItemSettings()), "soulwoven_helmet")
    val SOULWOVEN_CHESTPLATE = register(SoulwovenArmorItem(AiConfig.materials.armor.soulwoven, ArmorItem.Type.CHESTPLATE,AiItemSettings()), "soulwoven_chestplate")
    val SOULWOVEN_LEGGINGS = register(SoulwovenArmorItem(AiConfig.materials.armor.soulwoven, ArmorItem.Type.LEGGINGS,AiItemSettings()), "soulwoven_leggings")
    val SOULWOVEN_BOOTS = register(SoulwovenArmorItem(AiConfig.materials.armor.soulwoven, ArmorItem.Type.BOOTS,AiItemSettings()),"soulwoven_boots")

    fun registerAll() {}
}