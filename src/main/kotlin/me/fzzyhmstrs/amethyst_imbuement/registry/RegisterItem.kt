@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.*
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItem {

    var regItem: MutableMap<String, Item> = mutableMapOf()

    //declaring the items to add to the game
    val GOLDEN_HEART = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON),"golden_heart", false).also{ regItem["golden_heart"] = it}
    val STEEL_INGOT = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["steel_ingot"] = it}
    val BERYL_COPPER_INGOT = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["beryl_copper_ingot"] = it}
    val CITRINE = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["citrine"] = it}
    val AMETRINE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE),"ametrine", false).also{ regItem["ametrine"] = it} // item is custom for flavor text
    val MOONSTONE = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["moonstone"] = it}
    val OPAL = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["opal"] = it}
    val SMOKY_QUARTZ = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["smoky_quartz"] = it}
    val GARNET = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["garnet"] = it}
    val DANBURITE = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["danburite"] = it}
    val CRYSTALLINE_HEART = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE),"crystalline_heart", true).also{ regItem["crystalline_heart"] = it} //item is custom for flavor text
    val CELESTINE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.EPIC),"celestine", true).also{ regItem["celestine"] = it} // item is custom for flavor text. need texture
    val BRILLIANT_DIAMOND = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.EPIC),"brilliant_diamond", false).also{ regItem["brilliant_diamond"] = it} // item is custom for flavor text
    val IMBUED_LAPIS = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["imbued_lapis"] = it}
    val PYRITE = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["pyrite"] = it}
    val IMBUED_QUARTZ = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["imbued_quartz"] = it}
    val XP_BUSH_SEED = AliasedBlockItem(RegisterBlock.EXPERIENCE_BUSH,FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["xp_bush_seed"] = it}

    //tool and weapon item declarations
    val GLISTERING_TRIDENT = GlisteringTridentItem(Item.Settings().maxDamage(550).group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["glistering_trident"] = it}
    val SNIPER_BOW = SniperBowItem(Item.Settings().maxDamage(500).group(ItemGroup.COMBAT)).also{ regItem["sniper_bow"] = it}
    val GARNET_SWORD = SwordItem(GarnetToolMaterial,3 ,-2.4f,FabricItemSettings().group(ItemGroup.COMBAT)).also{ regItem["garnet_sword"] = it}
    val GARNET_SHOVEL = ShovelItem(GarnetToolMaterial,1.5f,-3.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_shovel"] = it}
    val GARNET_PICKAXE = CustomPickaxeItem(GarnetToolMaterial,1,-2.8f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_pickaxe"] = it}
    val GARNET_AXE = CustomAxeItem(GarnetToolMaterial,5.0f,-3.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_axe"] = it}
    val GARNET_HOE = CustomHoeItem(GarnetToolMaterial,-3,0.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_hoe"] = it}
    val OPALINE_SCEPTER = ScepterItem(ScepterLvl1ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["opaline_scepter"] = it}
    val IRIDESCENT_SCEPTER = ScepterItem(ScepterLvl2ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["iridescent_scepter"] = it}
    val LUSTROUS_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE),RegisterModifier.MODIFIER_DEBUG.modifierId).also{ regItem["lustrous_scepter"] = it}


    //trinket and book declaration
    val COPPER_RING = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"copper_ring").also{ regItem["copper_ring"] = it}
    val COPPER_HEADBAND = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"copper_headband").also{ regItem["copper_headband"] = it}
    val COPPER_AMULET = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"copper_amulet").also{ regItem["copper_amulet"] = it}
    val IMBUED_RING = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"imbued_ring").also{ regItem["imbued_ring"] = it}
    val IMBUED_HEADBAND = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"imbued_headband").also{ regItem["imbued_headband"] = it}
    val IMBUED_AMULET = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"imbued_amulet").also{ regItem["imbued_amulet"] = it}
    val TOTEM_OF_AMETHYST = TotemItem(FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(360).rarity(Rarity.UNCOMMON)).also{ regItem["totem_of_amethyst"] = it}
    val IRIDESCENT_ORB = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON),"iridescent_orb", false).also{ regItem["iridescent_orb"] = it}
    val LUSTROUS_SPHERE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE),"lustrous_sphere", true).also{ regItem["lustrous_sphere"] = it}
    val HEARTSTONE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON),"heartstone", true).also{ regItem["heartstone"] = it}
    val BOOK_OF_LORE = BookOfLoreItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"book_of_lore", false).also{ regItem["book_of_lore"] = it}
    val BOOK_OF_MYTHOS = BookOfMythosItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1),"book_of_mythos", true).also{ regItem["book_of_mythos"] = it}
    val GLISTERING_TOME = GlisteringTomeItem(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["glistering_tome"] = it}
    val MANA_POTION = ManaPotionItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(16)).also{ regItem["mana_potion"] = it}

    fun registerAll() {

        for (k in regItem.keys){
            Registry.register(Registry.ITEM,Identifier(AI.MOD_ID,k), regItem[k])
        }
        regItem.clear()
    }
}