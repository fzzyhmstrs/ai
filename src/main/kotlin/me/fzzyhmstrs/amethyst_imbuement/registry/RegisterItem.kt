@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.*
import net.minecraft.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItem {

    private val regItem: MutableMap<String, Item> = mutableMapOf()

    //declaring the items to add to the game
    val GOLDEN_HEART = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"golden_heart")) .also{ regItem["golden_heart"] = it}
    val STEEL_INGOT = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["steel_ingot"] = it}
    val BERYL_COPPER_INGOT = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["beryl_copper_ingot"] = it}
    val CITRINE = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["citrine"] = it}
    val AMETRINE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"ametrine")).also{ regItem["ametrine"] = it} // item is custom for flavor text
    val MOONSTONE = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["moonstone"] = it}
    val OPAL = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["opal"] = it}
    val SMOKY_QUARTZ = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["smoky_quartz"] = it}
    val GARNET = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["garnet"] = it}
    val DANBURITE = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["danburite"] = it}
    val CRYSTALLINE_HEART = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"crystalline_heart")).withGlint().also{ regItem["crystalline_heart"] = it} //item is custom for flavor text
    val CELESTINE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.EPIC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"celestine")).withGlint().also{ regItem["celestine"] = it} // item is custom for flavor text. need texture
    val BRILLIANT_DIAMOND = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.EPIC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"brilliant_diamond")).withGlint().also{ regItem["brilliant_diamond"] = it} // item is custom for flavor text
    val IMBUED_LAPIS = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["imbued_lapis"] = it}
    val PYRITE = Item(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).also{ regItem["pyrite"] = it}
    val IMBUED_QUARTZ = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["imbued_quartz"] = it}
    val XP_BUSH_SEED = AliasedBlockItem(RegisterBlock.EXPERIENCE_BUSH,FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["xp_bush_seed"] = it}
    
    //scepter update gem and found items
    val GEM_OF_PROMISE = Item(FabricItemSettings().maxCount(1).group(ItemGroup.MISC)).also{ regItem["gem_of_promise"] = it}
    val GEM_DUST = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["gem_dust"] = it}
    val SPARKING_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["sparking_gem"] = it}
    val BLAZING_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["blazing_gem"] = it}
    val INQUISITIVE_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["inquisitive_gem"] = it}
    val LETHAL_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["lethal_gem"] = it}
    val HEALERS_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["healers_gem"] = it}
    val BRUTAL_GEM = Item(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["brutal_gem"] = it}
    val GLOWING_FRAGMENT = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_fragment").also{ regItem["glowing_fragment"] = it}
    val MALACHITE_FIGURINE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"malachite_figurine").also{ regItem["malachite_figurine"] = it}
    val RESONANT_ROD = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"resonant_rod").also{ regItem["resonant_rod"] = it}
    //val SURVEY_MAP = SurveyMapItem(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["survey_map"] = it}
    val CHARGED_MOONSTONE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"charged_moonstone").also{ regItem["charged_moonstone"] = it}
    val ENERGETIC_OPAL = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"energetic_opal").also{ regItem["energetic_opal"] = it}

    //tool and weapon item declarations
    val GLISTERING_TRIDENT = GlisteringTridentItem(Item.Settings().maxDamage(550).group(ItemGroup.COMBAT).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glistering_trident")).also{ regItem["glistering_trident"] = it}
    val SNIPER_BOW = SniperBowItem(Item.Settings().maxDamage(500).group(ItemGroup.COMBAT)).also{ regItem["sniper_bow"] = it}
    val GLOWING_BLADE = SwordItem(GlowingToolMaterial,3 ,-2.4f,FabricItemSettings().group(ItemGroup.COMBAT)).also{ regItem["garnet_sword"] = it}
    val GARNET_SWORD = SwordItem(GarnetToolMaterial,3 ,-2.4f,FabricItemSettings().group(ItemGroup.COMBAT)).also{ regItem["garnet_sword"] = it}
    val GARNET_SHOVEL = ShovelItem(GarnetToolMaterial,1.5f,-3.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_shovel"] = it}
    val GARNET_PICKAXE = CustomPickaxeItem(GarnetToolMaterial,1,-2.8f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_pickaxe"] = it}
    val GARNET_AXE = CustomAxeItem(GarnetToolMaterial,5.0f,-3.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_axe"] = it}
    val GARNET_HOE = CustomHoeItem(GarnetToolMaterial,-3,0.0f,FabricItemSettings().group(ItemGroup.TOOLS)).also{ regItem["garnet_hoe"] = it}
    val OPALINE_SCEPTER = ScepterItem(ScepterLvl1ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["opaline_scepter"] = it}
    val IRIDESCENT_SCEPTER = ScepterItem(ScepterLvl2ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["iridescent_scepter"] = it}
    val LUSTROUS_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE)).also{ regItem["lustrous_scepter"] = it}
    val DEBUG_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.DEBUG))
        .withModifiers(listOf(ModifierRegistry.MODIFIER_DEBUG,ModifierRegistry.MODIFIER_DEBUG_2,ModifierRegistry.MODIFIER_DEBUG_3))
        .also{ regItem["debug_scepter"] = it}
        
    // scepter upgrade scepters
    val FURIOUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.FURIOUS))
        .also{ regItem["furious_scepter"] = it}
    val WITTY_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withAugments(listOf(RegisterEnchantment.SHINE))
        .withModifiers(listOf(RegisterModifier.WITTY))
        .also{ regItem["witty_scepter"] = it}
    val GRACEFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withAugments(listOf(RegisterEnchantment.REGENERATE))
        .withModifiers(listOf(RegisterModifier.GRACEFUL))
        .also{ regItem["graceful_scepter"] = it}
    val BLAZING_SCEPTER = BlazingScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FIREBALL))
        .withModifiers(listOf(RegisterModifier.FIRE_ASPECT))
        .also{ regItem["blazing_scepter"] = it}
    val SPARKING_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.LIGHTNING_BOLT))
        .withModifiers(listOf(RegisterModifier.LIGHTNING_ASPECT))
        .also{ regItem["sparking_scepter"] = it}
    val FROSTED_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.ICE_SPIKES))
        .withModifiers(listOf(RegisterModifier.ICE_ASPECT,ModifierRegistry.LESSER_THRIFTY))
        .also{ regItem["frosted_scepter"] = it}
    val SCEPTER_OF_BLADES = CustomScepterItem(ScepterOfBladesToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SPECTRAL_SLASH))
        .withModifiers(listOf(RegisterModifier.BLADE_ASPECT, ModifierRegistry.LESSER_THRIFTY))
        .also{ regItem["scepter_of_blades"] = it}
    val SCEPTER_OF_RECALL = CustomScepterItem(ScepterOfBladesToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.RECALL))
        .withModifiers(listOf(RegisterModifier.GREATER_REACH))
        .also{ regItem["scepter_of_recall"] = it}
    val CLERICS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.MINOR_HEAL))
        .withModifiers(listOf(RegisterModifier.HEALING))
        .also{ regItem["clerics_scepter"] = it}
    val BARDIC_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.REGENERATE))
        .withModifiers(listOf(RegisterModifier.GREATER_ENDURING, ModifierRegistry.LESSER_ATTUNED))
        .also{ regItem["bardic_scepter"] = it}
    val SCEPTER_OF_SUMMONING = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_ZOMBIE))
        .withModifiers(listOf(RegisterModifier.SUMMONERS_ASPECT))
        .also{ regItem["scepter_of_summoning"] = it}
    val BUILDERS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.HARD_LIGHT_BRIDGE))
        .withModifiers(listOf(RegisterModifier.BUILDERS_ASPECT, RegisterModifier.LESSER_REACH))
        .also{ regItem["builders_scepter"] = it}
    val SCEPTER_OF_THE_VANGUARD = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BARRIER))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE ,RegisterModifier.SMITING))
        .also{ regItem["scepter_of_the_vanguard"] = it}
    val SCEPTER_OF_THE_PALADIN = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FORTIFY))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.LESSER_ENDURING))
        .also{ regItem["scepter_of_the_paladin"] = it}
    val SCEPTER_OF_THE_PACIFIST = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withAugments(listOf(RegisterEnchantment.BEDAZZLE))
        .withModifiers(listOf(RegisterModifier.HEALERS_GRACE, RegisterModifier.HEALERS_PACT))
        .also{ regItem["scepter_of_the_pacifist"] = it}
    val CORRUPTED_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.NECROTIC))
        .also{ regItem["corrupted_scepter"] = it}
    val DANGEROUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.DANGEROUS))
        .also{ regItem["dangerous_scepter"] = it}
    val SKILLFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.SKILLFUL))
        .also{ regItem["skillful_scepter"] = it}
    val ENDURING_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.ENDURING))
        .also{ regItem["enduring_scepter"] = it}
    val SCEPTER_OF_INSIGHT = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.INSIGHTFUL, RegisterModifier.LESSER_REACH))
        .also{ regItem["scepter_of_insight"] = it}
    val TRAVELERS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_BOAT, RegisterEnchantment.SUMMON_STRIDER))
        .withModifiers(listOf(RegisterModifier.TRAVELER))
        .also{ regItem["travelers_scepter"] = it}
    val LETHALITY = LethalityScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.FANGS))
        .withModifiers(listOf(RegisterModifier.DANGEROUS, ModifierRegistry.LESSER_THRIFTY))
        .also{ regItem["lethality"] = it}
    val RESONANCE = CustomScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.RESONATE))
        .withModifiers(listOf(RegisterModifier.ECHOING,RegisterModifier.SKILLFUL))
        .also{ regItem["resonance"] = it}
    val REDEMPTION = CustomScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.HEALERS_PACT,RegisterModifier.HEALERS_GRACE, ModifierRegistry.LESSER_ATTUNED))
        .also{ regItem["redemption"] = it}
    val EQUINOX = EquinoxScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.ELEMENTAL, ModifierRegistry.GREATER_THRIFTY))
        .also{ regItem["equinox"] = it}
    val SOJOURN = CustomScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.SURVEY))
        .withModifiers(listOf(RegisterModifier.WITTY,RegisterModifier.TRAVELER))
        .also{ regItem["sojourn"] = it}
    val AEGIS = CustomScepterItem(ScepterLvl3ToolMaterial,FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.LESSER_ENDURING, ModifierRegistry.LESSER_ATTUNED))
        .also{ regItem["aegis"] = it}


    //trinket and book declaration
    val COPPER_RING = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_ring")) .also{ regItem["copper_ring"] = it}
    val COPPER_HEADBAND = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_headband")).also{ regItem["copper_headband"] = it}
    val COPPER_AMULET = CopperJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_amulet")).also{ regItem["copper_amulet"] = it}
    val IMBUED_RING = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_ring")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_ring")) .also{ regItem["imbued_ring"] = it}
    val IMBUED_HEADBAND = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_headband")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_headband")).also{ regItem["imbued_headband"] = it}
    val IMBUED_AMULET = ImbuedJewelryItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_amulet")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_amulet")).also{ regItem["imbued_amulet"] = it}
    val TOTEM_OF_AMETHYST = TotemItem(FabricItemSettings().group(ItemGroup.COMBAT).maxDamage(360).rarity(Rarity.UNCOMMON)).also{ regItem["totem_of_amethyst"] = it}
    val IRIDESCENT_ORB = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"iridescent_orb")).also{ regItem["iridescent_orb"] = it}
    val LUSTROUS_SPHERE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"lustrous_sphere")).withGlint().also{ regItem["lustrous_sphere"] = it}
    val HEARTSTONE = CustomFlavorItem(FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"heartstone")).withGlint() .also{ regItem["heartstone"] = it}
    val BOOK_OF_LORE = BookOfLoreItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_lore")) .also{ regItem["book_of_lore"] = it}
    val BOOK_OF_MYTHOS = BookOfMythosItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_mythos")).withGlint() .also{ regItem["book_of_mythos"] = it}
    val GLISTERING_TOME = GlisteringTomeItem(FabricItemSettings().group(ItemGroup.MISC)).also{ regItem["glistering_tome"] = it}
    val MANA_POTION = ManaPotionItem(FabricItemSettings().group(ItemGroup.MISC).maxCount(16)).also{ regItem["mana_potion"] = it}

    fun registerAll() {

        for (k in regItem.keys){
            Registry.register(Registry.ITEM,Identifier(AI.MOD_ID,k), regItem[k])
        }
        regItem.clear()
    }
}
