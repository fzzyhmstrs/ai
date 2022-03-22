@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.augment.*
import me.fzzyhmstrs.amethyst_imbuement.enchantment.*
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.scepter.*
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import net.minecraft.enchantment.DamageEnchantment
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Suppress("MemberVisibilityCanBePrivate")
object RegisterEnchantment {
    private var regEnchant: MutableMap<String,Enchantment> = mutableMapOf()

    //vanilla style enchantments
    val ATTUNED = AttunedEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{regEnchant["attuned"] = it}
    val HEROIC = DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlot.MAINHAND).also{regEnchant["heroic"] = it}
    val WASTING = WastingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{regEnchant["wasting"] = it}
    val DEADLY_SHOT = DeadlyShotEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{regEnchant["deadly_shot"] = it}
    val PUNCTURING = PuncturingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{regEnchant["puncturing"] = it}
    val INSIGHT = InsightEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{regEnchant["insight"] = it}
    val LIFESTEAL = LifestealEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{regEnchant["lifesteal"] = it}
    val DECAYED = DecayedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).also{regEnchant["decayed"] = it}
    val CONTAMINATED = ContaminatedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND).also{regEnchant["contaminated"] = it}
    val CLEAVING = CleavingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND).also{regEnchant["cleaving"] = it}
    val BULWARK = BulwarkEnchantment(Enchantment.Rarity.COMMON,1, EquipmentSlot.MAINHAND).also{regEnchant["bulwark"] = it}
    val MULTI_JUMP = MultiJumpEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.FEET).also{regEnchant["multi_jump"] = it}
    val NIGHT_VISION = NightVisionEnchantment(Enchantment.Rarity.RARE,1, EquipmentSlot.HEAD).also{regEnchant["night_vision"] = it}
    val STEADFAST = SteadfastEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).also{regEnchant["steadfast"] = it}
    val RAIN_OF_THORNS = RainOfThornsEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND).also{regEnchant["rain_of_thorns"] = it}
    val VEIN_MINER = VeinMinerEnchantment(Enchantment.Rarity.RARE,EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND).also{regEnchant["vein_miner"] = it}

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
    val INVISIBILITY = InvisibilityAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).also{regEnchant["invisibility"] = it}
    val LEAPING = LeapingAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.LEGS).also{regEnchant["leaping"] = it}
    val LIGHTFOOTED = LightfootedAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.FEET).also{regEnchant["lightfooted"] = it}
    val LUCKY = LuckyAugment(Enchantment.Rarity.VERY_RARE,1, EquipmentSlot.OFFHAND).also{regEnchant["lucky"] = it}
    val MOONLIT = MoonlitAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).also{regEnchant["moonlit"] = it}
    val RESILIENCE = ResilienceAugment(Enchantment.Rarity.VERY_RARE,2, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).also{regEnchant["resilience"] = it}
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
    val ABUNDANCE = AbundanceAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["abundance"] = it}
    val BEDAZZLE = BedazzleAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["bedazzle"] = it}
    val CLEANSE = CleanseAugment(1,1,EquipmentSlot.MAINHAND).also{regEnchant["cleanse"] = it}
    val COMET_STORM = CometStormAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["comet_storm"] = it}
    val CREATE_LAVA = CreateLavaAugment(2,1,Items.LAVA_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_lava"] = it}
    val CREATE_SPONGE = CreateSpongeAugment(1,1,Items.SPONGE,EquipmentSlot.MAINHAND).also{regEnchant["create_sponge"] = it}
    val CREATE_WATER = CreateWaterAugment(2,1,Items.WATER_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_water"] = it}
    val DASH = DashAugment(2,3,EquipmentSlot.MAINHAND).also{regEnchant["dash"] = it}
    val FANG_BARRAGE = FangBarrageAugment(3,1,EquipmentSlot.MAINHAND).also{regEnchant["fang_barrage"] = it}
    val FANGS = FangsAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["fangs"] = it}
    val FIREBALL = FireballAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["fireball"] = it}
    val FLAMEBOLT = FlameboltAugment(1,5,EquipmentSlot.MAINHAND).also{regEnchant["flamebolt"] = it}
    val FLAMEWAVE = FlamewaveAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["flamewave"] = it}
    val FORCE_FIELD = ForcefieldAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["force_field"] = it}
    val FORTIFY = FortifyAugment(2,2,EquipmentSlot.MAINHAND).also{regEnchant["fortify"] = it}
    val FREEZING = FreezingAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["freezing"] = it}
    val GUSTING = GustingAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["gusting"] = it}
    val HARD_LIGHT_BRIDGE = HardLightBridgeAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["hard_light_bridge"] = it}
    val LEVITATING_BULLET = LevitatingBulletAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["levitating_bullet"] = it}
    val LIGHTNING_BOLT = LightningBoltAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["lightning_bolt"] = it}
    val LIGHTNING_STORM = LightningStormAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["lightning_storm"] = it}
    val MASS_CLEANSE = MassCleanseAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["mass_cleanse"] = it}
    val MASS_EXHAUST = MassExhaustAugment(3,3,EquipmentSlot.MAINHAND).also{regEnchant["mass_exhaust"] = it}
    val MASS_FORTIFY = MassFortifyAugment(3,2,EquipmentSlot.MAINHAND).also{regEnchant["mass_fortify"] = it}
    val MASS_HEAL = MassHealAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_heal"] = it}
    val MASS_REVIVIFY = MassRevivifyAugment(3,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_revivify"] = it}
    val MEND_EQUIPMENT = MendEquipmentAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["mend_equipment"] = it}
    val MINOR_HEAL = MinorHealAugment(1,5,EquipmentSlot.MAINHAND).also{regEnchant["minor_heal"] = it}
    val REGENERATE = RegenerateAugment(1,5,EquipmentSlot.MAINHAND).also{regEnchant["regenerate"] = it}
    val SHINE = ShineAugment(1,1, Items.TORCH,EquipmentSlot.MAINHAND).also{regEnchant["shine"] = it}
    val SOUL_MISSILE = SoulMissileAugment(1,1, EquipmentSlot.MAINHAND).also{regEnchant["soul_missile"] = it}
    val SPECTRAL_SLASH = SpectralSlashAugment(1,3, EquipmentSlot.MAINHAND).also{regEnchant["spectral_slash"] = it}
    //val SUMMON_BLAZE = SummonEntityAugment(2,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_blaze"] = it}
    val SUMMON_BOAT = SummonBoatAugment(1,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_boat"] = it}
    val SUMMON_CHICKEN = SummonChickenAugment(1,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_chicken"] = it}
    val SUMMON_GOLEM = SummonGolemAugment(3,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_golem"] = it}
    val SUMMON_STRIDER = SummonStriderAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_strider"] = it}
    val SUMMON_ZOMBIE = SummonZombieAugment(2,6,EquipmentSlot.MAINHAND).also{regEnchant["summon_zombie"] = it}
    val TELEPORT = TeleportAugment(2,1,EquipmentSlot.MAINHAND).also{regEnchant["teleport"] = it}
    val WITHERING_BOLT = WitheringBoltAugment(2,5,EquipmentSlot.MAINHAND).also{regEnchant["withering_bolt"] = it}

    fun registerAll(){

        for (k in regEnchant.keys){
            val enchant = regEnchant[k]
            Registry.register(Registry.ENCHANTMENT, Identifier("amethyst_imbuement", k), enchant)
            if (enchant is ScepterAugment){
                enchant.registerAugmentStat()
            }
        }

        regEnchant.clear()
        /*
        Registry.register(Registry.ENCHANTMENT, Identifier("amethyst_imbuement","seeking_missile"), SEEKING_MISSILE)
        */
    }


}