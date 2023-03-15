package me.fzzyhmstrs.amethyst_imbuement.config

import com.google.gson.GsonBuilder
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.gson
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.readOrCreate
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.readOrCreateUpdated
import me.fzzyhmstrs.fzzy_core.registry.SyncedConfigRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object AiConfig: SyncedConfigHelper.SyncedConfig {

    var items: Items
    var altars: Altars
    var colors: Colors
    var villages: Villages
    var enchantments: Enchantments
    var trinkets: Trinkets
    var entities: Entities

    init {
        items = readOrCreateUpdated("items_v1.json","items_v0.json", base = AI.MOD_ID, configClass = { Items() }, previousClass = {ItemsV0()})
        altars = readOrCreateUpdated("altars_v3.json","altars_v2.json", base = AI.MOD_ID, configClass = {Altars()}, previousClass = {AltarsV2()})
        colors = readOrCreateUpdated("colors_v1.json","colors_v0.json", base = AI.MOD_ID, configClass = {Colors()}, previousClass = {ColorsV0()})
        colors.trimData()
        //villages = readOrCreate("villages_v0.json", base = AI.MOD_ID) { Villages() }
        villages = readOrCreateUpdated("villages_v1.json","villages_v0.json", base = AI.MOD_ID, configClass = { Villages() }, previousClass = {VillagesV0()})
        enchantments = readOrCreate("enchantments_v0.json", base = AI.MOD_ID) { Enchantments() }
        trinkets = readOrCreate("trinkets_v0.json", base = AI.MOD_ID) { Trinkets() }
        entities = readOrCreate("entities_v0.json", base = AI.MOD_ID) { Entities() }
        ReadmeText.writeReadMe("README.txt", AI.MOD_ID)
    }

    override fun initConfig(){
        SyncedConfigRegistry.registerConfig(AI.MOD_ID,this)
    }

    override fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(items))
        buf.writeString(gson.toJson(altars))
        buf.writeString(gson.toJson(colors))
        buf.writeString(gson.toJson(villages))
        buf.writeString(gson.toJson(enchantments))
        buf.writeString(gson.toJson(trinkets))
        buf.writeString(gson.toJson(entities))
    }

    override fun readFromServer(buf:PacketByteBuf){
        items = gson.fromJson(buf.readString(),Items::class.java)
        altars = gson.fromJson(buf.readString(),Altars::class.java)
        colors = gson.fromJson(buf.readString(),Colors::class.java)
        villages = gson.fromJson(buf.readString(),Villages::class.java)
        enchantments = gson.fromJson(buf.readString(),Enchantments::class.java)
        trinkets = gson.fromJson(buf.readString(),Trinkets::class.java)
        entities = gson.fromJson(buf.readString(),Entities::class.java)
    }


    class Items {
        var giveGlisteringTome: Boolean = true
        var totemOfAmethystDurability: Int = 360
        var opalineDurability: Int = ScepterLvl1ToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterLvl1ToolMaterial.baseCooldown()
        var bladesDurability: Int = ScepterOfBladesToolMaterial.defaultDurability()
        var bladesDamage: Float = ScepterOfBladesToolMaterial.defaultAttackDamage()
        var lethalityDurability: Int = LethalityToolMaterial.defaultDurability()
        var lethalityDamage: Float = LethalityToolMaterial.defaultAttackDamage()
    }

    class Altars {
        var experienceBushBonemealChance: Float = 0.4f
        var experienceBushGrowChance: Float = 0.15f
        var disenchantLevelCosts: List<Int> = listOf(11, 17, 24, 33, 44)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
        var imbuingTableMatchEasyMagicBehavior: Boolean = true
        var imbuingTableEasyMagicRerollEnabled: Boolean = true
        var imbuingTableEasyMagicLevelCost: Int = 5
        var imbuingTableEasyMagicLapisCost: Int = 1
        var imbuingTableEasyMagicShowTooltip: Boolean = true
        var imbuingTableEasyMagicSingleEnchantTooltip: Boolean = true
        var imbuingTableEasyMagicLenientBookshelves: Boolean = false
        var imbuingTableMatchRerollBehavior: Boolean = true
        var imbuingTableRerollLevelCost: Int = 1
        var imbuingTableRerollLapisCost: Int = 0
        var imbuingTableMatchEnhancementBehavior: Boolean = true
        var imbuingTableEnhancementLevelsPer: Int = 5
        var imbuingTableEnhancementLapisPer: Int = 5
        var imbuingTableMatchEasierEnchantingBehavior: Boolean = true
        var imbuingTableEasierEnchantingLapisCost: Int = 6
        var imbuingTableMatchBetterEnchantmentBoostingBehavior: Boolean = true
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5
        var altarOfExperienceCustomXpMethod: Boolean = true
    }

    class Colors{
        var defaultColorMap: Map<String,String> = DefaultColorMap.defaultColorMap()
        var defaultRainbowList: List<String> = DefaultColorMap.defaultRainbowList()
        var modColorMap: Map<String,String> = mapOf()
        var modRainbowList: List<String> = listOf()
    }

    private fun Colors.trimData(){
        fun colorMapBuilder(rawMap: Map<String,String>): Map<String,String>{
            val actualMap: MutableMap<String,String> = mutableMapOf()
            for (entry in rawMap){
                val id = Identifier(entry.key).namespace
                if (FabricLoader.getInstance().isModLoaded(id)){
                    actualMap[entry.key] = entry.value
                }
            }
            return actualMap
        }
        fun rainbowListBuilder(rawList: List<String>): List<String>{
            val actualList: MutableList<String> = mutableListOf()
            for (entry in rawList){
                val id = Identifier(entry).namespace
                if (FabricLoader.getInstance().isModLoaded(id)){
                    actualList.add(entry)
                }
            }
            return actualList
        }
        this.defaultColorMap = colorMapBuilder(this.defaultColorMap)
        this.defaultRainbowList = rainbowListBuilder(this.defaultRainbowList)
        this.modColorMap = colorMapBuilder(this.modColorMap)
        this.modRainbowList = rainbowListBuilder(this.modRainbowList)
    }

    class Villages{
        var enableDesertWorkshops: Boolean = true
        var desertWorkshopWeight: Int = 2
        var enablePlainsWorkshops: Boolean = true
        var plainsWorkshopWeight: Int = 3
        var enableSavannaWorkshops: Boolean = true
        var savannaWorkshopWeight: Int = 3
        var enableSnowyWorkshops: Boolean = true
        var snowyWorkshopWeight: Int = 2
        var enableTaigaWorkshops: Boolean = true
        var taigaWorkshopWeight: Int = 3
        var enableCtovWorkshops: Boolean = true
        var ctovBeachWorkshopWeight: Int = 4
        var ctovDarkForestWorkshopWeight: Int = 4
        var ctovJungleWorkshopWeight: Int = 4
        var ctovJungleTreeWorkshopWeight: Int = 4
        var ctovMesaWorkshopWeight: Int = 4
        var ctovMesaFortifiedWorkshopWeight: Int = 4
        var ctovMountainWorkshopWeight: Int = 4
        var ctovMountainAlpineWorkshopWeight: Int = 4
        var ctovMushroomWorkshopWeight: Int = 4
        var ctovSwampWorkshopWeight: Int = 4
        var ctovSwampFortifiedWorkshopWeight: Int = 4
        var enableRsWorkshops: Boolean = true
        var rsBadlandsWorkshopWeight: Int = 2
        var rsBirchWorkshopWeight: Int = 2
        var rsDarkForestWorkshopWeight: Int = 2
        var rsGiantTaigaWorkshopWeight: Int = 1
        var rsJungleWorkshopWeight: Int = 2
        var rsMountainsWorkshopWeight: Int = 2
        var rsMushroomsWorkshopWeight: Int = 2
        var rsOakWorkshopWeight: Int = 2
        var rsSwampWorkshopWeight: Int = 2
        var rsCrimsonWorkshopWeight: Int = 2
        var rsWarpedWorkshopWeight: Int = 2
    }

    class Enchantments{
        var enabledEnchantments: Map<String,Boolean> = mapOf(
            "heroic" to true,
            "wasting" to true,
            "deadly_shot" to true,
            "puncturing" to true,
            "insight" to true,
            "lifesteal" to true,
            "decayed" to true,
            "contaminated" to true,
            "cleaving" to true,
            "bulwark" to true,
            "multi_jump" to true,
            "night_vision" to true,
            "steadfast" to true,
            "rain_of_thorns" to true,
            "vein_miner" to true
        )
    }

    class Trinkets{
        var enabledAugments: Map<String,Boolean> = mapOf(
            "angelic" to true,
            "crystalline" to true,
            "draconic_vision" to true,
            "escape" to true,
            "feline" to true,
            "friendly" to true,
            "guardian" to true,
            "hasting" to true,
            "headhunter" to true,
            "healthy" to true,
            "illuminating" to true,
            "immunity" to true,
            "invisibility" to true,
            "leaping" to true,
            "lightfooted" to true,
            "lucky" to true,
            "moonlit" to true,
            "resilience" to true,
            "shielding" to true,
            "slimy" to true,
            "soulbinding" to true,
            "soul_of_the_conduit" to true,
            "spectral_vision" to true,
            "spiked" to true,
            "strength" to true,
            "striding" to true,
            "suntouched" to true,
            "swiftness" to true,
            "undying" to true
        )
    }
    
    class Entities{
        
        fun isEntityPvpTeammate(user: LivingEntity, entity: Entity, spell: ScepterAugment): Boolean{
            if (forcePvpOnAllSpells || spell.getPvpMode()){
                return user.isTeammate(entity)
            }
            return true
        }
        
        var forcePvpOnAllSpells: Boolean = false
        var unhallowedBaseLifespan: Int = 2400
        var unhallowedBaseHealth: Double = 20.0
        var unhallowedBaseDamage: Float = 3.0f
        var familiarBaseHealth: Double = 20.0
        var familiarBaseDamage: Float = 5.0f
        var familiarBaseAttackSpeed: Float = 1.0f
        var crystalGolemSpellBaseLifespan: Int = 5500
        var crystalGolemSpellPerLvlLifespan: Int = 500
        var crystalGolemGuardianLifespan: Int = 900
        var crystalGolemBaseHealth: Double = 180.0
        var crystalGolemBaseDamage: Float = 20.0f
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class ItemsV0: SyncedConfigHelper.OldClass<Items> {
        var giveGlisteringTome: Boolean = true
        var opalineDurability: Int = ScepterLvl1ToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterLvl1ToolMaterial.baseCooldown()
        var bladesDurability: Int = ScepterOfBladesToolMaterial.defaultDurability()
        var bladesDamage: Float = ScepterOfBladesToolMaterial.defaultAttackDamage()
        var lethalityDurability: Int = LethalityToolMaterial.defaultDurability()
        var lethalityDamage: Float = LethalityToolMaterial.defaultAttackDamage()
        override fun generateNewClass(): Items {
            val items = Items()
            items.giveGlisteringTome = giveGlisteringTome
            items.baseRegenRateTicks = baseRegenRateTicks
            items.opalineDurability = opalineDurability
            items.iridescentDurability = iridescentDurability
            items.lustrousDurability = lustrousDurability
            items.bladesDurability = bladesDurability
            items.bladesDamage = bladesDamage
            items.lethalityDurability = lethalityDurability
            items.lethalityDamage = lethalityDamage
            return items
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class ColorsV0: SyncedConfigHelper.OldClass<Colors>{

        private var defaultColorMap: Map<String,String> = mapOf(
            "minecraft:diamond_ore" to "#1ED0D6",
            "minecraft:deepslate_diamond_ore" to "#1ED0D6",
            "minecraft:coal_ore" to "#2A2A2A",
            "minecraft:deepslate_coal_ore" to "#2A2A2A",
            "minecraft:copper_ore" to "#E0734D",
            "minecraft:deepslate_copper_ore" to "#E0734D",
            "minecraft:emerald_ore" to "#17DD62",
            "minecraft:deepslate_emerald_ore" to "#17DD62",
            "minecraft:gold_ore" to "#FCEE4E",
            "minecraft:deepslate_gold_ore" to "#FCEE4E",
            "minecraft:iron_ore" to "#D8AF93",
            "minecraft:deepslate_iron_ore" to "#D8AF93",
            "minecraft:lapis_ore" to "#1034BD",
            "minecraft:deepslate_lapis_ore" to "#1034BD",
            "minecraft:redstone_ore" to "#FF0000",
            "minecraft:deepslate_redstone_ore" to "#FF0000",
            "minecraft:nether_gold_ore" to "#FCEE4E",
            "minecraft:nether_quarts_ore" to "#FFFFFF",
            "minecraft:amethyst_cluster" to "#A678F1",
            "minecraft:budding_amethyst" to "#A678F1",
            "minecraft:small_amethyst_bud" to "#A678F1",
            "minecraft:medium_amethyst_bud" to "#A678F1",
            "minecraft:large_amethyst_bud" to "#A678F1"
        )

        var defaultRainbowList: List<String> = listOf(
            "minecraft:ancient_debris"
        )

        var modColorMap: Map<String,String> = mapOf()

        var modRainbowList: List<String> = listOf()

        override fun generateNewClass(): Colors {
            val colors = Colors()
            colors.defaultColorMap = colors.defaultColorMap + defaultColorMap
            colors.defaultRainbowList = colors.defaultRainbowList + defaultRainbowList
            colors.modColorMap = colors.modColorMap + modColorMap
            colors.modRainbowList = colors.modRainbowList + modRainbowList
            return colors
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class AltarsV2: SyncedConfigHelper.OldClass<Altars> {
        var disenchantLevelCosts: List<Int> = listOf(11, 17, 24, 33, 44)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
        var imbuingTableMatchEasyMagicBehavior: Boolean = true
        var imbuingTableEasyMagicRerollEnabled: Boolean = true
        var imbuingTableEasyMagicLevelCost: Int = 5
        var imbuingTableEasyMagicLapisCost: Int = 1
        var imbuingTableEasyMagicShowTooltip: Boolean = true
        var imbuingTableEasyMagicSingleEnchantTooltip: Boolean = true
        var imbuingTableEasyMagicLenientBookshelves: Boolean = false
        var imbuingTableMatchRerollBehavior: Boolean = true
        var imbuingTableRerollLevelCost: Int = 1
        var imbuingTableRerollLapisCost: Int = 0
        var imbuingTableMatchEnhancementBehavior: Boolean = true
        var imbuingTableEnhancementLevelsPer: Int = 5
        var imbuingTableEnhancementLapisPer: Int = 5
        var imbuingTableMatchEasierEnchantingBehavior: Boolean = true
        var imbuingTableEasierEnchantingLapisCost: Int = 6
        var imbuingTableMatchBetterEnchantmentBoostingBehavior: Boolean = true
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5
        var altarOfExperienceCustomXpMethod: Boolean = true

        override fun generateNewClass(): Altars {
            val altars = Altars()
            altars.disenchantLevelCosts = disenchantLevelCosts
            altars.disenchantBaseDisenchantsAllowed = disenchantBaseDisenchantsAllowed
            altars.imbuingTableEnchantingEnabled = imbuingTableEnchantingEnabled
            altars.imbuingTableReplaceEnchantingTable = imbuingTableReplaceEnchantingTable
            altars.imbuingTableDifficultyModifier = imbuingTableDifficultyModifier
            altars.imbuingTableMatchEasyMagicBehavior = imbuingTableMatchEasyMagicBehavior
            altars.imbuingTableEasyMagicRerollEnabled = imbuingTableEasyMagicRerollEnabled
            altars.imbuingTableEasyMagicLevelCost = imbuingTableEasyMagicLevelCost
            altars.imbuingTableEasyMagicLapisCost = imbuingTableEasyMagicLapisCost
            altars.imbuingTableEasyMagicShowTooltip = imbuingTableEasyMagicShowTooltip
            altars.imbuingTableEasyMagicLenientBookshelves = imbuingTableEasyMagicLenientBookshelves
            altars.imbuingTableMatchRerollBehavior = imbuingTableMatchRerollBehavior
            altars.imbuingTableRerollLevelCost = imbuingTableRerollLevelCost
            altars.imbuingTableRerollLapisCost = imbuingTableRerollLapisCost
            altars.imbuingTableMatchEasierEnchantingBehavior = imbuingTableMatchEasierEnchantingBehavior
            altars.imbuingTableEasierEnchantingLapisCost = imbuingTableEasierEnchantingLapisCost
            altars.altarOfExperienceBaseLevels = altarOfExperienceBaseLevels
            altars.altarOfExperienceCandleLevelsPer = altarOfExperienceCandleLevelsPer
            altars.altarOfExperienceCustomXpMethod = altarOfExperienceCustomXpMethod
            return altars
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class VillagesV0: SyncedConfigHelper.OldClass<Villages>{
        var enableDesertWorkshops: Boolean = true
        var desertWorkshopWeight: Int = 1
        var enablePlainsWorkshops: Boolean = true
        var plainsWorkshopWeight: Int = 2
        var enableSavannaWorkshops: Boolean = true
        var savannaWorkshopWeight: Int = 2
        var enableSnowyWorkshops: Boolean = true
        var snowyWorkshopWeight: Int = 1
        var enableTaigaWorkshops: Boolean = true
        var taigaWorkshopWeight: Int = 2
        override fun generateNewClass(): Villages {
            val villages = Villages()
            villages.enableDesertWorkshops = enableDesertWorkshops
            villages.desertWorkshopWeight = desertWorkshopWeight
            villages.enablePlainsWorkshops = enablePlainsWorkshops
            villages.plainsWorkshopWeight = plainsWorkshopWeight
            villages.enableSavannaWorkshops = enableSavannaWorkshops
            villages.savannaWorkshopWeight = savannaWorkshopWeight
            villages.enableSnowyWorkshops = enableSnowyWorkshops
            villages.snowyWorkshopWeight = snowyWorkshopWeight
            villages.enableTaigaWorkshops = enableTaigaWorkshops
            villages.taigaWorkshopWeight = taigaWorkshopWeight
            return villages
        }
    }
}
