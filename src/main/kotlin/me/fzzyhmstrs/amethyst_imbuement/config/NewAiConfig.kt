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


    private fun buildSectionHeader(name:String): Header{
        return Header.Builder().underoverscore("readme.header.$name").add("readme.header.$name.desc").space().build()
    }

}