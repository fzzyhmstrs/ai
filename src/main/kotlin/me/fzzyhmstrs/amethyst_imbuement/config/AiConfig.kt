package me.fzzyhmstrs.amethyst_imbuement.config

import com.google.gson.GsonBuilder
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl2ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl3ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterMaterialAddon
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterToolMaterial
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import java.io.File
import java.io.FileWriter

object AiConfig {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    var scepters: Scepters
    var altars: Altars
    var colors: Colors

    init {
        scepters = readOrCreate("scepters_v0.json") { Scepters() }
        altars = readOrCreate("altars_v0.json") { Altars() }
        colors = readOrCreate("colors_v0.json") { Colors() }
        ReadMe.writeReadMe("README.txt")
    }

    fun initConfig(){}

    private inline fun <reified T> readOrCreate(file: String, configClass: () -> T): T {
        val dirPair = makeDir()
        if (!dirPair.second) {
            return configClass()
        }
        val dir = dirPair.first
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

    private fun makeDir(): Pair<File,Boolean>{
        val dir = File(FabricLoader.getInstance().configDir.toFile(), AI.MOD_ID)
        if (!dir.exists() && !dir.mkdirs()) {
            println("Could not create directory, using default configs.")
            return Pair(dir,false)
        }
        return Pair(dir,true)
    }

    fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(scepters))
        buf.writeString(gson.toJson(altars))
        buf.writeString(gson.toJson(colors))
    }

    fun readFromServer(buf:PacketByteBuf){
        scepters = gson.fromJson(buf.readString(),Scepters::class.java)
        altars = gson.fromJson(buf.readString(),Altars::class.java)
        colors = gson.fromJson(buf.readString(),Colors::class.java)
    }

    class Scepters {
        var opalineDurability: Int = ScepterToolMaterial.defaultDurability()
        var iridescentDurability: Int = ScepterLvl2ToolMaterial.defaultDurability()
        var lustrousDurability: Int = ScepterLvl3ToolMaterial.defaultDurability()
        var baseRegenRateTicks: Long = ScepterToolMaterial.baseCooldown()
    }

    class Altars {
        var disenchantLevelCosts: List<Int> = listOf(3, 5, 9, 15, 23)
        var disenchantBaseDisenchantsAllowed: Int = 1
        var imbuingTableEnchantingEnabled: Boolean = true
        var imbuingTableDifficultyModifier: Float = 1.0F
        var altarOfExperienceBaseLevels: Int = 35
        var altarOfExperienceCandleLevelsPer: Int = 5
    }

    class AugmentStats {
        var cooldown: Int = 20
        var manaCost: Int = 2
        var minLvl: Int = 1
    }

    class Colors{

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
    }

    private object ReadMe{
        val textLines: List<String> = listOf(
            "README",
            "Amethyst Imbuement",
            "------------------",
            "",
            "Scepters Config:",
            "The scepters config json tweaks the properties of the scepters in-game. You may want to tweak it if you feel like scepters have too many uses at once, or conversely if you feel that they run out of mana too quickly",
            "",
            "> opalineDurability: define durability for the Opaline Scepter (Low tier).",
            "> iridescentDurability: define durability for the Iridescent Scepter (mid tier).",
            "> lustrousDurability: define durability for the Lustrous Scepter (high tier).",
            "> baseRegenRateTicks: how quickly the scepters regain mana naturally. Value is in ticks (20 tick per second). 20 ticks is the minimum allowed.",
            "",
            "",
            "Altars Config:",
            "This json defines functional tweaks for the altars and tables in the mod.",
            "",
            "> disenchantLevelCosts: array of the levels required to disenchant the first, second, third, etc. enchantment off a particular item. You can extend this array if you'd like, but it won't do anything unless you also add to the base disenchants allowed. If you allow 3 base enchants, an array up to 7 long would have practical use.",
            "> disenchantBaseDisenchantsAllowed: the base number of disenchants allowed with just the table present before adding pillars. If you want virtually infinite disenchants, make this number very high. You could make it 0, meaning you have to add pillars before you can disenchant at all, but I don't recommend it.",
            "> imbuingTableEnchantingEnabled: disable this to prevent the player from using the imbuing table as an enchanting table. Use this if you have an alternate enchanting system and don't want the table to allow vanilla style enchanting.",
            "> imbuingTableDifficultyModifier: Multiplies the level cost of imbuing by the value entered. A value of 0.5 will halve the imbuing level costs, 2.0 will double them, and so on. Clamped between 0.0 (free) and 10.0",
            "> altarOfExperienceBaseLevels: base number of levels a player can store in an altar of experience surrounded by 0 candles.",
            "> altarOfExperienceCandleLevelsPer: number of storable levels each candle placed around the altar of experience adds. Warding Candles provide double this base bonus.",
            "",
            "",
            "Colors Config:",
            "This config sets the outline colors for the various ores when seen under the Draconic Vision augment. If an ore is not in these lists, it will appear as the default white outline. Map keys are the ore identifiers, which can be seen with the advanced tooltips turned on (F3+H by default). If an ore is in both a color map and a rainbow list, the rainbow list will take precedence. The mod color map takes precedence over the default one, in case you put a pre-existing color in the mod map, your desired color will still be shown",
            "",
            "Precedence: defaultRainbowList = modRainbowList > modColorMap > defaultColorMap",
            "",
            "> defaultColorMap: A map of the default colors pre-assigned to vanilla ores.",
            "> defaultRainbowList: List of the vanilla ores that are pre-assigned a rainbow outline",
            "> modColorMap: A map where you can add ores from other mods you are playing with. By default includes some ores from common ore-gen mods",
            "> modRainbowList: List for mod ores you want to appear as a rainbow outline. By default includes some ores from common ore-gen mods"
        )

        fun writeReadMe(file: String){
            val dirPair = makeDir()
            if (!dirPair.second){
                println("Couldn't make directory for storing the readme")
            }
            val f = File(dirPair.first,file)
            val fw = FileWriter(f)
            textLines.forEach {
                value -> fw.write(value)
                fw.write(System.getProperty("line.separator"))
            }
            fw.close()
        }
    }

}