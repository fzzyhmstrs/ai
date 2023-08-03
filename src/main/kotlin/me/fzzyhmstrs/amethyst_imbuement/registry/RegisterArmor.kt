@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
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
    
    val STEEL_HELMET = register(ArmorItem(AiArmorMaterials.STEEL, ArmorItem.Type.HELMET,Item.Settings()), "steel_helmet")
    val STEEL_CHESTPLATE = register(ArmorItem(AiArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,Item.Settings()), "steel_chestplate")
    val STEEL_LEGGINGS = register(ArmorItem(AiArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS,Item.Settings()), "steel_leggings")
    val STEEL_BOOTS = register(ArmorItem(AiArmorMaterials.STEEL, ArmorItem.Type.BOOTS,Item.Settings()), "steel_boots")
    val AMETRINE_HELMET = register(ArmorItem(AiArmorMaterials.AMETRINE, ArmorItem.Type.HELMET,Item.Settings()), "ametrine_helmet")
    val AMETRINE_CHESTPLATE = register(ArmorItem(AiArmorMaterials.AMETRINE, ArmorItem.Type.CHESTPLATE,Item.Settings()), "ametrine_chestplate")
    val AMETRINE_LEGGINGS = register(ArmorItem(AiArmorMaterials.AMETRINE, ArmorItem.Type.LEGGINGS,Item.Settings()), "ametrine_leggings")
    val AMETRINE_BOOTS = register(ArmorItem(AiArmorMaterials.AMETRINE, ArmorItem.Type.BOOTS,Item.Settings()),"ametrine_boots")

    fun registerAll() {}
}
