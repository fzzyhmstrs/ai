@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.armor.AmetrineArmorMaterial
import me.fzzyhmstrs.amethyst_imbuement.armor.SteelArmorMaterial
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterArmor {
    val STEEL_ARMOR_MATERIAL = SteelArmorMaterial()
    val STEEL_HELMET = ArmorItem(STEEL_ARMOR_MATERIAL, EquipmentSlot.HEAD,Item.Settings().group(ItemGroup.COMBAT))
    val STEEL_CHESTPLATE = ArmorItem(STEEL_ARMOR_MATERIAL, EquipmentSlot.CHEST,Item.Settings().group(ItemGroup.COMBAT))
    val STEEL_LEGGINGS = ArmorItem(STEEL_ARMOR_MATERIAL, EquipmentSlot.LEGS,Item.Settings().group(ItemGroup.COMBAT))
    val STEEL_BOOTS = ArmorItem(STEEL_ARMOR_MATERIAL, EquipmentSlot.FEET,Item.Settings().group(ItemGroup.COMBAT))
    val AMETRINE_ARMOR_MATERIAL = AmetrineArmorMaterial()
    val AMETRINE_HELMET = ArmorItem(AMETRINE_ARMOR_MATERIAL, EquipmentSlot.HEAD,Item.Settings().group(ItemGroup.COMBAT))
    val AMETRINE_CHESTPLATE = ArmorItem(AMETRINE_ARMOR_MATERIAL, EquipmentSlot.CHEST,Item.Settings().group(ItemGroup.COMBAT))
    val AMETRINE_LEGGINGS = ArmorItem(AMETRINE_ARMOR_MATERIAL, EquipmentSlot.LEGS,Item.Settings().group(ItemGroup.COMBAT))
    val AMETRINE_BOOTS = ArmorItem(AMETRINE_ARMOR_MATERIAL, EquipmentSlot.FEET,Item.Settings().group(ItemGroup.COMBAT))

    fun registerAll() {
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","steel_helmet"), STEEL_HELMET)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","steel_chestplate"), STEEL_CHESTPLATE)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","steel_leggings"), STEEL_LEGGINGS)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","steel_boots"), STEEL_BOOTS)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","ametrine_helmet"), AMETRINE_HELMET)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","ametrine_chestplate"), AMETRINE_CHESTPLATE)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","ametrine_leggings"), AMETRINE_LEGGINGS)
        Registry.register(Registry.ITEM, Identifier("amethyst_imbuement","ametrine_boots"), AMETRINE_BOOTS)
    }
}