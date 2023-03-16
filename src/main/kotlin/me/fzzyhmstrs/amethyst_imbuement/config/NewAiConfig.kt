package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_config.config_util.ConfigClass
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.config_util.ReadMeText
import me.fzzyhmstrs.fzzy_config.config_util.SyncedConfigWithReadMe
import me.fzzyhmstrs.fzzy_config.validated_field.*


object NewAiConfig
    :
    SyncedConfigWithReadMe(
        AI.MOD_ID,
        "README.txt",
        AI.MOD_ID,
        Header.Builder()
            .box("readme.main_header.title")
            .space()
            .add("readme.main_header.changelog")
            .literal()
            .add("1.18.1-13/1.18.2-14: Added imbuingTableReplaceEnchantingTable to the Altars Config. Config updated to v1.")
            .add("1.19-09/1.18.2-26: updated Altars to v2 with the addition of many (currently unused) integration options. Updated Villages to v1 with the addition of many options related to CTOV and RS. Updated Scepters to v1 and added default durabilities/damage values for the Scepter of Blades and Lethality.")
            .add("1.19-11/1.18.2-28: Added the entities config file.")
            .add("1.19-14/1.18.2-31: Added the trinkets config file and updated Entities to v1 with (currently unused) selections.")
            .add("1.19-22/1.18.2-39: Changed the scepters config from scepters_v1 to items_v0 and added the glistering tome boolean.")
            .add("1.19.3-02/1.19-25/1.18.2-42: Added a config for the chance an experience bush will grow (in Altars config v3).")
            .add("1.19.3-03/1.19-26/1.18.2-43: Added configurable durability for the Totem of Amethyst.")
            .add("1.19.4-01/1.19.3-06/1.19-29/1.18.2-46: Completely rebuilt the config system using fzzy config. Added many new config selections as detailed below.")
            .space()
            .translate()
            .add("readme.main_header.note")
            .space()
            .space()
            .build())
{
    private val itemsHeader = buildSectionHeader("items")

    class Items: ConfigClass(itemsHeader){
        @ReadMeText("readme.items.giveGlisteringTome")
        var giveGlisteringTome = ValidatedBoolean(true)
        var totemOfAmethystDurability = ValidatedInt(360,1000,32)
        var scepters = ScepterSection()
        class ScepterSection: ConfigSection(Header.Builder().space().add("readme.items.scepters_1").add("readme.items.scepters_2").add("readme.items.scepters_3").build()){
            var opalineDurability = ValidatedInt(ScepterLvl1ToolMaterial.defaultDurability(),1250,32)
            var opalineCooldown = ValidatedLong(ScepterLvl1ToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterLvl1ToolMaterial.minCooldown())
            var iridescentDurability = ValidatedInt(ScepterLvl2ToolMaterial.defaultDurability(),1650,64)
            var iridescentCooldown = ValidatedLong(ScepterLvl2ToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterLvl2ToolMaterial.minCooldown())
            var lustrousDurability = ValidatedInt(ScepterLvl3ToolMaterial.defaultDurability(),3550,128)
            var lustrousCooldown = ValidatedLong(ScepterLvl3ToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterLvl3ToolMaterial.minCooldown())
            var bladesDurability = ValidatedInt(ScepterOfBladesToolMaterial.defaultDurability(),1250,32)
            var bladesCooldown = ValidatedLong(ScepterOfBladesToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterOfBladesToolMaterial.minCooldown())
            @ReadMeText("readme.items.bladesDamage")
            var bladesDamage = ValidatedFloat(ScepterOfBladesToolMaterial.defaultAttackDamage(),20f,0f)
            var lethalityDurability = ValidatedInt(LethalityToolMaterial.defaultDurability(),3250,128)
            var lethalityCooldown = ValidatedLong(LethalityToolMaterial.baseCooldown(), Long.MAX_VALUE,LethalityToolMaterial.minCooldown())
            @ReadMeText("readme.items.lethalityDamage")
            var lethalityDamage = ValidatedFloat(LethalityToolMaterial.defaultAttackDamage(),30f,0f)
        }
    }
    
    private val altarsHeader = buildSectionHeader("altars")
    
    class Altars: ConfigClass(altarsHeader){
        var xpBush: XpBush()
        class XpBush: ConfigSection(Header.Builder().space().add("readme.altars.xp_bush_1").add("readme.altars.xp_bush_2").build()){
            var bonemealChance = ValidatedFloat(0.4f,1f,0f)
            var growChance = ValidatedFloat(0.15f,1f,0f)
        }
        
        var disenchanter: Disenchanter()
        class Disenchanter: ConfigSection(Header.Builder().space().add("readme.altars.disenchant_1").add("readme.altars.disenchant_2").build()){
            var levelCosts = ValidatedIntList(listOf(11, 17, 24, 33, 44), {i -> i >= 0}, "Needs integers greater than or equal to 0")
            var baseDisenchantsAllowed = ValidatedInt(1,Int.MAX_VALUE,0)
        }
        
        var imbuing: Imbuing()
        class Imbuing: ConfigSection(Header.Builder().space().add("readme.items.imbuing_1").add("readme.items.imbuing_2").build()){
            var enchantingEnabled = ValidatedBoolean(true)
            var replaceEnchantingTable = ValidatedBoolean(false)
            @ReadMeText("readme.altars.imbuing.difficultyModifier")
            var difficultyModifier = ValidatedFloat(1.0F,10f,0f)
            
            var easyMagic = EasyMagic()
            class EasyMagic: ConfigSection(Header.Builder().add("readme.items.imbuing_easy_1").add("readme.items.imbuing_easy_2").build()){
                var matchEasyMagicBehavior = ValidatedBoolean(true)
                var rerollEnabled = ValidatedBoolean(true)
                var levelCost = ValidatedInt(5,Int.MAX_VALUE,0)
                var lapisCost = ValidatedInt(1,Int.MAX_VALUE,0)
                var showTooltip = ValidatedBoolean(true)
                var singleEnchantTooltip = ValidatedBoolean(true)
            }
            
            var reroll: Reroll()
            class Reroll:ConfigSection(Header.Builder().add("readme.items.imbuing_reroll_1").add("readme.items.imbuing_reroll_2").build()){
                var matchRerollBehavior = ValidatedBoolean(true)
                var levelCost = ValidatedInt(1,Int.MAX_VALUE,0)
                var lapisCost = ValidatedInt(0,INT.MAX_VALUE,0)
            }
        }
    
        var altar: Altar()
        class Altar: ConfigSection(Header.Builder().space().add("readme.altars.altar_1").add("readme.altars.altar_2").build()){
            var baseLevels: Int = 35
            var candleLevelsPer: Int = 5
            @ReadMeText("readme.altars.altar.customXpMethod")
            var customXpMethod: Boolean = true
        }
    }

    private val villagesHeader = buildSectionHeader("villages")
    
    class Villages: ConfigClass(villagesHeader){
        var vanilla: Vanilla()
        class Vanilla: ConfigSection(Header.Builder().space().add("readme.villages.vanilla_1").add("readme.villages.vanilla_2").build()){
            var enableDesertWorkshops = ValidatedBoolean(true)
            var desertWorkshopWeight = ValidatedInt(2,100,1)
            var enablePlainsWorkshops = ValidatedBoolean(true)
            var plainsWorkshopWeight = ValidatedInt(3,100,1)
            var enableSavannaWorkshops = ValidatedBoolean(true)
            var savannaWorkshopWeight = ValidatedInt(3,100,1)
            var enableSnowyWorkshops = ValidatedBoolean(true)
            var snowyWorkshopWeight = ValidatedInt(2,100,1)
            var enableTaigaWorkshops = ValidatedBoolean(true)
            var taigaWorkshopWeight = ValidatedInt(3,100,1)
        }
        
        var ctov: Ctov()
        class Ctov: ConfigSection(Header.Builder().space().add("readme.villages.ctov_1").add("readme.villages.ctov_2").build()){
            var enableCtovWorkshops = ValidatedBoolean(true)
            var beachWorkshopWeight = ValidatedInt(4,100,1)
            var darkForestWorkshopWeight = ValidatedInt(4,100,1)
            var jungleWorkshopWeight = ValidatedInt(4,100,1)
            var jungleTreeWorkshopWeight = ValidatedInt(4,100,1)
            var mesaWorkshopWeight = ValidatedInt(4,100,1)
            var mesaFortifiedWorkshopWeight = ValidatedInt(4,100,1)
            var mountainWorkshopWeight = ValidatedInt(4,100,1)
            var mountainAlpineWorkshopWeight = ValidatedInt(4,100,1)
            var mushroomWorkshopWeight = ValidatedInt(4,100,1)
            var swampWorkshopWeight = ValidatedInt(4,100,1)
            var swampFortifiedWorkshopWeight = ValidatedInt(4,100,1)
        }
        
        var rs: Rs()
        class Rs: ConfigSection(Header.Builder().space().add("readme.villages.ctov_1").add("readme.villages.ctov_2").build()){
            var enableRsWorkshops = ValidatedBoolean(true)
            var badlandsWorkshopWeight = ValidatedInt(2,100,1)
            var birchWorkshopWeight = ValidatedInt(2,100,1)
            var darkForestWorkshopWeight = ValidatedInt(2,100,1)
            var giantTaigaWorkshopWeight = ValidatedInt(1,100,1)
            var jungleWorkshopWeight = ValidatedInt(2,100,1)
            var mountainsWorkshopWeight = ValidatedInt(2,100,1)
            var mushroomsWorkshopWeight = ValidatedInt(2,100,1)
            var oakWorkshopWeight = ValidatedInt(2,100,1)
            var swampWorkshopWeight = ValidatedInt(2,100,1)
            var crimsonWorkshopWeight = ValidatedInt(2,100,1)
            var warpedWorkshopWeight = ValidatedInt(2,100,1)
        }
    }
    
    private val enchantsHeader = buildSectionHeader("enchants")
    
    class Enchants: Villages: ConfigClass(enchantsHeader){
        
        @ReadMeText("readme.enchants.disableExtraEnchantLevels")
        var disableExtraEnchantLevels = ValidatedBoolean(false)
        
        @ReadMeText("readme.enchants.enabledEnchants")
        var enabledEnchants = ValidatedStringBoolMap(AiConfigDefaults.enabledEnchantments,{id,_ -> Registries.ENCHANTMENT.containsId(Identifier.tryParse(id))}, "Needs a valid registered enchantment identifier.")
        
        @ReadMeText("readme.enchants.enchantCosts")
        var enchantCosts = ValidatedStringIntMap(AiConfigDefaults.enchantmentCosts,{id,i -> Registries.ENCHANTMENT.containsId(Identifier.tryParse(id)) && i > 0}, "Needs a valid registered enchantment identifier and a level greater than 0.")
    }

    private fun buildSectionHeader(name:String): Header{
        return Header.Builder().space().underoverscore("readme.header.$name").add("readme.header.$name.desc").space().build()
    }

}
