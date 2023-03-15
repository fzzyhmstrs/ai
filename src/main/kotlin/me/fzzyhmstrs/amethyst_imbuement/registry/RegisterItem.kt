@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings.AiItemGroup
import me.fzzyhmstrs.amethyst_imbuement.item.custom.*
import me.fzzyhmstrs.amethyst_imbuement.item.promise.*
import me.fzzyhmstrs.amethyst_imbuement.item.scepter.*
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.*
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItem {

    private val regItem: MutableMap<String, Item> = mutableMapOf()


    //basic ingredients
    val CITRINE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["citrine"] = it}
    val SMOKY_QUARTZ = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["smoky_quartz"] = it}
    val IMBUED_QUARTZ = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["imbued_quartz"] = it}
    val IMBUED_LAPIS = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["imbued_lapis"] = it}
    val DANBURITE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["danburite"] = it}
    val MOONSTONE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["moonstone"] = it}
    val OPAL = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["opal"] = it}
    val GARNET = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["garnet"] = it}
    val PYRITE = Item(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["pyrite"] = it}
    val CHARGED_MOONSTONE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).withGlint().also{ regItem["charged_moonstone"] = it}
    val ENERGETIC_OPAL = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).withGlint().also{ regItem["energetic_opal"] = it}
    val AMETRINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).also{ regItem["ametrine"] = it} // item is custom for flavor text
    val CELESTINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.EPIC)).withGlint().also{ regItem["celestine"] = it} // item is custom for flavor text. need texture
    val STEEL_INGOT = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["steel_ingot"] = it}
    val BERYL_COPPER_INGOT = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["beryl_copper_ingot"] = it}


    //scepter update gem and found/crafted items
    val GEM_OF_PROMISE = GemOfPromiseItem(AiItemSettings().aiGroup(AiItemGroup.GEM).maxCount(1))
        .withFlavorDefaultPath(Identifier(AI.MOD_ID,"gem_of_promise"))
        .withFlavorDescDefaultPath(Identifier(AI.MOD_ID,"gem_of_promise"))
        .also{ regItem["gem_of_promise"] = it}
    val GEM_DUST = Item(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["gem_dust"] = it}
    val SPARKING_GEM = SparkingGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["sparking_gem"] = it}
    val BLAZING_GEM = BlazingGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["blazing_gem"] = it}
    val INQUISITIVE_GEM = InquisitiveGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["inquisitive_gem"] = it}
    val LETHAL_GEM = LethalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["lethal_gem"] = it}
    val HEALERS_GEM = HealersGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["healers_gem"] = it}
    val BRUTAL_GEM = BrutalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["brutal_gem"] = it}
    val MYSTICAL_GEM = MysticalGemItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)).also{ regItem["mystical_gem"] = it}
    val GLOWING_FRAGMENT = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).also{ regItem["glowing_fragment"] = it}
    val BRILLIANT_DIAMOND = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.EPIC)).withGlint().also{ regItem["brilliant_diamond"] = it} // item is custom for flavor text
    val MALACHITE_FIGURINE = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).also{ regItem["malachite_figurine"] = it}
    val RESONANT_ROD = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM)).also{ regItem["resonant_rod"] = it}
    //val SURVEY_MAP = SurveyMapItem(FabricItemSettings()).also{ regItem["survey_map"] = it}
    val HEARTSTONE = CustomFlavorItem(FabricItemSettings().rarity(Rarity.UNCOMMON)).withGlint() .also{ regItem["heartstone"] = it}
    val IRIDESCENT_ORB = CustomFlavorItem(FabricItemSettings().rarity(Rarity.UNCOMMON)).also{ regItem["iridescent_orb"] = it}
    val LUSTROUS_SPHERE = CustomFlavorItem(FabricItemSettings().rarity(Rarity.RARE)).withGlint().also{ regItem["lustrous_sphere"] = it}
    val XP_BUSH_SEED = AliasedBlockItem(RegisterBlock.EXPERIENCE_BUSH,FabricItemSettings()).also{ regItem["xp_bush_seed"] = it}
    val GOLDEN_HEART = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.UNCOMMON)) .also{ regItem["golden_heart"] = it}
    val CRYSTALLINE_HEART = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.GEM).rarity(Rarity.RARE)).withGlint().also{ regItem["crystalline_heart"] = it} //item is custom for flavor text

    //tool and weapon items
    val GLISTERING_TRIDENT = GlisteringTridentItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(550).rarity(Rarity.RARE)).also{ regItem["glistering_trident"] = it}
    val SNIPER_BOW = SniperBowItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(500).rarity(Rarity.RARE)).also{ regItem["sniper_bow"] = it}
    val GARNET_SWORD = SwordItem(GarnetToolMaterial,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_sword"] = it}
    val GARNET_SHOVEL = ShovelItem(GarnetToolMaterial,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_shovel"] = it}
    val GARNET_PICKAXE = CustomPickaxeItem(GarnetToolMaterial,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_pickaxe"] = it}
    val GARNET_AXE = CustomAxeItem(GarnetToolMaterial,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_axe"] = it}
    val GARNET_HOE = CustomHoeItem(GarnetToolMaterial,-3,0.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_hoe"] = it}
    val GARNET_HORSE_ARMOR = HorseArmorItem(12,"garnet",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["garnet_horse_armor"] = it}
    val GLOWING_BLADE = CustomSwordItem(GlowingToolMaterial,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)) .also{ regItem["glowing_blade"] = it}
    val GLOWING_SPADE = CustomShovelItem(GlowingToolMaterial,1.5f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)) .also{ regItem["glowing_spade"] = it}
    val GLOWING_PICK = CustomPickaxeItem(GlowingToolMaterial,1 ,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)) .also{ regItem["glowing_pick"] = it}
    val GLOWING_AXE = CustomAxeItem(GlowingToolMaterial,5.0f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)) .also{ regItem["glowing_axe"] = it}
    val GLOWING_HOE = CustomHoeItem(GlowingToolMaterial,-3 ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)) .also{ regItem["glowing_hoe"] = it}
    val GLOWING_HORSE_ARMOR = FlavorHorseArmorItem(14,"glowing",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["glowing_horse_armor"] = it}

    //trinket and books
    val COPPER_RING = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)) .also{ regItem["copper_ring"] = it}
    val COPPER_HEADBAND = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)) .also{ regItem["copper_headband"] = it}
    val COPPER_AMULET = CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)) .also{ regItem["copper_amulet"] = it}
    val IMBUED_RING = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)) .also{ regItem["imbued_ring"] = it}
    val IMBUED_HEADBAND = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)).also{ regItem["imbued_headband"] = it}
    val IMBUED_AMULET = ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)).also{ regItem["imbued_amulet"] = it}
    val TOTEM_OF_AMETHYST = TotemItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.totemOfAmethystDurability).rarity(Rarity.UNCOMMON)).also{ regItem["totem_of_amethyst"] = it}
    val SPELLCASTERS_FOCUS = SpellcastersFocusItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).rarity(Rarity.UNCOMMON)).also{ regItem["spellcasters_focus"] = it}
    val EMPTY_SPELL_SCROLL = CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["empty_spell_scroll"] = it}
    val SPELL_SCROLL = SpellScrollItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)).also{ regItem["spell_scroll"] = it}
    val BOOK_OF_LORE = BookOfLoreItem(FabricItemSettings().maxCount(1)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_lore")) .also{ regItem["book_of_lore"] = it}
    val BOOK_OF_MYTHOS = BookOfMythosItem(FabricItemSettings().maxCount(1).rarity(Rarity.RARE)).withFlavorDefaultPath(Identifier(AI.MOD_ID,"book_of_mythos")).withGlint() .also{ regItem["book_of_mythos"] = it}
    val MANA_POTION = ManaPotionItem(FabricItemSettings().maxCount(16)).also{ regItem["mana_potion"] = it}


    //Basic scepters
    val OPALINE_SCEPTER = ScepterItem(ScepterLvl1ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.COMMON)).also{ regItem["opaline_scepter"] = it}
    val IRIDESCENT_SCEPTER = ScepterItem(ScepterLvl2ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON)).also{ regItem["iridescent_scepter"] = it}
    val LUSTROUS_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE)).also{ regItem["lustrous_scepter"] = it}
    val DEBUG_SCEPTER = ScepterItem(ScepterLvl3ToolMaterial, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.DEBUG))
        .withModifiers(listOf(ModifierRegistry.MODIFIER_DEBUG,ModifierRegistry.MODIFIER_DEBUG_2,ModifierRegistry.MODIFIER_DEBUG_3))
        .also{ regItem["debug_scepter"] = it}
        
    // scepter upgrade scepters
    val FURIOUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.FURIOUS))
        .also{ regItem["furious_scepter"] = it}
    val WITTY_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.WITTY))
        .also{ regItem["witty_scepter"] = it}
    val GRACEFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.GRACEFUL))
        .also{ regItem["graceful_scepter"] = it}
    val DANGEROUS_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.DANGEROUS, RegisterModifier.DANGEROUS_PACT))
        .also{ regItem["dangerous_scepter"] = it}
    val SKILLFUL_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.SKILLFUL))
        .also{ regItem["skillful_scepter"] = it}
    val ENDURING_SCEPTER = CustomScepterItem(ScepterLvl1ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(ModifierRegistry.LESSER_ENDURING))
        .also{ regItem["enduring_scepter"] = it}
    val BLAZING_SCEPTER = ParticleScepterItem(ParticleTypes.SMOKE,10,ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FIREBALL))
        .withModifiers(listOf(RegisterModifier.FIRE_ASPECT))
        .also{ regItem["blazing_scepter"] = it}
    val SPARKING_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.LIGHTNING_BOLT))
        .withModifiers(listOf(RegisterModifier.LIGHTNING_ASPECT))
        .also{ regItem["sparking_scepter"] = it}
    val FROSTED_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.ICE_SPIKES))
        .withModifiers(listOf(RegisterModifier.ICE_ASPECT))
        .also{ regItem["frosted_scepter"] = it}
    val SCEPTER_OF_BLADES = CustomScepterItem(ScepterOfBladesToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SPECTRAL_SLASH))
        .withModifiers(listOf(RegisterModifier.BLADE_ASPECT))
        .also{ regItem["scepter_of_blades"] = it}
    val CORRUPTED_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.NECROTIC))
        .also{ regItem["corrupted_scepter"] = it}
    val SCEPTER_OF_INSIGHT = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.INSIGHTFUL, ModifierRegistry.LESSER_REACH))
        .also{ regItem["scepter_of_insight"] = it}
    val SCEPTER_OF_SUMMONING = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_ZOMBIE))
        .withModifiers(listOf(RegisterModifier.SUMMONERS_ASPECT))
        .also{ regItem["scepter_of_summoning"] = it}
    val TRAVELERS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_BOAT, RegisterEnchantment.SUMMON_STRIDER))
        .withModifiers(listOf(RegisterModifier.TRAVELER))
        .also{ regItem["travelers_scepter"] = it}
    val SCEPTER_OF_RECALL = ParticleScepterItem(ParticleTypes.PORTAL,10,ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.RECALL))
        .withModifiers(listOf(ModifierRegistry.GREATER_REACH))
        .also{ regItem["scepter_of_recall"] = it}
    val BUILDERS_SCEPTER = BuilderScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.HARD_LIGHT_BRIDGE))
        .withModifiers(listOf(RegisterModifier.BUILDERS_ASPECT, ModifierRegistry.LESSER_REACH))
        .also{ regItem["builders_scepter"] = it}
    val SCEPTER_OF_THE_VANGUARD = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BARRIER))
        .withModifiers(listOf(RegisterModifier.SMITING, RegisterModifier.GRACEFUL))
        .also{ regItem["scepter_of_the_vanguard"] = it}
    val SCEPTER_OF_THE_PALADIN = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FORTIFY))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, ModifierRegistry.LESSER_ENDURING))
        .also{ regItem["scepter_of_the_paladin"] = it}
    val SCEPTER_OF_THE_PACIFIST = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BEDAZZLE))
        .withModifiers(listOf(RegisterModifier.HEALERS_GRACE, RegisterModifier.HEALERS_PACT))
        .also{ regItem["scepter_of_the_pacifist"] = it}
    val CLERICS_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.MINOR_HEAL))
        .withModifiers(listOf(RegisterModifier.HEALING))
        .also{ regItem["clerics_scepter"] = it}
    val BARDIC_SCEPTER = CustomScepterItem(ScepterLvl2ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.INSPIRING_SONG))
        .withModifiers(listOf(ModifierRegistry.LESSER_ENDURING, ModifierRegistry.ATTUNED))
        .also{ regItem["bardic_scepter"] = it}
    val EQUINOX = EquinoxScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.ELEMENTAL, RegisterModifier.FURIOUS))
        .also{ regItem["equinox"] = it}
    val LETHALITY = LethalityScepterItem(LethalityToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.DANGEROUS,RegisterModifier.DANGEROUS_PACT))
        .also{ regItem["lethality"] = it}
    val RESONANCE = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.RESONATE))
        .withModifiers(listOf(RegisterModifier.ECHOING,RegisterModifier.SKILLFUL))
        .also{ regItem["resonance"] = it}
    val SOJOURN = SojournScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.SURVEY))
        .withModifiers(listOf(RegisterModifier.WITTY,RegisterModifier.TRAVELER))
        .also{ regItem["sojourn"] = it}
    val AEGIS = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.SMITING))
        .also{ regItem["aegis"] = it}
    val REDEMPTION = CustomScepterItem(ScepterLvl3ToolMaterial,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.HEALERS_PACT,RegisterModifier.HEALERS_GRACE, ModifierRegistry.LESSER_ENDURING))
        .also{ regItem["redemption"] = it}

    //////////////////////////////

    val AI_GROUP: ItemGroup by lazy{
        registerItemGroup()
    }

    fun registerItemGroup(): ItemGroup{
        return FabricItemGroup.builder(Identifier(AI.MOD_ID,"ai_group"))
            .displayName(Text.translatable("itemGroup.amethyst_imbuement.ai_group"))
            .icon { ItemStack(RegisterBlock.IMBUING_TABLE.asItem()) }
            .entries { _, entries, _ ->
                entries.addAll(regItem.values.stream().map { item -> ItemStack(item) }.toList())
                entries.addAll(RegisterArmor.regArmor.stream().map { item -> ItemStack(item) }.toList())
                entries.addAll(RegisterBlock.regBlock.values.stream()
                    .filter { block -> block !== RegisterBlock.EXPERIENCE_BUSH }
                    .map { block -> ItemStack(block.asItem()) }
                    .toList())

            }.build()
    }

    fun registerAll() {

        for (k in regItem.keys){
            val item = regItem[k]
            if (item is IgnitedGemItem){
                GemOfPromiseItem.register(item)
            }
            Registry.register(Registries.ITEM,Identifier(AI.MOD_ID,k), item)
        }
        @Suppress("UNUSED_VARIABLE") val group = AI_GROUP
    }
}
