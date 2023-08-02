package me.fzzyhmstrs.amethyst_imbuement.config

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CholemEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.FleshGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.LavaGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.hamster.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.hamster.HambieEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.hamster.HamethystEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.DraftHorseEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.SeahorseEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BonestormEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.FloralConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.zombie.BonesEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.zombie.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.tool.*
import me.fzzyhmstrs.fzzy_config.config_util.*
import me.fzzyhmstrs.fzzy_config.interfaces.OldClass
import me.fzzyhmstrs.fzzy_config.validated_field.*
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedIntList
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedSeries
import me.fzzyhmstrs.fzzy_config.validated_field.map.ValidatedStringBoolMap
import me.fzzyhmstrs.fzzy_config.validated_field.map.ValidatedStringIntMap
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import kotlin.math.max


object AiConfig
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
            .add("1.19.4-01/1.19.3-09/1.19-32: Updated the values of some scepters and added two new material configs. Added a new trinket config for turning off burnout on totem augments.")
            .add("1.19.4-01/1.19.3-12/1.19-35: Tweaked the default values for the healers gem and brutal gem in items_v4. Adds configs for the Hard Light block in the renamed Blocks_v0")
            .add("1.20-01/1.19.4-01/1.19.3-13/1.19-36: Added hamster and bonestorm configs in entities_v2. Updated to items_v5 with fzzyhammer and harvest scepter info and new loot chances for unique items.")
            .space()
            .translate()
            .add("readme.main_header.note")
            .space()
            .space()
            .build())
{
    private val itemsHeader = buildSectionHeader("items")

    class Items: ConfigClass(itemsHeader), OldClass<Items> {

        @ReadMeText("readme.items.giveGlisteringTome")
        var giveGlisteringTome = ValidatedBoolean(true)

        var manaItems = ManaItems()
        class ManaItems: ConfigSection(Header.Builder().space().add("readme.items.manaItems_1").add("readme.items.manaItems_2").build()) {
            var totemOfAmethystDurability = ValidatedInt(360, 1000, 32)
            var imbuedJewelryDurability = ValidatedInt(120, 1000, 32)
            @ReadMeText("readme.items.manaItems.imbuedJewelryDamagePerAmplifier")
            var imbuedJewelryDamagePerAmplifier = ValidatedInt(6,30,0)
            @ReadMeText("readme.items.manaItems.fullManaColor")
            var fullManaColor = ValidatedColor(0,85,255)
            @ReadMeText("readme.items.manaItems.fullManaColor")
            var emptyManaColor = ValidatedColor(255,0,85)

            fun getItemBarColor(stack:ItemStack): Int{
                val f = max(0.0f, (stack.maxDamage.toFloat() - stack.damage.toFloat()) / stack.maxDamage.toFloat())
                val r = ((f * fullManaColor.r.get()) + ((1-f)*emptyManaColor.r.get())).toInt()
                val g = ((f * fullManaColor.g.get()) + ((1-f)*emptyManaColor.g.get())).toInt()
                val b = ((f * fullManaColor.b.get()) + ((1-f)*emptyManaColor.b.get())).toInt()
                return ColorHelper.Argb.getArgb(255,r,g,b)
            }
        }

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
            @ReadMeText("readme.items.scepters.bladesDamage")
            var bladesDamage = ValidatedFloat(ScepterOfBladesToolMaterial.defaultAttackDamage(),20f,0f)
            var lethalityDurability = ValidatedInt(LethalityToolMaterial.defaultDurability(),3250,128)
            var lethalityCooldown = ValidatedLong(LethalityToolMaterial.baseCooldown(), Long.MAX_VALUE,LethalityToolMaterial.minCooldown())
            @ReadMeText("readme.items.scepters.lethalityDamage")
            var lethalityDamage = ValidatedFloat(LethalityToolMaterial.defaultAttackDamage(),30f,0f)
            var vanguardDurability = ValidatedInt(ScepterOfTheVanguardToolMaterial.defaultDurability(),1650,128)
            var vanguardCooldown = ValidatedLong(ScepterOfTheVanguardToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterOfTheVanguardToolMaterial.minCooldown())
            @ReadMeText("readme.items.scepters.vanguardDamage")
            var vanguardDamage = ValidatedFloat(ScepterOfTheVanguardToolMaterial.defaultAttackDamage(),20f,0f)
            var buildersDurability = ValidatedInt(BuildersScepterToolMaterial.defaultDurability(),1650,128)
            var buildersCooldown = ValidatedLong(BuildersScepterToolMaterial.baseCooldown(), Long.MAX_VALUE,BuildersScepterToolMaterial.minCooldown())
            @ReadMeText("readme.items.scepters.buildersDamage")
            var buildersDamage = ValidatedFloat(BuildersScepterToolMaterial.defaultAttackDamage(),20f,0f)
            @ReadMeText("readme.items.scepters.buildersMiningSpeed")
            var buildersMiningSpeed = ValidatedFloat(BuildersScepterToolMaterial.defaultMiningSpeed(),12f,1f)
            var fowlDurability = ValidatedInt(ScepterSoFoulToolMaterial.defaultDurability(),1650,32)
            var fowlCooldown = ValidatedLong(ScepterSoFoulToolMaterial.baseCooldown(), Long.MAX_VALUE,ScepterSoFoulToolMaterial.minCooldown())
            var fowlChestChance = ValidatedFloat(0.02f,1f,0f)
            var fzzyDurability = ValidatedInt(FzzyhammerToolMaterial.defaultDurability(),1650,128)
            var fzzyCooldown = ValidatedLong(FzzyhammerToolMaterial.baseCooldown(), Long.MAX_VALUE,FzzyhammerToolMaterial.minCooldown())
            @ReadMeText("readme.items.scepters.fzzyDamage")
            var fzzyDamage = ValidatedFloat(FzzyhammerToolMaterial.defaultAttackDamage(),30f,0f)
            @ReadMeText("readme.items.scepters.fzzyMiningSpeed")
            var fzzyMiningSpeed = ValidatedFloat(FzzyhammerToolMaterial.defaultMiningSpeed(),12f,1f)
            var fzzyChestChance = ValidatedFloat(0.002f,1f,0f)
            var harvestDurability = ValidatedInt(ScepterOfHarvestsToolMaterial.defaultDurability(),1650,128)
            var harvestCooldown = ValidatedLong(ScepterOfHarvestsToolMaterial.baseCooldown(), Long.MAX_VALUE,BuildersScepterToolMaterial.minCooldown())
            @ReadMeText("readme.items.scepters.harvestMiningSpeed")
            var harvestMiningSpeed = ValidatedFloat(ScepterOfHarvestsToolMaterial.defaultMiningSpeed(),12f,1f)
            @ReadMeText("readme.items.scepters.uniqueWitherChance")
            var uniqueWitherChance = ValidatedFloat(0.01f,1f,0f)
        }

        var gems = Gems()
        class Gems: ConfigSection(Header.Builder().space().add("readme.items.gem_1").add("readme.items.gems_2").build()){
            @ReadMeText("readme.items.gems.fireTarget")
            var fireTarget = ValidatedInt(120,1200,1)
            @ReadMeText("readme.items.gems.hitTarget")
            var hitTarget = ValidatedInt(60,600,1)
            @ReadMeText("readme.items.gems.healTarget")
            var healTarget = ValidatedFloat(120f,1200f,1f)
            @ReadMeText("readme.items.gems.statusesTarget")
            var statusesTarget = ValidatedInt(8,42,1)
            @ReadMeText("readme.items.gems.killTarget")
            var killTarget = ValidatedInt(30,300,1)
            @ReadMeText("readme.items.gems.spellXpTarget")
            var spellXpTarget = ValidatedInt(350,3500,1)
        }

        var focus = Focus()
        class Focus: ConfigSection(Header.Builder().space().add("readme.items.focus_1").build()) {
            var bolsteringRange = ValidatedDouble(5.0,50.0,1.0)
            var tierXp = ValidatedSeries(arrayOf(500,1500,3000,5000),Int::class.java,{a,b-> b>a},"Xp tier value is a cumulative XP value. Each tier needs higher xp than the last.")
        }

        var scroll = Scroll()
        class Scroll: ConfigSection(Header.Builder().space().add("readme.items.scroll_1").build()) {
            @ReadMeText("readme.items.scroll.uses")
            var uses = ValidatedSeries(arrayOf(16,24,32),Int::class.java,{a,b-> b>a},"Higher tier scrolls need more uses than the previous tier.")
            @ReadMeText("readme.items.scroll.levels")
            var levels = ValidatedSeries(arrayOf(1,2,3,5,7),Int::class.java,{a,b-> b>a},"Spell levels need to increase from one to the next tier.")
        }
        
        override fun generateNewClass(): Items {
            val items = this
            items.scepters.buildersMiningSpeed.validateAndSet(BuildersScepterToolMaterial.defaultMiningSpeed())
            return items
        }
    }

    private val materialsHeader = buildSectionHeader("materials")
    
    class Materials: ConfigClass(materialsHeader), OldClass<Materials> {
        var armor: Armor()
        class Armor: ConfigSection(Header.Builder().space().add("readme.materials.armor_1").build()) {
            var ametrine = AiArmorMaterialsConfig.AMETRINE
            var steel = AiArmorMaterialsConfig.STEEL
        }
        var tools: Tools()
        class Tools: ConfigSection(Header.Builder().space().add("readme.materials.tools_1").build()) {
        }
        var scepters: Scepters()
        class Scepters: ConfigSection(Header.Builder().space().add("readme.materials.scepters_1").build()) {
        }
    }
    
    private val blocksHeader = buildSectionHeader("altars")
    
    class Blocks: ConfigClass(blocksHeader), OldClass<Blocks>{

        fun isCreateBlockTemporary(): Boolean{
            return hardLight.createTemporary.get() && hardLight.temporaryDuration.get() > 0
        }

        fun isBridgeBlockTemporary(): Boolean{
            return hardLight.bridgeTemporary.get() && hardLight.temporaryDuration.get() > 0
        }

        var hardLight = HardLight()
        class HardLight: ConfigSection(Header.Builder().space().add("readme.altars.hard_light_1").add("readme.altars.hard_light_2").build()){
            @ReadMeText("readme.altars.hardLight.bridgeTemporary")
            var bridgeTemporary = ValidatedBoolean(false)
            @ReadMeText("readme.altars.hardLight.createTemporary")
            var createTemporary = ValidatedBoolean(false)
            @ReadMeText("readme.altars.hardLight.temporaryDuration")
            var temporaryDuration = ValidatedInt(600,Int.MAX_VALUE)
        }
            
        var xpBush = XpBush()
        class XpBush: ConfigSection(Header.Builder().space().add("readme.altars.xp_bush_1").add("readme.altars.xp_bush_2").build()){
            var bonemealChance = ValidatedFloat(0.4f,1f,0f)
            var growChance = ValidatedFloat(0.15f,1f,0f)
        }
        
        var disenchanter = Disenchanter()
        class Disenchanter: ConfigSection(Header.Builder().space().add("readme.altars.disenchant_1").add("readme.altars.disenchant_2").build()){
            @ReadMeText("readme.altars.disenchanter.levelCosts")
            var levelCosts = ValidatedIntList(listOf(11, 17, 24, 33, 44), {i -> i >= 0}, "Needs integers greater than or equal to 0")
            var baseDisenchantsAllowed = ValidatedInt(1,Int.MAX_VALUE,0)
        }
        
        var imbuing = Imbuing()
        class Imbuing: ConfigSection(Header.Builder().space().add("readme.altars.imbuing_1").add("readme.altars.imbuing_2").build()){

            fun getRerollEnabled(): Boolean{
                return easyMagic.matchEasyMagicBehavior.get() && easyMagic.rerollEnabled.get()

            }

            fun getLapisCost(): Int{
                if(getRerollEnabled()){
                    return easyMagic.lapisCost.get()
                }
                return 0
            }

            fun getLevelCost(): Int{
                if(getRerollEnabled()){
                    return easyMagic.levelCost.get()
                }
                return 0
            }

            var enchantingEnabled = ValidatedBoolean(true)
            var replaceEnchantingTable = ValidatedBoolean(false)
            @ReadMeText("readme.altars.imbuing.difficultyModifier")
            var difficultyModifier = ValidatedFloat(1.0F,10f,0f)
            
            var easyMagic = EasyMagic()
            class EasyMagic: ConfigSection(Header.Builder().add("readme.altars.imbuing_easy_1").add("readme.altars.imbuing_easy_2").build()){
                var matchEasyMagicBehavior = ValidatedBoolean(true)
                var rerollEnabled = ValidatedBoolean(true)
                var levelCost = ValidatedInt(5,Int.MAX_VALUE,0)
                var lapisCost = ValidatedInt(1,Int.MAX_VALUE,0)
                var showTooltip = ValidatedBoolean(true)
                var singleEnchantTooltip = ValidatedBoolean(true)
            }

            @Deprecated("Don't need this, as I'm not mod-checking any more. Can remove next config update")
            var reroll = Reroll()
            class Reroll:ConfigSection(Header.Builder().add("readme.altars.imbuing_reroll_1").add("readme.altars.imbuing_reroll_2").build()){
                var matchRerollBehavior = ValidatedBoolean(true)
                var levelCost = ValidatedInt(1,Int.MAX_VALUE,0)
                var lapisCost = ValidatedInt(0,Int.MAX_VALUE,0)
            }
        }
    
        var altar = Altar()
        class Altar: ConfigSection(Header.Builder().space().add("readme.altars.altar_1").add("readme.altars.altar_2").build()){
            var baseLevels = ValidatedInt(35,Int.MAX_VALUE,0)
            var candleLevelsPer = ValidatedInt(5,Int.MAX_VALUE/16,0)
            @ReadMeText("readme.altars.altar.customXpMethod")
            var customXpMethod = ValidatedBoolean(true)
        }
        
        override fun generateNewClass(): Blocks {
            return this
        }
    }

    private val villagesHeader = buildSectionHeader("villages")
    
    class Villages: ConfigClass(villagesHeader){
        var vanilla = Vanilla()
        class Vanilla: ConfigSection(Header.Builder().space().add("readme.villages.vanilla_1").build()){
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
        
        var ctov = Ctov()
        class Ctov: ConfigSection(Header.Builder().space().add("readme.villages.ctov_1").build()){
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
        
        var rs = Rs()
        class Rs: ConfigSection(Header.Builder().space().add("readme.villages.rs_1").build()){
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
    
    class Enchants: ConfigClass(enchantsHeader){

        fun getAiMaxLevel(id: String, fallback: Int): Int{
            if (disableIncreaseMaxLevels.get()) return fallback
            return aiEnchantMaxLevels[id]?:fallback
        }
        
        fun getVanillaMaxLevel(id: String, fallback: Int): Int{
            if (disableIncreaseMaxLevels.get()) return fallback
            return vanillaEnchantMaxLevels[id]?:fallback
        }
        
        @ReadMeText("readme.enchants.disableIncreaseMaxLevels")
        var disableIncreaseMaxLevels = ValidatedBoolean(false)
        
        @ReadMeText("readme.enchants.enabledEnchants")
        var enabledEnchants = ValidatedStringBoolMap(AiConfigDefaults.enabledEnchantments,{id,_ -> Identifier.tryParse(id) != null}, invalidEntryMessage = "Needs a valid registered enchantment identifier.")
        
        @ReadMeText("readme.enchants.aiEnchantMaxLevels")
        var aiEnchantMaxLevels = ValidatedStringIntMap(AiConfigDefaults.aiEnchantmentMaxLevels,{ id, i -> Identifier.tryParse(id) != null && i > 0}, invalidEntryMessage = "Needs a valid registered enchantment identifier and a level greater than 0.")

        @ReadMeText("readme.enchants.vanillaEnchantMaxLevels")
        var vanillaEnchantMaxLevels = ValidatedStringIntMap(AiConfigDefaults.vanillaEnchantmentMaxLevels,{ id, i -> Identifier.tryParse(id) != null && i > 0}, invalidEntryMessage = "Needs a valid registered enchantment identifier and a level greater than 0.")
    }

    private val trinketsHeader = buildSectionHeader("trinkets")

    class Trinkets: ConfigClass(trinketsHeader), OldClass<Trinkets>{
        @ReadMeText("readme.trinkets.enableBurnout")
        var enableBurnout = ValidatedBoolean(true)
        @ReadMeText("readme.trinkets.draconicVisionRange")
        var draconicVisionRange = ValidatedInt(5,16,1)
        @ReadMeText("readme.trinkets.enabledAugments")
        var enabledAugments = ValidatedStringBoolMap(AiConfigDefaults.enabledAugments,{id,_ -> Identifier.tryParse(id) != null}, invalidEntryMessage = "Needs a valid registered enchantment identifier.")

        override fun generateNewClass(): Trinkets {
            return this
        }
    }

    private val entitiesHeader = buildSectionHeader("entities")

    class Entities: ConfigClass(entitiesHeader), OldClass<Entities>{
        fun isEntityPvpTeammate(user: LivingEntity?, entity: Entity, spell: ScepterAugment?): Boolean{
            if (user == null) return false
            if (user === entity) return true
            if (forcePvpOnAllSpells.get() || spell?.getPvpMode() == true){
                return user.isTeammate(entity)
            }
            return true
        }

        fun isEntityPvpTeammate(user: LivingEntity?, entity: Entity): Boolean{
            if (user == null) return false
            if (user === entity) return true
            if (forcePvpOnAllSpells.get()){
                return user.isTeammate(entity)
            }
            return true
        }

        @ReadMeText("readme.entities.forcePvpOnAllSpells")
        var forcePvpOnAllSpells = ValidatedBoolean(false)

        var unhallowed = UnhallowedEntity.Unhallowed()
        var bones = BonesEntity.Bones()
        var crystalGolem = CrystallineGolemEntity.CrystalGolem()
        var cholem = CholemEntity.Cholem()
        var lavaGolem = LavaGolemEntity.LavaGolem()
        var fleshGolem = FleshGolemEntity.FleshGolem()
        var hamster = BaseHamsterEntity.Hamster()
        var hamethyst = HamethystEntity.Hamethyst()
        var hambie = HambieEntity.Hambie()
        var bonestorm = BonestormEntity.Bonestorm()
        var floralConstruct = FloralConstructEntity.FloralConstruct()
        var chorse = ChorseEntity.Chorse()
        var draftHorse = DraftHorseEntity.DraftHorse()
        var seahorse = SeahorseEntity.Seahorse()

        override fun generateNewClass(): Entities {
            return this
        }
    }

    var items: Items = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("items_v5.json","items_v4.json", base = AI.MOD_ID, configClass = { Items() }, previousClass = {Items()})
    var blocks: Blocks = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("blocks_v0.json","altars_v4.json", base = AI.MOD_ID, configClass = {Blocks()}, previousClass = {Blocks()})
    var villages: Villages = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("villages_v2.json","villages_v1.json", base = AI.MOD_ID, configClass = {Villages()}, previousClass = {AiConfigOldClasses.VillagesV1()})
    var enchants: Enchants = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("enchantments_v1.json","enchantments_v0.json", base = AI.MOD_ID, configClass = { Enchants() }, previousClass = {AiConfigOldClasses.EnchantmentsV0()})
    var trinkets: Trinkets = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("trinkets_v2.json","trinkets_v1.json", base = AI.MOD_ID, configClass = {Trinkets()}, previousClass = {Trinkets()})
    var entities: Entities = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("entities_v2.json","entities_v1.json", base = AI.MOD_ID, configClass = {Entities()}, previousClass = {AiConfigOldClasses.EntitiesV0()})


    private fun buildSectionHeader(name:String): Header{
        return Header.Builder().space().underoverscore("readme.header.$name").add("readme.header.$name.desc").space().build()
    }

}
