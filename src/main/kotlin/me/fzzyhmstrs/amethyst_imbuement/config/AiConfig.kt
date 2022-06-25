package me.fzzyhmstrs.amethyst_imbuement.config

import com.google.gson.GsonBuilder
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl2ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl3ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl1ToolMaterial
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.io.File

object AiConfig {

    val gson = GsonBuilder().setPrettyPrinting().create()
    const val augmentVersion = "_v0"

    var scepters: Scepters
    var altars: Altars
    var colors: Colors
    var villages: Villages
    var enchantments: Enchantments

    init {
        scepters = readOrCreate("scepters_v0.json") { Scepters() }
        altars = readOrCreateUpdated("altars_v1.json","altars_v0.json", configClass = {Altars()}, previousClass = {AltarsV0()})
        colors = readOrCreateUpdated("colors_v1.json","colors_v0.json", configClass = {Colors()}, previousClass = {ColorsV0()})
        colors.trimData()
        villages = readOrCreate("villages_v0.json") { Villages() }
        enchantments = readOrCreate("enchantments_v0.json") { Enchantments() }
        ReadmeText.writeReadMe("README.txt")
    }

    fun initConfig(){}

    @Deprecated("moving to amethyst_core")
    inline fun <reified T> readOrCreate(file: String, child: String = "", configClass: () -> T): T {
        val (dir,dirCreated) = makeDir(child)
        if (!dirCreated) {
            return configClass()
        }
        val f = File(dir, file)
        try {
            if (f.exists()) {
                return gson.fromJson(f.readLines().joinToString(""), T::class.java)
            } else if (!f.createNewFile()) {
                println("Failed to create default config file ($file), using default config.")
            } else {
                f.writeText(gson.toJson(configClass()))
            }
            return configClass()
        } catch (e: Exception) {
            println("Failed to read config file! Using default values: " + e.message)
            return configClass()
        }
    }

    @Deprecated("moving to amethyst_core")
    @Suppress("UNUSED_PARAMETER")
    inline fun <reified T, reified P> readOrCreateUpdated(file: String, previous: String, child: String = "", configClass: () -> T, previousClass: () -> P): T{
        val (dir,dirCreated) = makeDir(child)
        if (!dirCreated) {
            return configClass()
        }
        val p = File(dir, previous)
        try {
            if (p.exists()) {
                val previousConfig = gson.fromJson(p.readLines().joinToString(""), P::class.java)
                if (previousConfig is OldClass){
                    val newClass = previousConfig.generateNewClass()
                    if (newClass !is T){
                        throw RuntimeException("Old config class is not returning the proper new config class: ${P::class.simpleName} is returning ${newClass.javaClass.simpleName}; expected ${T::class.simpleName}")
                    } else {
                        val f = File(dir,file)
                        if (f.exists()){
                            p.delete() //attempts to delete the now useless old config version file
                            return gson.fromJson(f.readLines().joinToString(""), T::class.java)
                        } else if (!f.createNewFile()){
                            //don't delete old file if the new one can't be generated to take its place
                            println("Failed to create new config file ($file), using old config with new defaults.")
                        } else {
                            p.delete() //attempts to delete the now useless old config version file
                            f.writeText(gson.toJson(newClass))
                        }
                        return newClass
                    }
                } else {
                    throw RuntimeException("Old config not properly set up as an OldConfig: ${P::class.simpleName}")
                }
            } else {
                return readOrCreate(file, configClass = configClass)
            }
        } catch (e: Exception) {
            println("Failed to read config file! Using default values: " + e.message)
            return configClass()
        }
    }

    fun configAugment(file: String, configClass: AugmentStats): AugmentStats{
        return readOrCreate(file,"augments") {configClass}
    }

    @Deprecated("moving to amethyst_core")
    fun makeDir(child:String = ""): Pair<File,Boolean>{
        val dir = if (child != ""){
            File(File(FabricLoader.getInstance().configDir.toFile(), AI.MOD_ID),child)
        } else {
            File(FabricLoader.getInstance().configDir.toFile(), AI.MOD_ID)
        }
        if (!dir.exists() && !dir.mkdirs()) {
            println("Could not create directory, using default configs.")
            return Pair(dir,false)
        }
        return Pair(dir,true)
    }

    @Deprecated("moving to amethyst_core")
    fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(scepters))
        buf.writeString(gson.toJson(altars))
        buf.writeString(gson.toJson(colors))
    }

    @Deprecated("moving to amethyst_core")
    fun readFromServer(buf:PacketByteBuf){
        scepters = gson.fromJson(buf.readString(),Scepters::class.java)
        altars = gson.fromJson(buf.readString(),Altars::class.java)
        colors = gson.fromJson(buf.readString(),Colors::class.java)
    }


    class Scepters {
        var opalineDurability: Int = ScepterLvl1ToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterLvl1ToolMaterial.baseCooldown()
    }

    class Altars {
        var disenchantLevelCosts: List<Int> = listOf(3, 5, 9, 15, 23)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableReplaceEnchantingTable: Boolean = false
        var imbuingTableDifficultyModifier: Float = 1.0F
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5
    }

    class AugmentStats {
        var id: String = ScepterObject.fallbackAugment
        var cooldown: Int = 20
        var manaCost: Int = 2
        var minLvl: Int = 1
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

    class ColorsV0: OldClass{

        var defaultColorMap: Map<String,String> = mapOf(
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

    class AltarsV0: OldClass {
        var disenchantLevelCosts: List<Int> = listOf(3, 5, 9, 15, 23)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableDifficultyModifier: Float = 1.0F
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5

        override fun generateNewClass(): Any {
            val altars = Altars()
            altars.disenchantLevelCosts = disenchantLevelCosts
            altars.disenchantBaseDisenchantsAllowed = disenchantBaseDisenchantsAllowed
            altars.imbuingTableEnchantingEnabled = imbuingTableEnchantingEnabled
            altars.imbuingTableDifficultyModifier = imbuingTableDifficultyModifier
            altars.altarOfExperienceBaseLevels = altarOfExperienceBaseLevels
            altars.altarOfExperienceCandleLevelsPer = altarOfExperienceCandleLevelsPer
            return altars
        }
    }

    @Deprecated("moving to amethyst_core")
    interface OldClass{

        fun generateNewClass(): Any

    }
}