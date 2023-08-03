@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

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

    private fun <T: Enchantment> register(e: T, name: String): T{
        val id = AI.identity(name)
        val e1 = Registry.register(Registries.ENCHANTMENT,id, e)
        if (e1 is AbstractConfigDisableEnchantment){
            if (!e1.isEnabled()){
                LOGGER.info("Augment $id is set as disabled in the configs!")
            }
        }
        return e1
    }
    
    //vanilla style enchantments
    val HEROIC = register(DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlot.MAINHAND),"heroic")
    val WASTING = register(WastingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),"wasting")
    val DEADLY_SHOT = register(DeadlyShotEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND),"deadly_shot")
    val PUNCTURING = register(PuncturingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),"puncturing")
    val INSIGHT = register(InsightEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND),"insight")
    val LIFESTEAL = register(LifestealEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),"lifesteal")
    val DECAYED = register(DecayedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND),"decayed")
    val CONTAMINATED = register(ContaminatedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND),"contaminated")
    val CLEAVING = register(CleavingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND),"cleaving")
    val MULTI_JUMP = register(MultiJumpEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.FEET),"multi_jump")
    val NIGHT_VISION = register(NightVisionEnchantment(Enchantment.Rarity.RARE,1, EquipmentSlot.HEAD),"night_vision")
    val STEADFAST = register(SteadfastEnchantment(Enchantment.Rarity.UNCOMMON, *AI.slots),"steadfast")
    val RAIN_OF_THORNS = register(RainOfThornsEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),"rain_of_thorns")
    val VEIN_MINER = register(VeinMinerEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),"vein_miner")

    //augments for imbuing
    val ACCURSED = register(AccursedAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND),"accursed")
    val ANGELIC = register(AngelicAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND),"angelic")
    val BEAST_MASTER = register(BeastMasterAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND),"beast_master")
    val BULWARK = register(BulwarkAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND),"bulwark")
    val CONSECRATED = register(ConsecratedAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.OFFHAND),"consecrated")
    val CRYSTALLINE = register(CrystallineAugment(Enchantment.Rarity.VERY_RARE,6, EquipmentSlot.OFFHAND),"crystalline")
    val DRACONIC_VISION = register(DraconicVisionAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"draconic_vision")
    val ESCAPE = register(EscapeAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"escape")
    val FELINE = register(FelineAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"feline")
    val FRIENDLY = register(FriendlyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"friendly")
    val GUARDIAN = register(GuardianAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"guardian")
    val HASTING = register(HastingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND),"hasting")
    val HEADHUNTER = register(HeadhunterAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND),"headhunter")
    val HEALTHY = register(HealthyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"healthy")
    val ILLUMINATING = register(IlluminatingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"illuminating")
    val IMMUNITY = register(ImmunityAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"immunity")
    val INFLAMMABLE = register(InflammableAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"inflammable")
    val INVISIBILITY = register(InvisibilityAugment(Enchantment.Rarity.VERY_RARE,1, *AI.slots),"invisibility")
    val LEAPING = register(LeapingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS),"leaping")
    val LIGHTFOOTED = register(LightfootedAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET),"lightfooted")
    val LUCKY = register(LuckyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"lucky")
    val MOONLIT = register(MoonlitAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots),"moonlit")
    val RESILIENCE = register(ResilienceAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots),"resilience")
    val SHIELDING = register(ShieldingAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND),"shielding")
    val SLIMY = register(SlimyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET),"slimy")
    val SOULBINDING = register(SoulbindingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"soulbinding")
    val SOUL_OF_THE_CONDUIT = register(SoulOfTheConduitAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.OFFHAND),"soul_of_the_conduit")
    val SPECTRAL_VISION = register(SpectralVisionAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.HEAD),"spectral_vision")
    val SPIKED = register(SpikedAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND),"spiked")
    val STRENGTH = register(StrengthAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND),"strength")
    val STRIDING = register(StridingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET),"striding")
    val SUNTOUCHED = register(SuntouchedAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.OFFHAND),"suntouched")
    val SWIFTNESS = register(SwiftnessAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS),"swiftness")
    val UNDYING = register(UndyingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND),"undying")

    //Scepter Spells
    val MAGIC_MISSILE = register(MagicMissileAugment(),"magic_missile")
    val ABUNDANCE = register(AbundanceAugment(),"abundance")
    val ANIMAL_HUSBANDRY = register(AnimalHusbandryAugment(),"animal_husbandry")
    val BALL_LIGHTNING = register(BallLightningAugment(),"ball_lightning")
    val BARRIER = register(BarrierAugment(),"barrier")
    val BEDAZZLE = register(BedazzleAugment(),"bedazzle")
    val BODY_SWAP = register(BodySwapAugment(),"body_swap")
    val CHICKENFORM = register(ChickenformAugment(),"chickenform")
    val CLEANSE = register(CleanseAugment(),"cleanse")
    val COMET_STORM = register(CometStormAugment(),"comet_storm")
    val CREATE_HARD_LIGHT = register(CreateHardLightAugment(),"create_hard_light")
    val CREATE_LAVA = register(CreateLavaAugment(),"create_lava")
    val CREATE_SPONGE = register(CreateSpongeAugment(),"create_sponge")
    val CREATE_WATER = register(CreateWaterAugment(),"create_water")
    val CRIPPLE = register(CrippleAugment(),"cripple")
    val CURSE = register(CurseAugment(),"curse")
    val DASH = register(DashAugment(),"dash")
    val EMPOWERED_SLASH = register(EmpoweredSlashAugment(),"empowered_slash")
    val EXCAVATE = register(ExcavateAugment(),"excavate")
    val EXHAUST = register(ExhaustAugment(),"exhaust")
    val FANG_BARRAGE = register(FangBarrageAugment(),"fang_barrage")
    val FANGS = register(FangsAugment(),"fangs")
    val FIREBALL = register(FireballAugment(),"fireball")
    val FLAMEBOLT = register(FlameboltAugment(),"flamebolt")
    val FLAMEWAVE = register(FlamewaveAugment(),"flamewave")
    val FLARE = register(FlareAugment(),"flare")
    val FORCE_FIELD = register(ForcefieldAugment(),"forcefield")
    val FORTIFY = register(FortifyAugment(),"fortify")
    val FROSTBOLT = register(FrostboltAugment(),"freezing")
    val GUSTING = register(GustingAugment(),"gusting")
    val HAMPTERTIME = register(HamptertimeAugment(),"hamptertime")
    val HARD_LIGHT_BRIDGE = register(HardLightBridgeAugment(),"hard_light_bridge")
    val HEALING_WIND = register(HealingWindAugment(),"healing_wind")
    val ICE_SHARD = register(IceShardAugment(),"ice_shard")
    val ICE_SPIKES = register(IceSpikesAugment(),"ice_spikes")
    val INSPIRING_SONG = register(InspiringSongAugment(),"inspiring_song")
    val LEVITATING_BULLET = register(LevitatingBulletAugment(),"levitating_bullet")
    val LIGHTNING_BOLT = register(LightningBoltAugment(),"lightning_bolt")
    val LIGHTNING_STORM = register(LightningStormAugment(),"lightning_storm")
    val MAGNETIC_AURA = register(MagneticAuraAugment(),"magnetic_aura")
    val MASS_CLEANSE = register(MassCleanseAugment(),"mass_cleanse")
    val MASS_EXHAUST = register(MassExhaustAugment(),"mass_exhaust")
    val MASS_FORTIFY = register(MassFortifyAugment(),"mass_fortify")
    val MASS_HEAL = register(MassHealAugment(),"mass_heal")
    val MASS_REVIVIFY = register(MassRevivifyAugment(),"mass_revivify")
    val MEND_EQUIPMENT = register(MendEquipmentAugment(),"mend_equipment")
    val MENTAL_CLARITY = register(MentalClarityAugment(),"mental_clarity")
    val MINOR_HEAL = register(MinorHealAugment(),"minor_heal")
    val PERSUADE = register(PersuadeAugment(),"persuade")
    val POULTRYMORPH = register(PoultrymorphAugment(),"poultrymorph")
    val RECALL = register(RecallAugment(),"recall")
    val REGENERATE = register(RegenerateAugment(),"regenerate")
    val RESONATE = register(ResonateAugment(),"resonate")
    val SHINE = register(ShineAugment(),"shine")
    val SMITING_BLOW = register(SmitingBlowAugment(),"smiting_blow")
    val SOUL_MISSILE = register(SoulMissileAugment(),"soul_missile")
    val SPECTRAL_SLASH = register(SpectralSlashAugment(),"spectral_slash")
    val SUMMON_SEAHORSE = register(SummonSeahorseAugment(),"summon_seahorse")
    val SUMMON_BONESTORM = register(SummonBonestormAugment(),"summon_bonestorm")
    val SUMMON_CHICKEN = register(SummonChickenAugment(),"summon_chicken")
    val SUMMON_FURY_TOTEM = register(SummonFuryTotemAugment(),"summon_fury_totem")
    val SUMMON_GOLEM = register(SummonGolemAugment(),"summon_golem")
    val SUMMON_GRACE_TOTEM = register(SummonGraceTotemAugment(),"summon_grace_totem")
    val SUMMON_HAMSTER = register(SummonHamsterAugment(),"summon_hamster")
    val SUMMON_ZOMBIE = register(SummonZombieAugment(),"summon_zombie")
    val SURVEY = register(SurveyAugment(),"survey")
    val TELEPORT = register(TeleportAugment(),"teleport")
    val TORRENT_OF_BEAKS = register(TorrentOfBeaksAugment(),"torrent_of_beaks")
    val WEIGHTLESSNESS = register(WeightlessnessAugment(),"weightlessness")
    val WINTERS_GRASP = register(WintersGraspAugment(),"winters_grasp") //id kept as hail storm to maintain compat
    val WITHERING_BOLT = register(WitheringBoltAugment(),"withering_bolt")
    val ZAP = register(ZapAugment(),"zap")

    val DEBUG = register(DebugAugment(),"debug")

    fun registerAll(){}
}
