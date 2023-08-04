@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object RegisterArmor {

    internal val regArmor: MutableList<Item> = mutableListOf()

    private fun register(item: Item, name: String): Item{
        regArmor.add(item)
        return Registry.register(Registries.ITEM,AI.identity(name), item)
    }
    
    val STEEL_HELMET = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.HELMET,Item.Settings()), "steel_helmet")
    val STEEL_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.CHESTPLATE,Item.Settings()), "steel_chestplate")
    val STEEL_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.LEGGINGS,Item.Settings()), "steel_leggings")
    val STEEL_BOOTS = register(ArmorItem(AiConfig.materials.armor.steel, ArmorItem.Type.BOOTS,Item.Settings()), "steel_boots")
    val AMETRINE_HELMET = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.HELMET,Item.Settings()), "ametrine_helmet")
    val AMETRINE_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.CHESTPLATE,Item.Settings()), "ametrine_chestplate")
    val AMETRINE_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.LEGGINGS,Item.Settings()), "ametrine_leggings")
    val AMETRINE_BOOTS = register(ArmorItem(AiConfig.materials.armor.ametrine, ArmorItem.Type.BOOTS,Item.Settings()),"ametrine_boots")
    val GARNET_HELMET = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.HELMET,Item.Settings()), "garnet_helmet")
    val GARNET_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.CHESTPLATE,Item.Settings()), "garnet_chestplate")
    val GARNET_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.LEGGINGS,Item.Settings()), "garnet_leggings")
    val GARNET_BOOTS = register(ArmorItem(AiConfig.materials.armor.garnet, ArmorItem.Type.BOOTS,Item.Settings()),"garnet_boots")
    val GLOWING_HELMET = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.HELMET,Item.Settings()), "glowing_helmet")
    val GLOWING_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.CHESTPLATE,Item.Settings()), "glowing_chestplate")
    val GLOWING_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.LEGGINGS,Item.Settings()), "glowing_leggings")
    val GLOWING_BOOTS = register(ArmorItem(AiConfig.materials.armor.glowing, ArmorItem.Type.BOOTS,Item.Settings()),"glowing_boots")
    val SHIMMERING_HELMET = register(ArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.HELMET,Item.Settings()), "shimmering_helmet")
    val SHIMMERING_CHESTPLATE = register(ArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.CHESTPLATE,Item.Settings()), "shimmering_chestplate")
    val SHIMMERING_LEGGINGS = register(ArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.LEGGINGS,Item.Settings()), "shimmering_leggings")
    val SHIMMERING_BOOTS = register(ArmorItem(AiConfig.materials.armor.shimmering, ArmorItem.Type.BOOTS,Item.Settings()),"shimmering_boots")
    
    fun registerAll() {}
}
