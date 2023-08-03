@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object RegisterArmor {

    internal val regArmor: MutableList<Item> = mutableListOf()

    val STEEL_ARMOR_MATERIAL = SteelArmorMaterial()
    val STEEL_HELMET = ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.HELMET,Item.Settings()).also { regArmor.add(it) }
    val STEEL_CHESTPLATE = ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,Item.Settings()).also { regArmor.add(it) }
    val STEEL_LEGGINGS = ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,Item.Settings()).also { regArmor.add(it) }
    val STEEL_BOOTS = ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,Item.Settings()).also { regArmor.add(it) }
    val AMETRINE_ARMOR_MATERIAL = AmetrineArmorMaterial()
    val AMETRINE_HELMET = ArmorItem(AMETRINE_ARMOR_MATERIAL, ArmorItem.Type.HELMET,Item.Settings()).also { regArmor.add(it) }
    val AMETRINE_CHESTPLATE = ArmorItem(AMETRINE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,Item.Settings()).also { regArmor.add(it) }
    val AMETRINE_LEGGINGS = ArmorItem(AMETRINE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,Item.Settings()).also { regArmor.add(it) }
    val AMETRINE_BOOTS = ArmorItem(AMETRINE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,Item.Settings()).also { regArmor.add(it) }

    fun registerAll() {
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"steel_helmet"), STEEL_HELMET)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"steel_chestplate"), STEEL_CHESTPLATE)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"steel_leggings"), STEEL_LEGGINGS)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"steel_boots"), STEEL_BOOTS)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"ametrine_helmet"), AMETRINE_HELMET)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"ametrine_chestplate"), AMETRINE_CHESTPLATE)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"ametrine_leggings"), AMETRINE_LEGGINGS)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"ametrine_boots"), AMETRINE_BOOTS)
    }
}