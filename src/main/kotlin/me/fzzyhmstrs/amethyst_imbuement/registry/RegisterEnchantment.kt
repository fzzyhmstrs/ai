@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.LOGGER
import me.fzzyhmstrs.amethyst_imbuement.augment.*
import me.fzzyhmstrs.amethyst_imbuement.enchantment.*
import me.fzzyhmstrs.amethyst_imbuement.spells.*
import me.fzzyhmstrs.fzzy_core.coding_util.AbstractConfigDisableEnchantment
import net.minecraft.enchantment.DamageEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object RegisterEnchantment {

    private fun <T: Enchantment> T.register(name: String): T{
        val id = AI.identity(name)
        val e1 = Registry.register(Registries.ENCHANTMENT,id, this)
        if (e1 is AbstractConfigDisableEnchantment){
            if (!e1.isEnabled()){
                LOGGER.info("Augment $id is set as disabled in the configs!")
            }
        }
        if (e1 is ScepterAugment){
            if (!AugmentHelper.getAugmentEnabled(e1)) {
                LOGGER.info("Augment $id is set as disabled in the configs!")
            }
        }
        return e1
    }

    //vanilla style enchantments
    val HEROIC = DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlot.MAINHAND).register("heroic")
    val WASTING = WastingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).register("wasting")
    val DEADLY_SHOT = DeadlyShotEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).register("deadly_shot")
    val PUNCTURING = PuncturingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).register("puncturing")
    val INSIGHT = InsightEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).register("insight")
    val LIFESTEAL = LifestealEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).register("lifesteal")
    val DECAYED = DecayedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).register("decayed")
    val CONTAMINATED = ContaminatedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).register("contaminated")
    val CLEAVING = CleavingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).register("cleaving")
    val MULTI_JUMP = MultiJumpEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.FEET).register("multi_jump")
    val NIGHT_VISION = NightVisionEnchantment(Enchantment.Rarity.RARE,1, EquipmentSlot.HEAD).register("night_vision")
    val STEADFAST = SteadfastEnchantment(Enchantment.Rarity.UNCOMMON, *AI.slots).register("steadfast")
    val RAIN_OF_THORNS = RainOfThornsEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).register("rain_of_thorns")
    val VEIN_MINER = VeinMinerEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND).register("vein_miner")

    //augments for imbuing
    val ACCURSED = AccursedAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).register("accursed")
    val ANGELIC = AngelicAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND).register("angelic")
    val BEAST_MASTER = BeastMasterAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).register("beast_master")
    val BULWARK = BulwarkAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND).register("bulwark")
    val CONSECRATED = ConsecratedAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.OFFHAND).register("consecrated")
    val CRYSTALLINE = CrystallineAugment(Enchantment.Rarity.VERY_RARE,6, EquipmentSlot.OFFHAND).register("crystalline")
    val DRACONIC_VISION = DraconicVisionAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("draconic_vision")
    val ESCAPE = EscapeAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("escape")
    val FELINE = FelineAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("feline")
    val FRIENDLY = FriendlyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("friendly")
    val GUARDIAN = GuardianAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("guardian")
    val HASTING = HastingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND).register("hasting")
    val HEADHUNTER = HeadhunterAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND).register("headhunter")
    val HEALTHY = HealthyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("healthy")
    val ILLUMINATING = IlluminatingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("illuminating")
    val IMMUNITY = ImmunityAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("immunity")
    val INFLAMMABLE = InflammableAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("inflammable")
    val INVISIBILITY = InvisibilityAugment(Enchantment.Rarity.VERY_RARE,1, *AI.slots).register("invisibility")
    val LEAPING = LeapingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS).register("leaping")
    val LIGHTFOOTED = LightfootedAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).register("lightfooted")
    val LUCKY = LuckyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("lucky")
    val MOONLIT = MoonlitAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots).register("moonlit")
    val RESILIENCE = ResilienceAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots).register("resilience")
    val SHIELDING = ShieldingAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).register("shielding")
    val SLIMY = SlimyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).register("slimy")
    val SOULBINDING = SoulbindingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("soulbinding")
    val SOUL_OF_THE_CONDUIT = SoulOfTheConduitAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.OFFHAND).register("soul_of_the_conduit")
    val SPECTRAL_VISION = SpectralVisionAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.HEAD).register("spectral_vision")
    val SPIKED = SpikedAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).register("spiked")
    val STRENGTH = StrengthAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND).register("strength")
    val STRIDING = StridingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).register("striding")
    val SUNTOUCHED = SuntouchedAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.OFFHAND).register("suntouched")
    val SWIFTNESS = SwiftnessAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS).register("swiftness")
    val UNDYING = UndyingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).register("undying")

    //Scepter Spells
    val MAGIC_MISSILE = MagicMissileAugment().register("magic_missile")
    val ABUNDANCE = AbundanceAugment().register("abundance")
    val ANIMAL_HUSBANDRY = AnimalHusbandryAugment().register("animal_husbandry")
    val BALL_LIGHTNING = BallLightningAugment().register("ball_lightning")
    val BARRIER = BarrierAugment().register("barrier")
    val BEDAZZLE = BedazzleAugment().register("bedazzle")
    val BODY_SWAP = BodySwapAugment().register("body_swap")
    val CHICKENFORM = ChickenformAugment().register("chickenform")
    val CLEANSE = CleanseAugment().register("cleanse")
    val COMET_STORM = CometStormAugment().register("comet_storm")
    val CREATE_HARD_LIGHT = CreateHardLightAugment().register("create_hard_light")
    val CREATE_LAVA = CreateLavaAugment().register("create_lava")
    val CREATE_SPONGE = CreateSpongeAugment().register("create_sponge")
    val CREATE_WATER = CreateWaterAugment().register("create_water")
    val CRIPPLE = CrippleAugment().register("cripple")
    val CURSE = CurseAugment().register("curse")
    val DASH = DashAugment().register("dash")
    val EMPOWERED_SLASH = EmpoweredSlashAugment().register("empowered_slash")
    val EXCAVATE = ExcavateAugment().register("excavate")
    val EXHAUST = ExhaustAugment().register("exhaust")
    val FANG_BARRAGE = FangBarrageAugment().register("fang_barrage")
    val FANGS = FangsAugment().register("fangs")
    val FIREBALL = FireballAugment().register("fireball")
    val FLAMEBOLT = FlameboltAugment().register("flamebolt")
    val FLAMEWAVE = FlamewaveAugment().register("flamewave")
    val FLARE = FlareAugment().register("flare")
    val FORCE_FIELD = ForcefieldAugment().register("forcefield")
    val FORTIFY = FortifyAugment().register("fortify")
    val FREEZING = FreezingAugment().register("freezing")
    val GUSTING = GustingAugment().register("gusting")
    val HAMPTERTIME = HamptertimeAugment().register("hamptertime")
    val HARD_LIGHT_BRIDGE = HardLightBridgeAugment().register("hard_light_bridge")
    val HEALING_WIND = HealingWindAugment().register("healing_wind")
    val ICE_SHARD = IceShardAugment().register("ice_shard")
    val ICE_SPIKES = IceSpikesAugment().register("ice_spikes")
    val INSPIRING_SONG = InspiringSongAugment().register("inspiring_song")
    val LEVITATING_BULLET = LevitatingBulletAugment().register("levitating_bullet")
    val LIGHTNING_BOLT = LightningBoltAugment().register("lightning_bolt")
    val LIGHTNING_STORM = LightningStormAugment().register("lightning_storm")
    val MAGNETIC_AURA = MagneticAuraAugment().register("magnetic_aura")
    val MASS_CLEANSE = MassCleanseAugment().register("mass_cleanse")
    val MASS_EXHAUST = MassExhaustAugment().register("mass_exhaust")
    val MASS_FORTIFY = MassFortifyAugment().register("mass_fortify")
    val MASS_HEAL = MassHealAugment().register("mass_heal")
    val MASS_REVIVIFY = MassRevivifyAugment().register("mass_revivify")
    val MEND_EQUIPMENT = MendEquipmentAugment().register("mend_equipment")
    val MENTAL_CLARITY = MentalClarityAugment().register("mental_clarity")
    val MINOR_HEAL = MinorHealAugment().register("minor_heal")
    val PERSUADE = PersuadeAugment().register("persuade")
    val POULTRYMORPH = PoultrymorphAugment().register("poultrymorph")
    val RECALL = RecallAugment().register("recall")
    val REGENERATE = RegenerateAugment().register("regenerate")
    val RESONATE = ResonateAugment().register("resonate")
    val SHINE = ShineAugment().register("shine")
    val SMITING_BLOW = SmitingBlowAugment().register("smiting_blow")
    val SOUL_MISSILE = SoulMissileAugment().register("soul_missile")
    val SPECTRAL_SLASH = SpectralSlashAugment().register("spectral_slash")
    val SUMMON_BOAT = SummonBoatAugment().register("summon_boat")
    val SUMMON_BONESTORM = SummonBonestormAugment().register("summon_bonestorm")
    val SUMMON_CHICKEN = SummonChickenAugment().register("summon_chicken")
    val SUMMON_FURY_TOTEM = SummonFuryTotemAugment().register("summon_fury_totem")
    val SUMMON_GOLEM = SummonGolemAugment().register("summon_golem")
    val SUMMON_GRACE_TOTEM = SummonGraceTotemAugment().register("summon_grace_totem")
    val SUMMON_HAMSTER = SummonHamsterAugment().register("summon_hamster")
    val SUMMON_STRIDER = SummonStriderAugment().register("summon_strider")
    val SUMMON_ZOMBIE = SummonZombieAugment().register("summon_zombie")
    val SURVEY = SurveyAugment().register("survey")
    val TELEPORT = TeleportAugment().register("teleport")
    val TORRENT_OF_BEAKS = TorrentOfBeaksAugment().register("torrent_of_beaks")
    val WEIGHTLESSNESS = WeightlessnessAugment().register("weightlessness")
    val WINTERS_GRASP = WintersGraspAugment().register("hail_storm") //id kept as hail storm to maintain compat
    val WITHERING_BOLT = WitheringBoltAugment().register("withering_bolt")
    val ZAP = ZapAugment().register("zap")

    val DEBUG = DebugAugment().register("debug")

    val CHAOS_BOLT = ChaosBoltAugment().register("chaos_bolt")

    fun registerAll(){}


}
