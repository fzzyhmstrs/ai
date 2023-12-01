package me.fzzyhmstrs.amethyst_imbuement.config

import fzzyhmstrs.should_i_hit_that.api.MobCheckerBuilder
import fzzyhmstrs.should_i_hit_that.api.MobCheckers
import fzzyhmstrs.should_i_hit_that.api.ShouldHitResult
import fzzyhmstrs.should_i_hit_that.api.ShouldItHitPredicate
import fzzyhmstrs.should_i_hit_that.checkers.ExcludeTagChecker
import fzzyhmstrs.should_i_hit_that.checkers.MobChecker
import fzzyhmstrs.should_i_hit_that.checkers.PredicatedPassChecker
import me.fzzyhmstrs.amethyst_core.event.ShouldScrollEvent
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.material.AiArmorMaterialsConfig
import me.fzzyhmstrs.amethyst_imbuement.material.AiScepterMaterialsConfig
import me.fzzyhmstrs.amethyst_imbuement.material.AiToolMaterialsConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_config.config_util.*
import me.fzzyhmstrs.fzzy_config.interfaces.OldClass
import me.fzzyhmstrs.fzzy_config.validated_field.*
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedIntList
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedSeries
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedStringList
import me.fzzyhmstrs.fzzy_config.validated_field.map.ValidatedStringBoolMap
import me.fzzyhmstrs.fzzy_config.validated_field.map.ValidatedStringIntMap
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
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
            .add("1.20.1-10: Add materials_v0 to capture configurable gear materials. Add some durability configs to items and remove the previous items_v5 durability/damage configs.")
            .add("1.20.2-01/1.20.1-11: Vanilla enchantments are now configurable. Renamed 'trinkets_vX' to 'augments_vX'. Switched Bulwark to the augments config where it belongs. Added Soulwoven armor material to materials_v1. Added new entities in entities_v4. Added sky village integration configs in villages_v3")
            .add("1.20.2-01/1.20.1-16: Updated how PvpTeammates are considered. new Entities v5. Beast augments added into augments v4 config.")
            .add("1.20.2-01/1.20.1-17: Added Hud_v0 config for controlling client side gui-related items. Added a series of commands for controlling this in-game. Entities_v6 includes a massively overhauled Pvp checker based on the Should I Hit That API. Config option added for the default secondary checker options.")
            .space()
            .translate()
            .add("readme.main_header.note")
            .space()
            .space()
            .build()), SimpleSynchronousResourceReloadListener
{
    private val itemsHeader = buildSectionHeader("items")

    class Items: ConfigClass(itemsHeader), OldClass<Items> {

        @ReadMeText("readme.items.giveGlisteringTome")
        var giveGlisteringTome = ValidatedBoolean(true)

        var glisteringTridentDurability = ValidatedInt(550,5500,0)
        var sniperBowDurability = ValidatedInt(500,5000,0)

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
            var fowlChestChance = ValidatedFloat(0.02f,1f,0f)
            var fzzyChestChance = ValidatedFloat(0.002f,1f,0f)
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
            return this
        }
    }

    private val materialsHeader = buildSectionHeader("materials")
    
    class Materials: ConfigClass(materialsHeader), OldClass<Materials> {
        var armor = Armor()
        class Armor: ConfigSection(Header.Builder().space().add("readme.materials.armor_1").build()) {
            var ametrine = AiArmorMaterialsConfig.AMETRINE
            var steel = AiArmorMaterialsConfig.STEEL
            var garnet = AiArmorMaterialsConfig.GARNET
            var glowing = AiArmorMaterialsConfig.GLOWING
            var shimmering = AiArmorMaterialsConfig.SHIMMERING
            var soulwoven = AiArmorMaterialsConfig.SOULWOVEN
        }
        var tools = Tools()
        class Tools: ConfigSection(Header.Builder().space().add("readme.materials.tools_1").build()) {
            var garnet = AiToolMaterialsConfig.GARNET
            var glowing = AiToolMaterialsConfig.GLOWING
            var steel = AiToolMaterialsConfig.STEEL
            var ametrine = AiToolMaterialsConfig.AMETRINE
        }
        var scepters = Scepters()
        class Scepters: ConfigSection(Header.Builder().space().add("readme.materials.scepters_1").build()) {
            var tier1Scepter = AiScepterMaterialsConfig.SCEPTER_TIER_1
            var tier2Scepter = AiScepterMaterialsConfig.SCEPTER_TIER_2
            var tier3Scepter = AiScepterMaterialsConfig.SCEPTER_TIER_3

            var blades = AiScepterMaterialsConfig.SCEPTER_OF_BLADES
            var builder = AiScepterMaterialsConfig.BUILDERS_SCEPTER
            var fowl = AiScepterMaterialsConfig.SCEPTER_SO_FOWL
            var fzzyhammer = AiScepterMaterialsConfig.FZZYHAMMER
            var harvests = AiScepterMaterialsConfig.SCEPTER_OF_HARVESTS
            var lethality = AiScepterMaterialsConfig.LETHALITY
            var vanguard = AiScepterMaterialsConfig.SCEPTER_OF_THE_VANGUARD

        }

        override fun generateNewClass(): Materials {
            return this
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
            var desertWorkshopWeight = ValidatedInt(2,150,1)
            var enablePlainsWorkshops = ValidatedBoolean(true)
            var plainsWorkshopWeight = ValidatedInt(3,150,1)
            var enableSavannaWorkshops = ValidatedBoolean(true)
            var savannaWorkshopWeight = ValidatedInt(3,150,1)
            var enableSnowyWorkshops = ValidatedBoolean(true)
            var snowyWorkshopWeight = ValidatedInt(2,150,1)
            var enableTaigaWorkshops = ValidatedBoolean(true)
            var taigaWorkshopWeight = ValidatedInt(3,150,1)
        }
        
        var ctov = Ctov()
        class Ctov: ConfigSection(Header.Builder().space().add("readme.villages.ctov_1").build()){
            var enableCtovWorkshops = ValidatedBoolean(true)
            var beachWorkshopWeight = ValidatedInt(4,150,1)
            var darkForestWorkshopWeight = ValidatedInt(4,150,1)
            var jungleWorkshopWeight = ValidatedInt(4,150,1)
            var jungleTreeWorkshopWeight = ValidatedInt(4,150,1)
            var mesaWorkshopWeight = ValidatedInt(4,150,1)
            var mesaFortifiedWorkshopWeight = ValidatedInt(4,150,1)
            var mountainWorkshopWeight = ValidatedInt(4,150,1)
            var mountainAlpineWorkshopWeight = ValidatedInt(4,150,1)
            var mushroomWorkshopWeight = ValidatedInt(4,150,1)
            var swampWorkshopWeight = ValidatedInt(4,150,1)
            var swampFortifiedWorkshopWeight = ValidatedInt(4,150,1)
        }
        
        var rs = Rs()
        class Rs: ConfigSection(Header.Builder().space().add("readme.villages.rs_1").build()){
            var enableRsWorkshops = ValidatedBoolean(true)
            var badlandsWorkshopWeight = ValidatedInt(2,150,1)
            var birchWorkshopWeight = ValidatedInt(2,150,1)
            var darkForestWorkshopWeight = ValidatedInt(2,150,1)
            var giantTaigaWorkshopWeight = ValidatedInt(1,150,1)
            var jungleWorkshopWeight = ValidatedInt(2,150,1)
            var mountainsWorkshopWeight = ValidatedInt(2,150,1)
            var mushroomsWorkshopWeight = ValidatedInt(2,150,1)
            var oakWorkshopWeight = ValidatedInt(2,150,1)
            var swampWorkshopWeight = ValidatedInt(2,150,1)
            var crimsonWorkshopWeight = ValidatedInt(2,150,1)
            var warpedWorkshopWeight = ValidatedInt(2,150,1)
        }

        var sky = Sky()
        class Sky: ConfigSection(Header.Builder().space().add("readme.villages.sky_1").build()){
            var enableSkyWorkshops = ValidatedBoolean(true)
            var skyWorkshopWeight = ValidatedInt(40,150,1)
        }
    }
    
    private val enchantsHeader = buildSectionHeader("enchants")
    
    class Enchants: ConfigClass(enchantsHeader), OldClass<Enchants>{

        fun isEnchantEnabled(enchantment: Enchantment): Boolean{
            val id = (Registries.ENCHANTMENT.getId(enchantment) ?: return true).toString()
            return enabledEnchants[id] ?: true
        }
        fun getAiMaxLevel(enchantment: Enchantment, fallback: Int): Int{
            val id = (Registries.ENCHANTMENT.getId(enchantment) ?: return fallback).toString()
            val amount = aiEnchantMaxLevels[id] ?: fallback
            if (disableIncreaseMaxLevels.get() && amount > fallback) return fallback
            return amount
        }
        
        fun getVanillaMaxLevel(enchantment: Enchantment, fallback: Int): Int{
            val id = (Registries.ENCHANTMENT.getId(enchantment) ?: return fallback).toString()
            val amount = vanillaEnchantMaxLevels[id] ?: fallback
            if (disableIncreaseMaxLevels.get() && amount > fallback) return fallback
            return amount
        }
        
        @ReadMeText("readme.enchants.disableIncreaseMaxLevels")
        var disableIncreaseMaxLevels = ValidatedBoolean(false)
        
        @ReadMeText("readme.enchants.enabledEnchants")
        var enabledEnchants = ValidatedStringBoolMap(AiConfigDefaults.enabledEnchantments,{id,_ -> Identifier.tryParse(id) != null}, "Needs a valid registered enchantment identifier.")
        
        @ReadMeText("readme.enchants.aiEnchantMaxLevels")
        var aiEnchantMaxLevels = ValidatedStringIntMap(AiConfigDefaults.aiEnchantmentMaxLevels,{ id, i -> Identifier.tryParse(id) != null && i > 0}, "Needs a valid registered enchantment identifier and a level greater than 0.")

        @ReadMeText("readme.enchants.vanillaEnchantMaxLevels")
        var vanillaEnchantMaxLevels = ValidatedStringIntMap(AiConfigDefaults.vanillaEnchantmentMaxLevels,{ id, i -> Identifier.tryParse(id) != null && i > 0}, "Needs a valid registered enchantment identifier and a level greater than 0.")
        override fun generateNewClass(): Enchants {
            val enchants = this
            val map1 = enchants.enabledEnchants.toMutableMap()
            map1.remove("amethyst_imbuement:bulwark")
            enchants.enabledEnchants.validateAndSet(map1)
            val map2 = enchants.aiEnchantMaxLevels.toMutableMap()
            map2.remove("amethyst_imbuement:bulwark")
            val map3 = enchants.vanillaEnchantMaxLevels.toMutableMap()
            map2["minecraft:projectile_protection"] = 5
            enchants.vanillaEnchantMaxLevels.validateAndSet(map2)
            return enchants
        }
    }

    private val trinketsHeader = buildSectionHeader("trinkets")

    class Trinkets: ConfigClass(trinketsHeader), OldClass<Trinkets>{
        @ReadMeText("readme.trinkets.enableBurnout")
        var enableBurnout = ValidatedBoolean(true)
        @ReadMeText("readme.trinkets.draconicVisionRange")
        var draconicVisionRange = ValidatedInt(5,16,1)
        @ReadMeText("readme.trinkets.enabledAugments")
        var enabledAugments = ValidatedStringBoolMap(AiConfigDefaults.enabledAugments,{id,_ -> Identifier.tryParse(id) != null}, "Needs a valid registered enchantment identifier.")

        override fun generateNewClass(): Trinkets {
            val trinkets = this
            val map = trinkets.enabledAugments.toMutableMap()
            map["amethyst_imbuement:beast_master"] = true
            map["amethyst_imbuement:beast_magnet"] = true
            trinkets.enabledAugments.validateAndSet(map)
            return trinkets
        }
    }

    private val entitiesHeader = buildSectionHeader("entities")

    class Entities: ConfigClass(entitiesHeader), OldClass<Entities>{

        private val IS_PVP_NOT_FRIEND: MobChecker = PredicatedPassChecker(
            {_,_,args -> forcePvpOnAllSpells.get() || args.isNotEmpty() && (args[0] as? ScepterAugment)?.getPvpMode() == true},
            MobCheckers.NOT_FRIEND
        )
        private val IS_PVP_FRIEND: MobChecker = PredicatedPassChecker(
            {attacker,victim,args -> if (TogglePvpMobChecker.togglePvpInstalledButNotPvp(attacker, victim)) false else forcePvpOnAllSpells.get() || args.isNotEmpty() && (args[0] as? ScepterAugment)?.getPvpMode() == true},
            MobCheckers.FRIEND
        )
        private val TOGGLE_PVP_FRIEND: MobChecker = PredicatedPassChecker(
            {attacker,victim,_ -> TogglePvpMobChecker.areBothPvp(attacker, victim)},
            MobCheckers.FRIEND
        )
        private val NULL_NOT_MONSTER: MobChecker = PredicatedPassChecker(
            {attacker,_,_ -> attacker == null},
            {_,victim,_ -> if (victim is Monster) ShouldHitResult.FAIL else ShouldHitResult.PASS}
        )
        
        private val HIT_CHECKER = MobCheckerBuilder.sequence(
            MobCheckers.NON_NULL_HIT,
            MobCheckers.NOT_SELF,
            MobCheckers.NOT_MONSTER_FRIEND,
            MobCheckers.NOT_CLAIMED,
            MobCheckers.NOT_PET,
            TogglePvpMobChecker,
            IS_PVP_NOT_FRIEND,
            MobCheckers.NOT_PLAYER
        )

        private val FRIEND_CHECKER = MobCheckerBuilder.sequence(
            NULL_NOT_MONSTER,
            MobCheckers.MONSTER_FRIEND,
            MobCheckers.CLAIMED,
            MobCheckers.PET,
            TOGGLE_PVP_FRIEND,
            IS_PVP_FRIEND,
            MobCheckers.HIT
        )
        
        fun isEntityPvpTeammate(user: LivingEntity?, entity: Entity, spell: ScepterAugment): Boolean{
            if (entity is Monster)
                return user is Monster
            if (user == null) return false
            if (entity === user) return true
            if ((entity as? Tameable)?.owner == user) return true
            if (forcePvpOnAllSpells.get() || spell.getPvpMode()){
                return user.isTeammate(entity)
            }
            return entity is PlayerEntity
        }

        enum class Options{
            NONE{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return true
                }
            },
            NON_BOSS{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return NON_BOSS_HIT_CHECKER.shouldItHit(attacker, victim, args)
                }
            },
            NON_ANIMAL{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return NON_ANIMAL_HIT_CHECKER.shouldItHit(attacker, victim, args)
                }
            },
            NON_VILLAGER{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return NON_VILLAGER_HIT_CHECKER.shouldItHit(attacker, victim, args)
                }
            },
            NON_FRIENDLY{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return NON_ANIMAL_HIT_CHECKER.shouldItHit(attacker, victim, args) && NON_VILLAGER.shouldItHit(attacker, victim, args)
                }
            },
            NON_BOSS_FRIENDLY{
                override fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean {
                    return NON_BOSS_HIT_CHECKER.shouldItHit(attacker, victim, args) && NON_FRIENDLY.shouldItHit(attacker, victim, args)
                }
            };
            abstract fun shouldItHit(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean

            protected val NON_ANIMAL_HIT_CHECKER: ShouldItHitPredicate = MobCheckerBuilder.single(MobCheckers.NOT_FARM_ANIMAL)

            protected val NON_VILLAGER_HIT_CHECKER: ShouldItHitPredicate = MobCheckerBuilder.single(MobCheckers.NOT_VILLAGER)

            protected val NON_BOSS_HIT_CHECKER: ShouldItHitPredicate = MobCheckerBuilder.single(ExcludeTagChecker(RegisterTag.POULTRYMORPH_IGNORES))
        }

        fun shouldItHitBase(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean{
            return shouldItHit(attacker,victim, defaultSecondaryHitCheckerOption.get(), args)
        }

        fun shouldItHit(attacker: LivingEntity?, victim: Entity, options: Options, vararg args: Any?): Boolean{
            return options.shouldItHit(attacker, victim, args) && HIT_CHECKER.shouldItHit(attacker, victim, args)
        }

        fun shouldItHitFriend(attacker: LivingEntity?, victim: Entity, vararg args: Any?): Boolean{
            return FRIEND_CHECKER.shouldItHit(attacker, victim, args)
        }

        //MonsterShouldHit:
        //NOT_MONSTER_FRIEND &&
        //

        @ReadMeText("readme.entities.forcePvpOnAllSpells")
        var forcePvpOnAllSpells = ValidatedBoolean(false)
        var defaultSecondaryHitCheckerOption = ValidatedEnum(Options.NONE,Options::class.java)

        var unhallowed = Unhallowed()
        class Unhallowed: ConfigSection(Header.Builder().space().add("readme.entities.unhallowed_1").build()){
            var baseLifespan = ValidatedInt(2400,180000,20)
            var baseHealth = ValidatedDouble(20.0,100.0,1.0)
            var baseDamage = ValidatedFloat(3.0f,20.0f,0.0f)
        }

        var crystalGolem = CrystalGolem()
        class CrystalGolem: ConfigSection(Header.Builder().space().add("readme.entities.golem_1").build()){
            @ReadMeText("readme.entities.crystalGolem.spellBaseLifespan")
            var spellBaseLifespan = ValidatedInt(5500, Int.MAX_VALUE-120000,20)
            @ReadMeText("readme.entities.crystalGolem.spellPerLvlLifespan")
            var spellPerLvlLifespan = ValidatedInt(500,5000,0)
            @ReadMeText("readme.entities.crystalGolem.guardianLifespan")
            var guardianLifespan = ValidatedInt(900, Int.MAX_VALUE,20)
            var baseHealth = ValidatedDouble(180.0,1024.0,1.0)
            var baseDamage = ValidatedFloat(20.0f,1000f,0f)
        }

        var hamster = Hamster()
        class Hamster: ConfigSection(Header.Builder().space().add("readme.entities.hamster_1").build()){
            @ReadMeText("readme.entities.hamster.baseLifespan")
            var baseLifespan = ValidatedInt(3600,180000,-1)
            var baseHealth = ValidatedDouble(8.0,40.0,1.0)
            @ReadMeText("readme.entities.hamster.baseDamage")
            var baseSummonDamage = ValidatedFloat(1.0f,10.0f,0.0f)
            var baseHamptertimeDamage = ValidatedFloat(2.0f,10.0f,0.0f)
            var perLvlDamage = ValidatedFloat(0.1f,1.0f,0.0f)
            var hamptertimeBaseSpawnCount = ValidatedDouble(10.0,100.0,1.0)
            var hamptertimePerLvlSpawnCount = ValidatedDouble(0.5,5.0,0.0)
        }

        var bonestorm = Bonestorm()
        class Bonestorm: ConfigSection(Header.Builder().space().add("readme.entities.bonestorm_1").build()){
            @ReadMeText("readme.entities.bonestorm.baseLifespan")
            var baseLifespan = ValidatedInt(2160,Int.MAX_VALUE-1000000,20)
            @ReadMeText("readme.entities.bonestorm.perLvlLifespan")
            var perLvlLifespan = ValidatedInt(240,2400,0)
            var baseHealth = ValidatedDouble(24.0,240.0,1.0)
            var baseDamage = ValidatedFloat(4.5f,10.0f,0.0f)
            var perLvlDamage = ValidatedFloat(0.25f,1.0f,0.0f)
        }

        var cholem = Cholem()
        class Cholem: ConfigSection(Header.Builder().space().add("readme.entities.cholem_1").build()){
            var baseLifespan = ValidatedInt(3600,Int.MAX_VALUE-1000000,20)
            var baseHealth = ValidatedDouble(80.0,800.0,1.0)
            var baseArmor = ValidatedDouble(4.0,20.0,1.0)
            var baseDamage = ValidatedFloat(10f,100f,0.0f)
            var enragedDamage = ValidatedFloat(4f,40f,0.0f)
        }

        var chorse = Chorse()
        class Chorse: ConfigSection(Header.Builder().space().add("readme.entities.chorse_1").build()){
            var baseHealth = ValidatedDouble(80.0,800.0,1.0)
            var baseJumpStrength = ValidatedDouble(0.93,2.0,0.0)
            var baseMoveSpeed = ValidatedDouble(0.275,1.0,0.01)
        }

        var sardonyxFragment = SardonyxFragment()
        class SardonyxFragment: ConfigSection(Header.Builder().space().add("readme.entities.sardonyx_fragment_1").build()){
            var baseHealth = ValidatedDouble(60.0,600.0,1.0)
            var baseArmor = ValidatedDouble(8.0,20.0,1.0)
            var baseDamage = ValidatedDouble(9.0,90.0,0.0)
            var enragedDamage = ValidatedDouble(6.0,60.0,0.0)
        }

        var sardonyxElemental = SardonyxElemental()
        class SardonyxElemental: ConfigSection(Header.Builder().space().add("readme.entities.sardonyx_elemental_1").build()){
            var baseHealth = ValidatedDouble(512.0,1024.0,1.0)
            var baseArmor = ValidatedDouble(14.0,50.0,1.0)
            var baseDamage = ValidatedDouble(28.0,280.0,0.0)
            var projectileDamage = ValidatedFloat(20f,200f,0f)
            var fragmentsSpawned = ValidatedInt(3,25,1)
            var devastationBeamDmg = ValidatedFloat(50f,Float.MAX_VALUE,0f)
            var spellActivationCooldown = ValidatedInt(600,6000,100)
            var amountHealedPerSecond = ValidatedFloat(0.2f,5f, 0f)
        }

        override fun generateNewClass(): Entities {
            sardonyxElemental.devastationBeamDmg.validateAndSet(50f)
            return this
        }
    }

    private val resourcesHeader = buildSectionHeader("resources")

    class Resources: ConfigClass(resourcesHeader), OldClass<Resources>{

        fun isEnabled(id: String): Boolean{
            return !disabledResources.contains(id)
        }

        var disabledResources: ValidatedStringList = ValidatedStringList(listOf("optional/iridescent_scepter_imbuing","optional/lustrous_scepter_imbuing"))

        override fun generateNewClass(): Resources {
            return this
        }
    }

    private val hudHeader = buildSectionHeader("hud")

    class Hud: SavableConfigClass("hud_v0.json", AI.MOD_ID, hudHeader), OldClass<Hud>{

        fun getX(width: Int): Int{
            return hudCorner.get().getX(width, hudX.get())
        }

        fun getY(height: Int): Int{
            return hudCorner.get().getY(height, hudY.get())
        }

        var showHud = ValidatedBoolean(true)
        var hudCorner = ValidatedEnum(Corner.TOP_LEFT,Corner::class.java)
        var hudX = ValidatedInt(0,Int.MAX_VALUE, 0)
        var hudY = ValidatedInt(0,Int.MAX_VALUE, 0)
        var spellHudSpacing = ValidatedInt(80,145,30)
        var scrollChangesSpells = ValidatedBoolean(true)
        override fun generateNewClass(): Hud {
            return this
        }

        enum class Corner{
            TOP_LEFT{
                override fun getX(width: Int, x: Int): Int {
                    return x + 6
                }
                override fun getY(height: Int, y: Int): Int {
                    return y + 4
                }
            },
            BOTTOM_LEFT{
                override fun getX(width: Int, x: Int): Int {
                    return x + 6
                }
                override fun getY(height: Int, y: Int): Int {
                    return height - y - 35
                }
            },
            TOP_RIGHT{
                override fun getX(width: Int, x: Int): Int {
                    return width - x - 93
                }
                override fun getY(height: Int, y: Int): Int {
                    return y + 4
                }
            },
            BOTTOM_RIGHT{
                override fun getX(width: Int, x: Int): Int {
                    return width - x - 93
                }
                override fun getY(height: Int, y: Int): Int {
                    return height - y - 35
                }
            },
            BOTTOM_MIDDLE{
                override fun getX(width: Int, x: Int): Int {
                    return width/2 - x
                }
                override fun getY(height: Int, y: Int): Int {
                    return height - y - 35
                }

                override fun validate(x: Int, y: Int, width: Int, height: Int): Boolean {
                    if (x < 0 || y < 0) return false
                    if (x > width/2) return false
                    return y <= height - 35
                }
            };

            abstract fun getX(width: Int, x: Int): Int
            abstract fun getY(height: Int, y: Int): Int

            open fun validate(x: Int, y: Int, width: Int, height: Int): Boolean {
                if (x < 0 || y < 0) return false
                if (x > width - 93) return false
                return y <= height - 35
            }
        }
    }

    /*fun saveHud(){
        SyncedConfigHelperV1.save("hud_v0.json", base = AI.MOD_ID, configClass = hud)
    }*/

    var items: Items = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("items_v6.json","items_v5.json", base = AI.MOD_ID,configClass = { Items() }, previousClass = {Items()})
    var materials: Materials = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("materials_v1.json","materials_v0.json", base = AI.MOD_ID, configClass = {Materials()}, previousClass = {Materials()})
    var blocks: Blocks = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("blocks_v0.json","altars_v4.json", base = AI.MOD_ID, configClass = {Blocks()}, previousClass = {Blocks()})
    var villages: Villages = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("villages_v3.json","villages_v2.json", base = AI.MOD_ID, configClass = {Villages()}, previousClass = {AiConfigOldClasses.VillagesV1()})
    var enchants: Enchants = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("enchantments_v3.json","enchantments_v2.json", base = AI.MOD_ID, configClass = { Enchants() }, previousClass = {AiConfigOldClasses.EnchantmentsV0()})
    var trinkets: Trinkets = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("augments_v4.json","augments_v3.json", base = AI.MOD_ID, configClass = {Trinkets()}, previousClass = {Trinkets()})
    var entities: Entities = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("entities_v6.json","entities_v5.json", base = AI.MOD_ID, configClass = {Entities()}, previousClass = {AiConfigOldClasses.EntitiesV0()})
    var resources: Resources = SyncedConfigHelperV1.readOrCreateAndValidate("resources_v0.json", base = AI.MOD_ID, configClass = {Resources()})
    @NonSync
    var hud: Hud = SyncedConfigHelperV1.readOrCreateAndValidate("hud_v0.json", base = AI.MOD_ID, configClass = {Hud()})

    fun registerClient(){
        ShouldScrollEvent.EVENT.register{_, _ ->
            hud.scrollChangesSpells.get()
        }
    }

    override fun initConfig() {
        super.initConfig()
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this)
    }

    override fun reload(manager: ResourceManager) {
        items = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("items_v6.json","items_v5.json", base = AI.MOD_ID,configClass = { Items() }, previousClass = {Items()})
        materials = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("materials_v1.json","materials_v0.json", base = AI.MOD_ID, configClass = {Materials()}, previousClass = {Materials()})
        blocks = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("blocks_v0.json","altars_v4.json", base = AI.MOD_ID, configClass = {Blocks()}, previousClass = {Blocks()})
        villages = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("villages_v3.json","villages_v2.json", base = AI.MOD_ID, configClass = {Villages()}, previousClass = {AiConfigOldClasses.VillagesV1()})
        enchants = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("enchantments_v3.json","enchantments_v2.json", base = AI.MOD_ID, configClass = { Enchants() }, previousClass = {AiConfigOldClasses.EnchantmentsV0()})
        trinkets = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("augments_v4.json","augments_v3.json", base = AI.MOD_ID, configClass = {Trinkets()}, previousClass = {Trinkets()})
        entities = SyncedConfigHelperV1.readOrCreateUpdatedAndValidate("entities_v6.json","entities_v5.json", base = AI.MOD_ID, configClass = {Entities()}, previousClass = {AiConfigOldClasses.EntitiesV0()})
        resources = SyncedConfigHelperV1.readOrCreateAndValidate("resources_v0.json", base = AI.MOD_ID, configClass = {Resources()})
    }

    override fun getFabricId(): Identifier {
        return Identifier(AI.MOD_ID,"ai_config_reloader")
    }

    private fun buildSectionHeader(name:String): Header{
        return Header.Builder().space().underoverscore("readme.header.$name").add("readme.header.$name.desc").space().build()
    }

}
