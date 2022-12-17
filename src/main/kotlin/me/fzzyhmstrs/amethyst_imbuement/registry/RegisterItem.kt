@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings.*
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.item.*
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItem {

    private val regItem: MutableMap<String, Item> = mutableMapOf()
    val AI_GROUP: ItemGroup = FabricItemGroupBuilder.create(Identifier(AI.MOD_ID,"ai_group")).icon { ItemStack(RegisterBlock.IMBUING_TABLE.asItem()) }.build()

    //basic ingredients
    val CITRINE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["citrine"] = it}
    val SMOKY_QUARTZ = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["smoky_quartz"] = it}
    val IMBUED_QUARTZ = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["imbued_quartz"] = it}
    val IMBUED_LAPIS = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["imbued_lapis"] = it}
    val DANBURITE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["danburite"] = it}
    val MOONSTONE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["moonstone"] = it}
    val OPAL = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["opal"] = it}
    val GARNET = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["garnet"] = it}
    val PYRITE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["pyrite"] = it}
    val CHARGED_MOONSTONE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withGlint().withFlavorDefaultPath(Identifier(AI.MOD_ID,"charged_moonstone")).also{ regItem["charged_moonstone"] = it}
    val ENERGETIC_OPAL = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withGlint().withFlavorDefaultPath(Identifier(AI.MOD_ID,"energetic_opal")).also{ regItem["energetic_opal"] = it}
    val AMETRINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"ametrine")).also{ regItem["ametrine"] = it} // item is custom for flavor text
    val CELESTINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.EPIC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"celestine")).withGlint().also{ regItem["celestine"] = it} // item is custom for flavor text. need texture
    val STEEL_INGOT = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["steel_ingot"] = it}
    val BERYL_COPPER_INGOT = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["beryl_copper_ingot"] = it}


    //scepter update gem and found/crafted items
    val GEM_OF_PROMISE = GemOfPromiseItem(AiItemSettings().aiGroup(AiItemGroup.GEM).maxCount(1).group(AI_GROUP))
        .withFlavorDefaultPath(Identifier(AI.MOD_ID,"gem_of_promise"))
        .withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"gem_of_promise"))
        .also{ regItem["gem_of_promise"] = it}
    val GEM_DUST = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).also{ regItem["gem_dust"] = it}
    val SPARKING_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["sparking_gem"] = it}
    val BLAZING_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["blazing_gem"] = it}
    val INQUISITIVE_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["inquisitive_gem"] = it}
    val LETHAL_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["lethal_gem"] = it}
    val HEALERS_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["healers_gem"] = it}
    val BRUTAL_GEM = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["brutal_gem"] = it}
    val GLOWING_FRAGMENT = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_fragment")).also{ regItem["glowing_fragment"] = it}
    val BRILLIANT_DIAMOND = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.EPIC)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"brilliant_diamond")).withGlint().also{ regItem["brilliant_diamond"] = it} // item is custom for flavor text
    val MALACHITE_FIGURINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"malachite_figurine")).also{ regItem["malachite_figurine"] = it}
    val RESONANT_ROD = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"resonant_rod")).also{ regItem["resonant_rod"] = it}
    //val SURVEY_MAP = SurveyMapItem(FabricItemSettings().group(AI_GROUP)).also{ regItem["survey_map"] = it}
    val HEARTSTONE = CustomFlavorItem(FabricItemSettings().group(AI_GROUP).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"heartstone")).withGlint() .also{ regItem["heartstone"] = it}
    val IRIDESCENT_ORB = CustomFlavorItem(FabricItemSettings().group(AI_GROUP).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"iridescent_orb")).also{ regItem["iridescent_orb"] = it}
    val LUSTROUS_SPHERE = CustomFlavorItem(FabricItemSettings().group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"lustrous_sphere")).withGlint().also{ regItem["lustrous_sphere"] = it}
    val XP_BUSH_SEED = AliasedBlockItem(RegisterBlock.EXPERIENCE_BUSH,FabricItemSettings().group(AI_GROUP)).also{ regItem["xp_bush_seed"] = it}
    val GOLDEN_HEART = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.UNCOMMON)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"golden_heart")) .also{ regItem["golden_heart"] = it}
    val CRYSTALLINE_HEART = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"crystalline_heart")).withGlint().also{ regItem["crystalline_heart"] = it} //item is custom for flavor text

    //tool and weapon items
    val GLISTERING_TRIDENT = GlisteringTridentItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(550).group(AI_GROUP).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glistering_trident")).also{ regItem["glistering_trident"] = it}
    val SNIPER_BOW = SniperBowItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(500).group(AI_GROUP).rarity(Rarity.RARE)).also{ regItem["sniper_bow"] = it}
    val GARNET_SWORD = SwordItem(GarnetToolMaterial,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_sword"] = it}
    val GARNET_SHOVEL = ShovelItem(GarnetToolMaterial,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_shovel"] = it}
    val GARNET_PICKAXE = CustomPickaxeItem(GarnetToolMaterial,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_pickaxe"] = it}
    val GARNET_AXE = CustomAxeItem(GarnetToolMaterial,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_axe"] = it}
    val GARNET_HOE = CustomHoeItem(GarnetToolMaterial,-3,0.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_hoe"] = it}
    val GARNET_HORSE_ARMOR = HorseArmorItem(12,"garnet",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).also{ regItem["garnet_horse_armor"] = it}
    val GLOWING_BLADE = CustomSwordItem(GlowingToolMaterial,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_blade")) .also{ regItem["glowing_blade"] = it}
    val GLOWING_SPADE = CustomShovelItem(GlowingToolMaterial,1.5f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_spade")) .also{ regItem["glowing_spade"] = it}
    val GLOWING_PICK = CustomPickaxeItem(GlowingToolMaterial,1 ,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_pick")) .also{ regItem["glowing_pick"] = it}
    val GLOWING_AXE = CustomAxeItem(GlowingToolMaterial,5.0f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_axe")) .also{ regItem["glowing_axe"] = it}
    val GLOWING_HOE = CustomHoeItem(GlowingToolMaterial,-3 ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_hoe")) .also{ regItem["glowing_hoe"] = it}
    val GLOWING_HORSE_ARMOR = FlavorHorseArmorItem(14,"glowing",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"glowing_horse_armor")).also{ regItem["glowing_horse_armor"] = it}

    //trinket and books
    val COPPER_RING = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_ring")) .also{ regItem["copper_ring"] = it}
    val COPPER_HEADBAND = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_headband")).also{ regItem["copper_headband"] = it}
    val COPPER_AMULET = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"copper_amulet")).also{ regItem["copper_amulet"] = it}
    val IMBUED_RING = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_ring")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_ring")) .also{ regItem["imbued_ring"] = it}
    val IMBUED_HEADBAND = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_headband")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_headband")).also{ regItem["imbued_headband"] = it}
    val IMBUED_AMULET = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"imbued_amulet")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"imbued_amulet")).also{ regItem["imbued_amulet"] = it}
    val TOTEM_OF_AMETHYST = TotemItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).group(AI_GROUP).maxDamage(360).rarity(Rarity.UNCOMMON)).also{ regItem["totem_of_amethyst"] = it}
    val BOOK_OF_LORE = BookOfLoreItem(FabricItemSettings().group(AI_GROUP).maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_lore")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"book_of_lore")) .also{ regItem["book_of_lore"] = it}
    val BOOK_OF_MYTHOS = BookOfMythosItem(FabricItemSettings().group(AI_GROUP).maxCount(1).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_mythos")).withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"book_of_mythos")).withGlint() .also{ regItem["book_of_mythos"] = it}
    val MANA_POTION = ManaPotionItem(FabricItemSettings().group(AI_GROUP).maxCount(16)).also{ regItem["mana_potion"] = it}


    //Basic scepters
    val OPALINE_SCEPTER = ScepterItem(ScepterLvl1ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.COMMON)).also{ regItem["opaline_scepter"] = it}
    val IRIDESCENT_SCEPTER = ScepterItem(ScepterLvl2ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON)).also{ regItem["iridescent_scepter"] = it}
    val LUSTROUS_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE)).also{ regItem["lustrous_scepter"] = it}
    val DEBUG_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.DEBUG))
        .withModifiers(listOf(ModifierRegistry.MODIFIER_DEBUG,ModifierRegistry.MODIFIER_DEBUG_2,ModifierRegistry.MODIFIER_DEBUG_3))
        .also{ regItem["debug_scepter"] = it}
        
    // scepter upgrade scepters
    val FURIOUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.FURIOUS))
        .also{ regItem["furious_scepter"] = it}
    val WITTY_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.WITTY))
        .also{ regItem["witty_scepter"] = it}
    val GRACEFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.GRACEFUL))
        .also{ regItem["graceful_scepter"] = it}
    val DANGEROUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.DANGEROUS, RegisterModifier.DANGEROUS_PACT))
        .also{ regItem["dangerous_scepter"] = it}
    val SKILLFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.SKILLFUL))
        .also{ regItem["skillful_scepter"] = it}
    val ENDURING_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.LESSER_ENDURING))
        .also{ regItem["enduring_scepter"] = it}
    val BLAZING_SCEPTER = ParticleScepterItem(ParticleTypes.SMOKE,10,ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FIREBALL))
        .withModifiers(listOf(RegisterModifier.FIRE_ASPECT))
        .also{ regItem["blazing_scepter"] = it}
    val SPARKING_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.LIGHTNING_BOLT))
        .withModifiers(listOf(RegisterModifier.LIGHTNING_ASPECT))
        .also{ regItem["sparking_scepter"] = it}
    val FROSTED_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.ICE_SPIKES))
        .withModifiers(listOf(RegisterModifier.ICE_ASPECT))
        .also{ regItem["frosted_scepter"] = it}
    val SCEPTER_OF_BLADES = CustomScepterItem(ScepterOfBladesToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SPECTRAL_SLASH))
        .withModifiers(listOf(RegisterModifier.BLADE_ASPECT))
        .also{ regItem["scepter_of_blades"] = it}
    val CORRUPTED_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.NECROTIC))
        .also{ regItem["corrupted_scepter"] = it}
    val SCEPTER_OF_INSIGHT = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.INSIGHTFUL, RegisterModifier.LESSER_REACH))
        .also{ regItem["scepter_of_insight"] = it}
    val SCEPTER_OF_SUMMONING = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_ZOMBIE))
        .withModifiers(listOf(RegisterModifier.SUMMONERS_ASPECT))
        .also{ regItem["scepter_of_summoning"] = it}
    val TRAVELERS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_BOAT, RegisterEnchantment.SUMMON_STRIDER))
        .withModifiers(listOf(RegisterModifier.TRAVELER))
        .also{ regItem["travelers_scepter"] = it}
    val SCEPTER_OF_RECALL = ParticleScepterItem(ParticleTypes.PORTAL,10,ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.RECALL))
        .withModifiers(listOf(RegisterModifier.GREATER_REACH))
        .also{ regItem["scepter_of_recall"] = it}
    val BUILDERS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.HARD_LIGHT_BRIDGE))
        .withModifiers(listOf(RegisterModifier.BUILDERS_ASPECT, RegisterModifier.LESSER_REACH))
        .also{ regItem["builders_scepter"] = it}
    val SCEPTER_OF_THE_VANGUARD = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BARRIER))
        .withModifiers(listOf(RegisterModifier.SMITING, RegisterModifier.GRACEFUL))
        .also{ regItem["scepter_of_the_vanguard"] = it}
    val SCEPTER_OF_THE_PALADIN = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FORTIFY))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.LESSER_ENDURING))
        .also{ regItem["scepter_of_the_paladin"] = it}
    val SCEPTER_OF_THE_PACIFIST = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BEDAZZLE))
        .withModifiers(listOf(RegisterModifier.HEALERS_GRACE, RegisterModifier.HEALERS_PACT))
        .also{ regItem["scepter_of_the_pacifist"] = it}
    val CLERICS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.MINOR_HEAL))
        .withModifiers(listOf(RegisterModifier.HEALING))
        .also{ regItem["clerics_scepter"] = it}
    val BARDIC_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.INSPIRING_SONG))
        .withModifiers(listOf(RegisterModifier.LESSER_ENDURING, ModifierRegistry.ATTUNED))
        .also{ regItem["bardic_scepter"] = it}
    val EQUINOX = EquinoxScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.ELEMENTAL, RegisterModifier.FURIOUS))
        .also{ regItem["equinox"] = it}
    val LETHALITY = LethalityScepterItem(LethalityToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.DANGEROUS,RegisterModifier.DANGEROUS_PACT))
        .also{ regItem["lethality"] = it}
    val RESONANCE = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.RESONATE))
        .withModifiers(listOf(RegisterModifier.ECHOING,RegisterModifier.SKILLFUL))
        .also{ regItem["resonance"] = it}
    val SOJOURN = SojournScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.SURVEY))
        .withModifiers(listOf(RegisterModifier.WITTY,RegisterModifier.TRAVELER))
        .also{ regItem["sojourn"] = it}
    val AEGIS = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.SMITING))
        .also{ regItem["aegis"] = it}
    val REDEMPTION = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).group(AI_GROUP).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.HEALERS_PACT,RegisterModifier.HEALERS_GRACE, RegisterModifier.LESSER_ENDURING))
        .also{ regItem["redemption"] = it}

    //////////////////////////////

    val GIVE_IF_CONFIG = TickCriterion(Identifier(AI.MOD_ID,"give_if_config"))


    fun registerAll() {
        Criteria.register(GIVE_IF_CONFIG)

        ServerPlayConnectionEvents.JOIN.register {handler, _, _ ->
            if (AiConfig.items.giveGlisteringTome){
                GIVE_IF_CONFIG.trigger(handler.player)
            }
        }

        for (k in regItem.keys){
            Registry.register(Registry.ITEM,Identifier(AI.MOD_ID,k), regItem[k])
        }
    }
}
