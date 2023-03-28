@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.LOGGER
import me.fzzyhmstrs.amethyst_imbuement.augment.*
import me.fzzyhmstrs.amethyst_imbuement.enchantment.*
import me.fzzyhmstrs.amethyst_imbuement.scepter.*
import me.fzzyhmstrs.fzzy_core.coding_util.AbstractConfigDisableEnchantment
import net.minecraft.enchantment.DamageEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterEnchantment {
    private var regEnchant: MutableMap<String,Enchantment> = mutableMapOf()

    //vanilla style enchantments
    val HEROIC = DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlot.MAINHAND).also{ checkConfig("heroic",it)}
    val WASTING = WastingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{ checkConfig("wasting", it)}
    val DEADLY_SHOT = DeadlyShotEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{ checkConfig("deadly_shot", it)}
    val PUNCTURING = PuncturingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{ checkConfig("puncturing", it)}
    val INSIGHT = InsightEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{ checkConfig("insight", it)}
    val LIFESTEAL = LifestealEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{ checkConfig("lifesteal", it)}
    val DECAYED = DecayedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).also{ checkConfig("decayed", it)}
    val CONTAMINATED = ContaminatedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).also{ checkConfig("contaminated", it)}
    val CLEAVING = CleavingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{ checkConfig("cleaving", it)}
    val BULWARK = BulwarkEnchantment(Enchantment.Rarity.COMMON,1, EquipmentSlot.MAINHAND).also{ checkConfig("bulwark", it)}
    val MULTI_JUMP = MultiJumpEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.FEET).also{ checkConfig("multi_jump", it)}
    val NIGHT_VISION = NightVisionEnchantment(Enchantment.Rarity.RARE,1, EquipmentSlot.HEAD).also{ checkConfig("night_vision", it)}
    val STEADFAST = SteadfastEnchantment(Enchantment.Rarity.UNCOMMON, *AI.slots).also{ checkConfig("steadfast", it)}
    val RAIN_OF_THORNS = RainOfThornsEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{ checkConfig("rain_of_thorns", it)}
    val VEIN_MINER = VeinMinerEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND).also{ checkConfig("vein_miner", it)}

    //augments for imbuing
    val ANGELIC = AngelicAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND).also{regEnchant["angelic"] = it}
    val CRYSTALLINE = CrystallineAugment(Enchantment.Rarity.VERY_RARE,6, EquipmentSlot.OFFHAND).also{regEnchant["crystalline"] = it}
    val DRACONIC_VISION = DraconicVisionAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["draconic_vision"] = it}
    val ESCAPE = EscapeAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["escape"] = it}
    val FELINE = FelineAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["feline"] = it}
    val FRIENDLY = FriendlyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["friendly"] = it}
    val GUARDIAN = GuardianAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["guardian"] = it}
    val HASTING = HastingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND).also{regEnchant["hasting"] = it}
    val HEADHUNTER = HeadhunterAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.MAINHAND).also{regEnchant["headhunter"] = it}
    val HEALTHY = HealthyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["healthy"] = it}
    val ILLUMINATING = IlluminatingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["illuminating"] = it}
    val IMMUNITY = ImmunityAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["immunity"] = it}
    val INVISIBILITY = InvisibilityAugment(Enchantment.Rarity.VERY_RARE,1, *AI.slots).also{regEnchant["invisibility"] = it}
    val LEAPING = LeapingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS).also{regEnchant["leaping"] = it}
    val LIGHTFOOTED = LightfootedAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).also{regEnchant["lightfooted"] = it}
    val LUCKY = LuckyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["lucky"] = it}
    val MOONLIT = MoonlitAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots).also{regEnchant["moonlit"] = it}
    val RESILIENCE = ResilienceAugment(Enchantment.Rarity.VERY_RARE,2, *AI.slots).also{regEnchant["resilience"] = it}
    val SHIELDING = ShieldingAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).also{regEnchant["shielding"] = it}
    val SLIMY = SlimyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).also{regEnchant["slimy"] = it}
    val SOULBINDING = SoulbindingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["soulbinding"] = it}
    val SOUL_OF_THE_CONDUIT = SoulOfTheConduitAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.OFFHAND).also{regEnchant["soul_of_the_conduit"] = it}
    val SPECTRAL_VISION = SpectralVisionAugment(Enchantment.Rarity.VERY_RARE,1 , EquipmentSlot.HEAD).also{regEnchant["spectral_vision"] = it}
    val SPIKED = SpikedAugment(Enchantment.Rarity.VERY_RARE,3, EquipmentSlot.OFFHAND).also{regEnchant["spiked"] = it}
    val STRENGTH = StrengthAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.MAINHAND).also{regEnchant["strength"] = it}
    val STRIDING = StridingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).also{regEnchant["striding"] = it}
    val SUNTOUCHED = SuntouchedAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.OFFHAND).also{regEnchant["suntouched"] = it}
    val SWIFTNESS = SwiftnessAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS).also{regEnchant["swiftness"] = it}
    val UNDYING = UndyingAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["undying"] = it}

    //Scepter Spells
    val MAGIC_MISSILE = MagicMissileAugment().also{regEnchant["magic_missile"] = it}
    val ABUNDANCE = AbundanceAugment().also{regEnchant["abundance"] = it}
    val BALL_LIGHTNING = BallLightningAugment().also{regEnchant["ball_lightning"] = it}
    val BARRIER = BarrierAugment().also{regEnchant["barrier"] = it}
    val BEDAZZLE = BedazzleAugment().also{regEnchant["bedazzle"] = it}
    val CHICKENFORM = ChickenformAugment().also{regEnchant["chickenform"] = it}
    val CLEANSE = CleanseAugment().also{regEnchant["cleanse"] = it}
    val COMET_STORM = CometStormAugment().also{regEnchant["comet_storm"] = it}
    val CREATE_HARD_LIGHT = CreateHardLightAugment().also{regEnchant["create_hard_light"] = it}
    val CREATE_LAVA = CreateLavaAugment().also{regEnchant["create_lava"] = it}
    val CREATE_SPONGE = CreateSpongeAugment().also{regEnchant["create_sponge"] = it}
    val CREATE_WATER = CreateWaterAugment().also{regEnchant["create_water"] = it}
    val CURSE = CurseAugment().also{regEnchant["curse"] = it}
    val DASH = DashAugment().also{regEnchant["dash"] = it}
    val EMPOWERED_SLASH = EmpoweredSlashAugment().also{regEnchant["empowered_slash"] = it}
    val EXHAUST = ExhaustAugment().also{regEnchant["exhaust"] = it}
    val FANG_BARRAGE = FangBarrageAugment().also{regEnchant["fang_barrage"] = it}
    val FANGS = FangsAugment().also{regEnchant["fangs"] = it}
    val FIREBALL = FireballAugment().also{regEnchant["fireball"] = it}
    val FLAMEBOLT = FlameboltAugment().also{regEnchant["flamebolt"] = it}
    val FLAMEWAVE = FlamewaveAugment().also{regEnchant["flamewave"] = it}
    val FLARE = FlareAugment().also{regEnchant["flare"] = it}
    val FORCE_FIELD = ForcefieldAugment().also{regEnchant["forcefield"] = it}
    val FORTIFY = FortifyAugment().also{regEnchant["fortify"] = it}
    val FREEZING = FreezingAugment().also{regEnchant["freezing"] = it}
    val GUSTING = GustingAugment().also{regEnchant["gusting"] = it}
    val HAIL_STORM = HailStormAugment().also{regEnchant["hail_storm"] = it}
    val HARD_LIGHT_BRIDGE = HardLightBridgeAugment().also{regEnchant["hard_light_bridge"] = it}
    val ICE_SHARD = IceShardAugment().also{regEnchant["ice_shard"] = it}
    val ICE_SPIKES = IceSpikesAugment().also{regEnchant["ice_spikes"] = it}
    val INSPIRING_SONG = InspiringSongAugment().also{regEnchant["inspiring_song"] = it}
    val LEVITATING_BULLET = LevitatingBulletAugment().also{regEnchant["levitating_bullet"] = it}
    val LIGHTNING_BOLT = LightningBoltAugment().also{regEnchant["lightning_bolt"] = it}
    val LIGHTNING_STORM = LightningStormAugment().also{regEnchant["lightning_storm"] = it}
    val MASS_CLEANSE = MassCleanseAugment().also{regEnchant["mass_cleanse"] = it}
    val MASS_EXHAUST = MassExhaustAugment().also{regEnchant["mass_exhaust"] = it}
    val MASS_FORTIFY = MassFortifyAugment().also{regEnchant["mass_fortify"] = it}
    val MASS_HEAL = MassHealAugment().also{regEnchant["mass_heal"] = it}
    val MASS_REVIVIFY = MassRevivifyAugment().also{regEnchant["mass_revivify"] = it}
    val MEND_EQUIPMENT = MendEquipmentAugment().also{regEnchant["mend_equipment"] = it}
    val MINOR_HEAL = MinorHealAugment().also{regEnchant["minor_heal"] = it}
    val POULTRYMORPH = PoultrymorphAugment().also{regEnchant["poultrymorph"] = it}
    val RECALL = RecallAugment().also{regEnchant["recall"] = it}
    val REGENERATE = RegenerateAugment().also{regEnchant["regenerate"] = it}
    val RESONATE = ResonateAugment().also{regEnchant["resonate"] = it}
    val SHINE = ShineAugment().also{regEnchant["shine"] = it}
    val SMITING_BLOW = SmitingBlowAugment().also{regEnchant["smiting_blow"] = it}
    val SOUL_MISSILE = SoulMissileAugment().also{regEnchant["soul_missile"] = it}
    val SPECTRAL_SLASH = SpectralSlashAugment().also{regEnchant["spectral_slash"] = it}
    //val SUMMON_BLAZE = SummonEntityAugment(2,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_blaze"] = it}
    val SUMMON_BOAT = SummonBoatAugment().also{regEnchant["summon_boat"] = it}
    val SUMMON_CHICKEN = SummonChickenAugment().also{regEnchant["summon_chicken"] = it}
    val SUMMON_FURY_TOTEM = SummonFuryTotemAugment().also{regEnchant["summon_fury_totem"] = it}
    //val SUMMON_FAMILIAR = SummonFamiliarAugment(2,13,EquipmentSlot.MAINHAND).also{regEnchant["summon_familiar"] = it}
    val SUMMON_GOLEM = SummonGolemAugment().also{regEnchant["summon_golem"] = it}
    val SUMMON_GRACE_TOTEM = SummonGraceTotemAugment().also{regEnchant["summon_grace_totem"] = it}
    val SUMMON_STRIDER = SummonStriderAugment().also{regEnchant["summon_strider"] = it}
    val SUMMON_WIT_TOTEM = SummonWitTotemAugment().also{regEnchant["summon_wit_totem"] = it}
    val SUMMON_ZOMBIE = SummonZombieAugment().also{regEnchant["summon_zombie"] = it}
    val SURVEY = SurveyAugment().also{regEnchant["survey"] = it}
    val TELEPORT = TeleportAugment().also{regEnchant["teleport"] = it}
    val TORRENT_OF_BEAKS = TorrentOfBeaksAugment().also{regEnchant["torrent_of_beaks"] = it}
    val WEIGHTLESSNESS = WeightlessnessAugment().also{regEnchant["weightlessness"] = it}
    val WITHERING_BOLT = WitheringBoltAugment().also{regEnchant["withering_bolt"] = it}
    val ZAP = ZapAugment().also{regEnchant["zap"] = it}

    val DEBUG = DebugAugment().also{regEnchant["debug"] = it}

    private fun checkConfig(check: String, enchant: Enchantment){
        regEnchant[check] = enchant
    }
    fun registerAll(){

        for (k in regEnchant.keys){
            val enchant = regEnchant[k]
            val id = Identifier(AI.MOD_ID, k)
            Registry.register(Registry.ENCHANTMENT, id, enchant)
        }
        for (enchant in regEnchant){
            val e = enchant.value
            val id = Identifier(AI.MOD_ID, enchant.key)
            if (e is AbstractConfigDisableEnchantment){
                if (!e.isEnabled()){
                    LOGGER.info("Augment $id is set as disabled in the configs!")
                }
            }
            if (e is ScepterAugment){
                AugmentHelper.registerAugmentStat(e)
                if (!AugmentHelper.getAugmentEnabled(id.toString())) {
                    LOGGER.info("Augment $id is set as disabled in the configs!")
                }
            }
        }
        regEnchant.clear()
    }


}
