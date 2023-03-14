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
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

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
    val MAGIC_MISSILE = MagicMissileAugment(1,1, EquipmentSlot.MAINHAND).also{regEnchant["magic_missile"] = it}
    val ABUNDANCE = AbundanceAugment(1,6,EquipmentSlot.MAINHAND).also{regEnchant["abundance"] = it}
    val BARRIER = BarrierAugment(2,10,EquipmentSlot.MAINHAND).also{regEnchant["barrier"] = it}
    val BEDAZZLE = BedazzleAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["bedazzle"] = it}
    val CLEANSE = CleanseAugment(1,11,EquipmentSlot.MAINHAND).also{regEnchant["cleanse"] = it}
    val COMET_STORM = CometStormAugment(3,9,EquipmentSlot.MAINHAND).also{regEnchant["comet_storm"] = it}
    val CREATE_LAVA = CreateLavaAugment(2,1,Items.LAVA_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_lava"] = it}
    val CREATE_SPONGE = CreateSpongeAugment(1,1,Items.SPONGE,EquipmentSlot.MAINHAND).also{regEnchant["create_sponge"] = it}
    val CREATE_WATER = CreateWaterAugment(1,1,Items.WATER_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_water"] = it}
    val DASH = DashAugment(2,3,EquipmentSlot.MAINHAND).also{regEnchant["dash"] = it}
    val EMPOWERED_SLASH = EmpoweredSlashAugment(2,5, EquipmentSlot.MAINHAND).also{regEnchant["empowered_slash"] = it}
    val EXHAUST = ExhaustAugment(2,6,EquipmentSlot.MAINHAND).also{regEnchant["exhaust"] = it}
    val FANG_BARRAGE = FangBarrageAugment(3,6,EquipmentSlot.MAINHAND).also{regEnchant["fang_barrage"] = it}
    val FANGS = FangsAugment(2,6,EquipmentSlot.MAINHAND).also{regEnchant["fangs"] = it}
    val FIREBALL = FireballAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["fireball"] = it}
    val FLAMEBOLT = FlameboltAugment(1,11,EquipmentSlot.MAINHAND).also{regEnchant["flamebolt"] = it}
    val FLAMEWAVE = FlamewaveAugment(3,11,EquipmentSlot.MAINHAND).also{regEnchant["flamewave"] = it}
    val FORCE_FIELD = ForcefieldAugment(2,6,EquipmentSlot.MAINHAND).also{regEnchant["forcefield"] = it}
    val FORTIFY = FortifyAugment(2,11,EquipmentSlot.MAINHAND).also{regEnchant["fortify"] = it}
    val FREEZING = FreezingAugment(1,6,EquipmentSlot.MAINHAND).also{regEnchant["freezing"] = it}
    val GUSTING = GustingAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["gusting"] = it}
    val HAIL_STORM = HailStormAugment(3,12,EquipmentSlot.MAINHAND).also{regEnchant["hail_storm"] = it}
    val HARD_LIGHT_BRIDGE = HardLightBridgeAugment(2,11,EquipmentSlot.MAINHAND).also{regEnchant["hard_light_bridge"] = it}
    val ICE_SHARD = IceShardAugment(2,6,EquipmentSlot.MAINHAND).also{regEnchant["ice_shard"] = it}
    val ICE_SPIKES = IceSpikesAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["ice_spikes"] = it}
    val INSPIRING_SONG = InspiringSongAugment(2,13,EquipmentSlot.MAINHAND).also{regEnchant["inspiring_song"] = it}
    val LEVITATING_BULLET = LevitatingBulletAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["levitating_bullet"] = it}
    val LIGHTNING_BOLT = LightningBoltAugment(2,11,EquipmentSlot.MAINHAND).also{regEnchant["lightning_bolt"] = it}
    val LIGHTNING_STORM = LightningStormAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["lightning_storm"] = it}
    val MASS_CLEANSE = MassCleanseAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_cleanse"] = it}
    val MASS_EXHAUST = MassExhaustAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["mass_exhaust"] = it}
    val MASS_FORTIFY = MassFortifyAugment(3,9,EquipmentSlot.MAINHAND).also{regEnchant["mass_fortify"] = it}
    val MASS_HEAL = MassHealAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_heal"] = it}
    val MASS_REVIVIFY = MassRevivifyAugment(3,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_revivify"] = it}
    val MEND_EQUIPMENT = MendEquipmentAugment(1,13,EquipmentSlot.MAINHAND).also{regEnchant["mend_equipment"] = it}
    val MINOR_HEAL = MinorHealAugment(1,6,EquipmentSlot.MAINHAND).also{regEnchant["minor_heal"] = it}
    val RECALL = RecallAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["recall"] = it}
    val REGENERATE = RegenerateAugment(1,17,EquipmentSlot.MAINHAND).also{regEnchant["regenerate"] = it}
    val RESONATE = ResonateAugment(3,5, EquipmentSlot.MAINHAND).also{regEnchant["resonate"] = it}
    val SHINE = ShineAugment(1,1, Items.TORCH,EquipmentSlot.MAINHAND).also{regEnchant["shine"] = it}
    val SMITING_BLOW = SmitingBlowAugment(2,5, EquipmentSlot.MAINHAND).also{regEnchant["smiting_blow"] = it}
    val SOUL_MISSILE = SoulMissileAugment(1,21, EquipmentSlot.MAINHAND).also{regEnchant["soul_missile"] = it}
    val SPECTRAL_SLASH = SpectralSlashAugment(1,9, EquipmentSlot.MAINHAND).also{regEnchant["spectral_slash"] = it}
    //val SUMMON_BLAZE = SummonEntityAugment(2,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_blaze"] = it}
    val SUMMON_BOAT = SummonBoatAugment(1,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_boat"] = it}
    val SUMMON_CHICKEN = SummonChickenAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_chicken"] = it}
    val SUMMON_FURY_TOTEM = SummonFuryTotemAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["summon_fury_totem"] = it}
    //val SUMMON_FAMILIAR = SummonFamiliarAugment(2,13,EquipmentSlot.MAINHAND).also{regEnchant["summon_familiar"] = it}
    val SUMMON_GOLEM = SummonGolemAugment(3,5,EquipmentSlot.MAINHAND).also{regEnchant["summon_golem"] = it}
    val SUMMON_GRACE_TOTEM = SummonGraceTotemAugment(2,11,EquipmentSlot.MAINHAND).also{regEnchant["summon_grace_totem"] = it}
    val SUMMON_STRIDER = SummonStriderAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_strider"] = it}
    val SUMMON_WIT_TOTEM = SummonWitTotemAugment(2,9,EquipmentSlot.MAINHAND).also{regEnchant["summon_wit_totem"] = it}
    val SUMMON_ZOMBIE = SummonZombieAugment(2,13,EquipmentSlot.MAINHAND).also{regEnchant["summon_zombie"] = it}
    val SURVEY = SurveyAugment(3,1, EquipmentSlot.MAINHAND).also{regEnchant["survey"] = it}
    val TELEPORT = TeleportAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["teleport"] = it}
    val WITHERING_BOLT = WitheringBoltAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["withering_bolt"] = it}
    val ZAP = ZapAugment(1,11, EquipmentSlot.MAINHAND).also{regEnchant["zap"] = it}

    val DEBUG = DebugAugment(1,1,EquipmentSlot.MAINHAND).also{regEnchant["debug"] = it}

    private fun checkConfig(check: String, enchant: Enchantment){
        regEnchant[check] = enchant
    }
    fun registerAll(){

        for (k in regEnchant.keys){
            val enchant = regEnchant[k]
            val id = Identifier(AI.MOD_ID, k)
            Registry.register(Registries.ENCHANTMENT, id, enchant)
            if (enchant is AbstractConfigDisableEnchantment){
                enchant.updateEnabled()
                if (!enchant.isEnabled()){
                    LOGGER.info("Augment $id is set as disabled in the configs!")
                }
            }
            if (enchant is ScepterAugment){
                AugmentHelper.registerAugmentStat(enchant)
                if (!AugmentHelper.getAugmentEnabled(id.toString())) {
                    LOGGER.info("Augment $id is set as disabled in the configs!")
                }
            }
        }
        regEnchant.clear()
    }


}
