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
    val VEIN_MINER = VienMinerEnchantment(Enchantment.Rarity.UNCOMMON,EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND).also{regEnchant["vein_miner"] = it}

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
    val MAGIC_MISSILE = SummonProjectileAugment(Enchantment.Rarity.VERY_RARE,1,1, EquipmentSlot.MAINHAND).also{regEnchant["magic_missile"] = it}
    val SOUL_MISSILE = SummonProjectileAugment(Enchantment.Rarity.VERY_RARE,1,1, EquipmentSlot.MAINHAND).also{regEnchant["soul_missile"] = it}
    val ABUNDANCE = AbundanceAugment(Enchantment.Rarity.VERY_RARE,1,3,EquipmentSlot.MAINHAND).also{regEnchant["abundance"] = it}
    val BUILDER = BuilderAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["builder"] = it}
    val CHARM = CharmAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["charm"] = it}
    val CLEANSE = CleanseAugment(Enchantment.Rarity.VERY_RARE,1,1,EquipmentSlot.MAINHAND).also{regEnchant["cleanse"] = it}
    val COMET_STORM = CometStormAugment(Enchantment.Rarity.VERY_RARE,3,3,EquipmentSlot.MAINHAND).also{regEnchant["comet_storm"] = it}
    val CREATE_LAVA = PlaceItemAugment(Enchantment.Rarity.VERY_RARE,2,1,Items.LAVA_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_lava"] = it}
    val CREATE_SPONGE = PlaceItemAugment(Enchantment.Rarity.VERY_RARE,1,1,Items.SPONGE,EquipmentSlot.MAINHAND).also{regEnchant["create_sponge"] = it}
    val CREATE_WATER = PlaceItemAugment(Enchantment.Rarity.VERY_RARE,2,1,Items.WATER_BUCKET,EquipmentSlot.MAINHAND).also{regEnchant["create_water"] = it}
    val DASH = DashAugment(Enchantment.Rarity.VERY_RARE,2,3,EquipmentSlot.MAINHAND).also{regEnchant["dash"] = it}
    val FANG_BARRAGE = FangBarrageAugment(Enchantment.Rarity.VERY_RARE,3,1,EquipmentSlot.MAINHAND).also{regEnchant["fang_barrage"] = it}
    val FANGS = FangsAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["fangs"] = it}
    val FIREBALL = FireballAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["fireball"] = it}
    val FLAMEBOLT = FlameboltAugment(Enchantment.Rarity.VERY_RARE,1,5,EquipmentSlot.MAINHAND).also{regEnchant["flamebolt"] = it}
    val FLAMEWAVE = FlameboltAugment(Enchantment.Rarity.VERY_RARE,3,3,EquipmentSlot.MAINHAND).also{regEnchant["flamewave"] = it}
    val FORCE_FIELD = ForcefieldAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["force_field"] = it}
    val FORTIFY = FortifyAugment(Enchantment.Rarity.VERY_RARE,2,2,EquipmentSlot.MAINHAND).also{regEnchant["fortify"] = it}
    val FREEZING = FreezingAugment(Enchantment.Rarity.VERY_RARE,1,3,EquipmentSlot.MAINHAND).also{regEnchant["freezing"] = it}
    val GUSTING = GustingAugment(Enchantment.Rarity.VERY_RARE,1,3,EquipmentSlot.MAINHAND).also{regEnchant["gusting"] = it}
    val LEVITATING_BULLET = LevitatingBulletAugment(Enchantment.Rarity.VERY_RARE,3,3,EquipmentSlot.MAINHAND).also{regEnchant["levitating_bullet"] = it}
    val LIGHTNING_BOLT = LightningBoltAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["lightning_bolt"] = it}
    val LIGHTNING_STORM = LightningStormAugment(Enchantment.Rarity.VERY_RARE,3,3,EquipmentSlot.MAINHAND).also{regEnchant["lightning_storm"] = it}
    val MASS_CLEANSE = MassCleanseAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["mass_cleanse"] = it}
    val MASS_EXHAUST = MassExhaustAugment(Enchantment.Rarity.VERY_RARE,3,3,EquipmentSlot.MAINHAND).also{regEnchant["mass_exhaust"] = it}
    val MASS_FORTIFY = MassFortifyAugment(Enchantment.Rarity.VERY_RARE,3,2,EquipmentSlot.MAINHAND).also{regEnchant["mass_fortify"] = it}
    val MASS_HEAL = MassHealAugment(Enchantment.Rarity.VERY_RARE,2,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_heal"] = it}
    val MASS_REVIVIFY = MassRevivifyAugment(Enchantment.Rarity.VERY_RARE,3,5,EquipmentSlot.MAINHAND).also{regEnchant["mass_revivify"] = it}
    val MINOR_HEAL = MinorSupportAugment(Enchantment.Rarity.VERY_RARE,1,5,EquipmentSlot.MAINHAND).also{regEnchant["minor_heal"] = it}
    val REGENERATE = RegenerateAugment(Enchantment.Rarity.VERY_RARE,1,5,EquipmentSlot.MAINHAND).also{regEnchant["regenerate"] = it}
    val REPAIR = RepairAugment(Enchantment.Rarity.VERY_RARE,1,3,EquipmentSlot.MAINHAND).also{regEnchant["repair"] = it}
    val SHINE = PlaceItemAugment(Enchantment.Rarity.VERY_RARE,1,1, Items.TORCH,EquipmentSlot.MAINHAND).also{regEnchant["shine"] = it}
    val SUMMON_BLAZE = SummonEntityAugment(Enchantment.Rarity.VERY_RARE,2,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_blaze"] = it}
    val SUMMON_BOAT = SummonEntityAugment(Enchantment.Rarity.VERY_RARE,1,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_boat"] = it}
    val SUMMON_CHICKEN = SummonChickenAugment(Enchantment.Rarity.VERY_RARE,1,3,EquipmentSlot.MAINHAND).also{regEnchant["summon_chicken"] = it}
    val SUMMON_GOLEM = SummonGolemAugment(Enchantment.Rarity.VERY_RARE,3,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_golem"] = it}
    val SUMMON_STRIDER = SummonStriderAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["summon_strider"] = it}
    val SUMMON_ZOMBIE = SummonZombieAugment(Enchantment.Rarity.VERY_RARE,2,6,EquipmentSlot.MAINHAND).also{regEnchant["summon_zombie"] = it}
    val TELEPORT = TeleportAugment(Enchantment.Rarity.VERY_RARE,2,1,EquipmentSlot.MAINHAND).also{regEnchant["teleport"] = it}
    val WITHERING_BOLT = WitheringBoltAugment(Enchantment.Rarity.VERY_RARE,2,5,EquipmentSlot.MAINHAND).also{regEnchant["withering_bolt"] = it}

    fun registerAll(){

        for (k in regEnchant.keys){
            Registry.register(Registry.ENCHANTMENT, Identifier("amethyst_imbuement", k), regEnchant[k])
        }
        ScepterObject.registerAugmentStat("magic_missile",SpellType.NULL,15,1,1,0)
        ScepterObject.registerAugmentStat("soul_missile",SpellType.FURY,14,2,1,0, Items.SOUL_SAND)
        ScepterObject.registerAugmentStat("abundance",SpellType.GRACE,10,2,1,0, Items.HAY_BLOCK)
        ScepterObject.registerAugmentStat("builder",SpellType.WIT,5,1,5,1, Items.CRAFTING_TABLE)
        ScepterObject.registerAugmentStat("charm",SpellType.WIT,1800,75,2,1, Items.DIAMOND)
        ScepterObject.registerAugmentStat("cleanse",SpellType.GRACE,600,12,1,0, Items.MILK_BUCKET)
        ScepterObject.registerAugmentStat("comet_storm",SpellType.FURY,400,50,24,2, Items.TNT)
        ScepterObject.registerAugmentStat("create_lava",SpellType.WIT,200,20,1,1, Items.LAVA_BUCKET)
        ScepterObject.registerAugmentStat("create_sponge",SpellType.WIT,30,8,1,1, Items.SPONGE)
        ScepterObject.registerAugmentStat("create_water",SpellType.WIT,40,8,1,0, Items.WATER_BUCKET)
        ScepterObject.registerAugmentStat("dash",SpellType.WIT,36,10,5,1, Items.SUGAR)
        ScepterObject.registerAugmentStat("fang_barrage",SpellType.FURY,100,35,15,2, Items.EMERALD_BLOCK)
        ScepterObject.registerAugmentStat("fangs",SpellType.FURY,30,8,5,1, Items.EMERALD)
        ScepterObject.registerAugmentStat("fireball",SpellType.FURY,40,10,8,1, Items.TNT)
        ScepterObject.registerAugmentStat("flamebolt",SpellType.FURY,18,4,1,0, Items.FIRE_CHARGE)
        ScepterObject.registerAugmentStat("flamewave",SpellType.FURY,2,3,16,2, Items.FIRE_CHARGE)
        ScepterObject.registerAugmentStat("force_field",SpellType.GRACE,600,60,6,1, Items.SHIELD)
        ScepterObject.registerAugmentStat("fortify",SpellType.GRACE,1200,25,1,1, Items.GOLDEN_APPLE)
        ScepterObject.registerAugmentStat("freezing",SpellType.FURY,35,8,5,1, Items.PACKED_ICE)
        ScepterObject.registerAugmentStat("gusting",SpellType.WIT,100,15,3,1, Items.FEATHER)
        ScepterObject.registerAugmentStat("levitating_bullet",SpellType.FURY,60,20,12,2, Items.SHULKER_SHELL)
        ScepterObject.registerAugmentStat("lightning_bolt",SpellType.FURY,50,10,5,1, Items.LIGHTNING_ROD)
        ScepterObject.registerAugmentStat("lightning_storm",SpellType.FURY,400,50,28,2, Items.COPPER_BLOCK)
        ScepterObject.registerAugmentStat("mass_cleanse",SpellType.GRACE,1200,35,7,1, Items.MILK_BUCKET)
        ScepterObject.registerAugmentStat("mass_exhaust",SpellType.GRACE,400,35,11,2, Items.FERMENTED_SPIDER_EYE)
        ScepterObject.registerAugmentStat("mass_fortify",SpellType.GRACE,1800,60,15,2, Items.GOLDEN_APPLE)
        ScepterObject.registerAugmentStat("mass_heal",SpellType.GRACE,240,25,5,1, Items.GLISTERING_MELON_SLICE)
        ScepterObject.registerAugmentStat("mass_revivify",SpellType.GRACE,300,90,25,2, RegisterItem.GOLDEN_HEART)
        ScepterObject.registerAugmentStat("minor_heal",SpellType.GRACE,120,8,1,1, Items.GLISTERING_MELON_SLICE)
        ScepterObject.registerAugmentStat("regenerate",SpellType.GRACE,1200,20,1,0, Items.GHAST_TEAR) //update cooldown once done debugging
        ScepterObject.registerAugmentStat("repair",SpellType.GRACE,14,3,1,0, Items.IRON_INGOT)
        ScepterObject.registerAugmentStat("shine",SpellType.WIT,5,2,1,0, Items.TORCH)
        ScepterObject.registerAugmentStat("summon_blaze",SpellType.WIT,1200,30,8,1, Items.BLAZE_ROD)
        ScepterObject.registerAugmentStat("summon_boat",SpellType.WIT,1200,10,1,0, Items.OAK_BOAT)
        ScepterObject.registerAugmentStat("summon_chicken",SpellType.GRACE,900,10,1,1, Items.EGG)
        ScepterObject.registerAugmentStat("summon_golem",SpellType.WIT,6000,250,25,2, Items.AMETHYST_BLOCK)
        ScepterObject.registerAugmentStat("summon_strider",SpellType.GRACE,1200,10,5,1, Items.SADDLE)
        ScepterObject.registerAugmentStat("summon_zombie",SpellType.WIT,1200,30,5,1, Items.ROTTEN_FLESH)
        ScepterObject.registerAugmentStat("teleport",SpellType.WIT,200,15,10,1, Items.ENDER_PEARL)
        ScepterObject.registerAugmentStat("withering_bolt",SpellType.FURY,28,13,14,1, Items.WITHER_SKELETON_SKULL)

        regEnchant.clear()
        /*
        Registry.register(Registry.ENCHANTMENT, Identifier("amethyst_imbuement","seeking_missile"), SEEKING_MISSILE)
        */
    }


}