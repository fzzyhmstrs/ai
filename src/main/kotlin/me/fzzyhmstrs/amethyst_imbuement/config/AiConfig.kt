package me.fzzyhmstrs.amethyst_imbuement.config

import com.google.gson.GsonBuilder
import me.fzzyhmstrs.amethyst_core.coding_util.SyncedConfigHelper
import me.fzzyhmstrs.amethyst_core.coding_util.SyncedConfigHelper.gson
import me.fzzyhmstrs.amethyst_core.coding_util.SyncedConfigHelper.readOrCreate
import me.fzzyhmstrs.amethyst_core.coding_util.SyncedConfigHelper.readOrCreateUpdated
import me.fzzyhmstrs.amethyst_core.registry.SyncedConfigRegistry
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object AiConfig: SyncedConfigHelper.SyncedConfig {

    var scepters: Scepters
    var altars: Altars
    var colors: Colors
    var villages: Villages
    var enchantments: Enchantments

    init {
        scepters = readOrCreateUpdated("scepters_v1.json","scepters_v0.json", base = AI.MOD_ID, configClass = { Scepters() }, previousClass = {SceptersV0()})
        altars = readOrCreateUpdated("altars_v2.json","altars_v1.json", base = AI.MOD_ID, configClass = {Altars()}, previousClass = {AltarsV1()})
        colors = readOrCreateUpdated("colors_v1.json","colors_v0.json", base = AI.MOD_ID, configClass = {Colors()}, previousClass = {ColorsV0()})
        colors.trimData()
        //villages = readOrCreate("villages_v0.json", base = AI.MOD_ID) { Villages() }
        villages = readOrCreateUpdated("villages_v1.json","villages_v0.json", base = AI.MOD_ID, configClass = { Villages() }, previousClass = {VillagesV0()})
        enchantments = readOrCreate("enchantments_v0.json", base = AI.MOD_ID) { Enchantments() }
        ReadmeText.writeReadMe("README.txt", AI.MOD_ID)
    }

    override fun initConfig(){
        SyncedConfigRegistry.registerConfig(AI.MOD_ID,this)
    }

    override fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(scepters))
        buf.writeString(gson.toJson(altars))
        buf.writeString(gson.toJson(colors))
    }

    override fun readFromServer(buf:PacketByteBuf){
        scepters = gson.fromJson(buf.readString(),Scepters::class.java)
        altars = gson.fromJson(buf.readString(),Altars::class.java)
        colors = gson.fromJson(buf.readString(),Colors::class.java)
    }


    class Scepters {
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
        var disenchantLevelCosts: List<Int> = listOf(3, 5, 9, 15, 23)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
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
        var desertWorkshopWeight: Int = 1
        var enablePlainsWorkshops: Boolean = true
        var plainsWorkshopWeight: Int = 2
        var enableSavannaWorkshops: Boolean = true
        var savannaWorkshopWeight: Int = 2
        var enableSnowyWorkshops: Boolean = true
        var snowyWorkshopWeight: Int = 1
        var enableTaigaWorkshops: Boolean = true
        var taigaWorkshopWeight: Int = 2
        var enableCtovWorkshops: Boolean = true
        var ctovBeachWorkshopWeight: Int = 5
        var ctovDarkForestWorkshopWeight: Int = 5
        var ctovJungleWorkshopWeight: Int = 5
        var ctovJungleTreeWorkshopWeight: Int = 5
        var ctovMesaWorkshopWeight: Int = 5
        var ctovMesaFortifiedWorkshopWeight: Int = 5
        var ctovMountainWorkshopWeight: Int = 5
        var ctovMountainAlpineWorkshopWeight: Int = 5
        var ctovMushroomWorkshopWeight: Int = 5
        var ctovSwampWorkshopWeight: Int = 5
        var ctovSwampFortifiedWorkshopWeight: Int = 5
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

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class SceptersV0: SyncedConfigHelper.OldClass {
        var opalineDurability: Int = ScepterLvl1ToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterLvl1ToolMaterial.baseCooldown()
        override fun generateNewClass(): Any {
            val scepters = Scepters()
            scepters.baseRegenRateTicks = baseRegenRateTicks
            scepters.opalineDurability = opalineDurability
            scepters.iridescentDurability = iridescentDurability
            scepters.lustrousDurability = lustrousDurability
            return scepters
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class ColorsV0: SyncedConfigHelper.OldClass{

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

        override fun generateNewClass(): Any {
            val colors = Colors()
            colors.defaultColorMap = colors.defaultColorMap + defaultColorMap
            colors.defaultRainbowList = colors.defaultRainbowList + defaultRainbowList
            colors.modColorMap = colors.modColorMap + modColorMap
            colors.modRainbowList = colors.modRainbowList + modRainbowList
            return colors
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class AltarsV1: SyncedConfigHelper.OldClass {
        var disenchantLevelCosts: List<Int> = listOf(3, 5, 9, 15, 23)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5

        override fun generateNewClass(): Any {
            val altars = Altars()
            altars.disenchantLevelCosts = disenchantLevelCosts
            altars.disenchantBaseDisenchantsAllowed = disenchantBaseDisenchantsAllowed
            altars.imbuingTableEnchantingEnabled = imbuingTableEnchantingEnabled
            altars.imbuingTableReplaceEnchantingTable = imbuingTableReplaceEnchantingTable
            altars.imbuingTableDifficultyModifier = imbuingTableDifficultyModifier
            altars.altarOfExperienceBaseLevels = altarOfExperienceBaseLevels
            altars.altarOfExperienceCandleLevelsPer = altarOfExperienceCandleLevelsPer
            return altars
        }
    }

    @Deprecated("Removing after assumed adoption of newer versions. Target end of 2022")
    class VillagesV0: SyncedConfigHelper.OldClass{
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
        override fun generateNewClass(): Any {
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