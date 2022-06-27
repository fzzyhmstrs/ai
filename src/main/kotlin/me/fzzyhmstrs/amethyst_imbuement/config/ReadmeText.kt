package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.amethyst_core.coding_util.SyncedConfigHelper

object ReadmeText: SyncedConfigHelper.ReadMeWriter {

    override fun readmeText(): List<String>{
        return listOf(
            "README",
            "Amethyst Imbuement",
            "------------------",
            "",
            "Config Changelog:",
            "1.18.1-13/1.18.2-14: Added imbuingTableReplaceEnchantingTable to the Altars Config. Config updated to v1.",
            "",
            "Note:  Previous versions of updated configs that have new version numbers (v1 from v0, for example) will be read and copied over to the new version, and the old version deleted.",
            "",
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
            "> imbuingTableReplaceEnchantingTable: disabled by default. Enable to replace all Enchanting Tables with Imbuing tables during structure generation. Doesn't affect existing structures or tables.",
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
            "> defaultColorMap: A map of the default colors pre-assigned to vanilla ores. By default includes some ores from common ore-gen mods.",
            "> defaultRainbowList: List of the vanilla ores that are pre-assigned a rainbow outline. By default includes some ores from common ore-gen mods.",
            "> modColorMap: A map where you can add ores from other mods you are playing with.",
            "> modRainbowList: List for mod ores you want to appear as a rainbow outline.",
            "",
            "",
            "Augments Configs:",
            "These configs allow you to tweak the casting parameters for individual spells. Change these if you perhaps think a spell doesn't cost enough mana, or is too slow between casts",
            "",
            "> id: don't change this, or the config will break for that spell.",
            "> cooldown: time in ticks (20 per second) between each cost of the spell.",
            "> manaCost: durability usage of the scepter on each cast. Note the default durabilities in the scepter config to determine proper values.",
            "> minLvl: the minimum scepter level required before the spell can be used in that scepter. Recommend keeping as-is, but may be useful to tweak if you think a spell is too easy to attain, for example.",
            "",
            "",
            "Villages Configs",
            "These configs allow you to tweak the generation for the Crystal Workshops",
            "> enable[Village_Type]Workshops: True by default. If for some reason you wish to disable generation completely, set to false.",
            "> [Village_Type]WorkshopWeight: The weight that the workshops are selected from the building pool for spawning in villages. Recommend leaving as-is.",
            "",
            "",
            "Enchantments Config",
            "This config allows you to enable or disable the various vanilla-style enchantments added by the mod. You may want to disable an enchantment this way if, for example, an enchantment is redundant to another mods enchantment and you want to reduce clutter.",
            "",
            "> enabledEnchantments: A map of the enchantments the mod adds and if they are enabled. All enchantments are enabled by default."
        )

    }

}