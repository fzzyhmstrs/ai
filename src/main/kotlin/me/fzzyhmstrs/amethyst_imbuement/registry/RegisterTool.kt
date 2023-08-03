@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings.AiItemGroup
import me.fzzyhmstrs.amethyst_imbuement.item.custom.*
import me.fzzyhmstrs.amethyst_imbuement.item.promise.GemOfPromiseItem
import me.fzzyhmstrs.amethyst_imbuement.item.promise.IgnitedGemItem
import me.fzzyhmstrs.amethyst_imbuement.item.scepter.*
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.*
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity
import java.util.*

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterTool {

    internal val regTool: MutableList<Item> = mutableListOf()

    private fun <T: Item> register(item: T, name: String): T{
        if (item is IgnitedGemItem){
            GemOfPromiseItem.register(item)
        }
        regTool.add(item)
        return Registry.register(Registries.ITEM,AI.identity(name), item)
    }

    //tool and weapon items
    val FZZYHAMMER = register(FzzyhammerItem(FabricItemSettings().rarity(Rarity.EPIC)),"fzzyhammer")
    val GLISTERING_TRIDENT = register(GlisteringTridentItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(550).rarity(Rarity.RARE)),"glistering_trident")
    val SNIPER_BOW = register(SniperBowItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(500).rarity(Rarity.RARE)),"sniper_bow")
    val GARNET_SWORD = register(SwordItem(AiConfig.materials.tools.garnet,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_sword")
    val GARNET_SHOVEL = register(ShovelItem(AiConfig.materials.tools.garnet,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_shovel")
    val GARNET_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.garnet,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_pickaxe")
    val GARNET_AXE = register(AxeItem(AiConfig.materials.tools.garnet,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_axe")
    val GARNET_HOE = register(HoeItem(AiConfig.materials.tools.garnet,-3,0.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_hoe")
    val GARNET_HORSE_ARMOR = register(HorseArmorItem(12,"garnet",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"garnet_horse_armor")
    val GLOWING_BLADE = register(CustomSwordItem(AiConfig.materials.tools.glowing,3 ,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_blade")
    val GLOWING_SPADE = register(CustomShovelItem(AiConfig.materials.tools.glowing,1.5f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_spade")
    val GLOWING_PICK = register(CustomPickaxeItem(AiConfig.materials.tools.glowing,1 ,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_pick")
    val GLOWING_AXE = register(CustomAxeItem(AiConfig.materials.tools.glowing,5.0f ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_axe")
    val GLOWING_HOE = register(CustomHoeItem(AiConfig.materials.tools.glowing,-3 ,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_hoe")
    val GLOWING_HORSE_ARMOR = register(FlavorHorseArmorItem(14,"glowing",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"glowing_horse_armor")
    val STEEL_AXE = register(AxeItem(AiConfig.materials.tools.steel,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_axe")
    val STEEL_HOE = register(CustomHoeItem(AiConfig.materials.tools.steel,-3,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_hoe")
    val STEEL_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.steel,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_pickaxe")
    val STEEL_SHOVEL = register(ShovelItem(AiConfig.materials.tools.steel,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_shovel")
    val STEEL_SWORD = register(SwordItem(AiConfig.materials.tools.steel,3,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_sword")
    val STEEL_HORSE_ARMOR = register(HorseArmorItem(9,"steel",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"steel_horse_armor")
    val AMETRINE_AXE = register(AxeItem(AiConfig.materials.tools.ametrine,5.0f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_axe")
    val AMETRINE_HOE = register(CustomHoeItem(AiConfig.materials.tools.ametrine,-3,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_hoe")
    val AMETRINE_PICKAXE = register(PickaxeItem(AiConfig.materials.tools.ametrine,1,-2.8f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_pickaxe")
    val AMETRINE_SHOVEL = register(ShovelItem(AiConfig.materials.tools.ametrine,1.5f,-3.0f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_shovel")
    val AMETRINE_SWORD = register(SwordItem(AiConfig.materials.tools.ametrine,3,-2.4f,AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_sword")
    val AMETRINE_HORSE_ARMOR = register(HorseArmorItem(15,"ametrine",AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"ametrine_horse_armor")

    //trinket and books
    val COPPER_RING = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_ring")
    val COPPER_HEADBAND = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_headband")
    val COPPER_AMULET = register(CopperJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"copper_amulet")
    val COPPER_WARD = register(CopperWardItem(RegisterAttribute.SHIELDING,
        EntityAttributeModifier(UUID.fromString("c66fd31a-ce6e-11ed-afa1-0242ac120002"),"ward_modifier",0.025,EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(336)),"copper_ward")
    val STEEL_AMULET = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_amulet")
    val STEEL_HEADBAND = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_headband")
    val STEEL_RING = register(SteelJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1)),"steel_ring")
    val STEEL_WARD = register(SteelWardItem(EntityAttributes.GENERIC_ARMOR,
        EntityAttributeModifier(UUID.fromString("1f6875e4-d167-11ed-afa1-0242ac120002"),"steel_ward_modifier",1.2, EntityAttributeModifier.Operation.ADDITION),
        AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(424)),"steel_ward")
    val IMBUED_RING = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_ring")
    val IMBUED_HEADBAND = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_headband")
    val IMBUED_AMULET = register(ImbuedJewelryItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.imbuedJewelryDurability.get())),"imbued_amulet")
    val IMBUED_WARD = register(ImbuedWardItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.totemOfAmethystDurability.get())),"imbued_ward")
    val TOTEM_OF_AMETHYST = register(TotemItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxDamage(AiConfig.items.manaItems.totemOfAmethystDurability.get()).rarity(Rarity.UNCOMMON)),"totem_of_amethyst")
    val SPELLCASTERS_FOCUS = register(SpellcastersFocusItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).rarity(Rarity.UNCOMMON)),"spellcasters_focus")
    val WITCHES_ORB = register(WitchesOrbItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT).maxCount(1).rarity(Rarity.RARE)).withGlint(),"witches_orb")

    //Basic scepters
    val OPALINE_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.COMMON)),"opaline_scepter")
    val IRIDESCENT_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON)),"iridescent_scepter")
    val LUSTROUS_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier3Scepter, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE)),"lustrous_scepter")
    val DEBUG_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier3Scepter, AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.DEBUG))
        .withModifiers(listOf(ModifierRegistry.MODIFIER_DEBUG,ModifierRegistry.MODIFIER_DEBUG_2,ModifierRegistry.MODIFIER_DEBUG_3)),"debug_scepter")
        
    // scepter upgrade scepters
    val FURIOUS_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.FURIOUS))
        ,"furious_scepter")
    val WITTY_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.WITTY))
        ,"witty_scepter")
    val GRACEFUL_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.GRACEFUL))
        ,"graceful_scepter")
    val DANGEROUS_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.DANGEROUS, RegisterModifier.DANGEROUS_PACT))
        ,"dangerous_scepter")
    val SKILLFUL_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(RegisterModifier.SKILLFUL))
        ,"skillful_scepter")
    val ENDURING_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier1Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.UNCOMMON))
        .withModifiers(listOf(ModifierRegistry.LESSER_ENDURING))
        ,"enduring_scepter")
    val BLAZING_SCEPTER = register(ParticleScepterItem(ParticleTypes.SMOKE,10,AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FIREBALL))
        .withModifiers(listOf(RegisterModifier.FIRE_ASPECT))
        ,"blazing_scepter")
    val SPARKING_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.LIGHTNING_BOLT))
        .withModifiers(listOf(RegisterModifier.LIGHTNING_ASPECT))
        ,"sparking_scepter")
    val FROSTED_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.ICE_SPIKES))
        .withModifiers(listOf(RegisterModifier.ICE_ASPECT))
        ,"frosted_scepter")
    val SCEPTER_OF_BLADES = register(SpellbladeItem(AiConfig.materials.scepters.blades,3,-2.7f,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withModifiers(listOf(RegisterModifier.BLADE_ASPECT))
        ,"scepter_of_blades")
    val CORRUPTED_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SOUL_MISSILE))
        .withModifiers(listOf(RegisterModifier.NECROTIC))
        ,"corrupted_scepter")
    val SCEPTER_OF_AGONIES = register(SpellbladeItem(AiConfig.materials.scepters.tier2Scepter,3,-3f,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.CRIPPLE))
        .withModifiers(listOf(ModifierRegistry.ENDURING, ModifierRegistry.LESSER_REACH))
        ,"scepter_of_agonies")  
    val SCEPTER_OF_INSIGHT = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.MENTAL_CLARITY))
        .withModifiers(listOf(RegisterModifier.INSIGHTFUL, ModifierRegistry.LESSER_REACH))
        ,"scepter_of_insight")
    val SCEPTER_OF_SUMMONING = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_ZOMBIE))
        .withModifiers(listOf(RegisterModifier.SUMMONERS_ASPECT))
        ,"scepter_of_summoning")
    val PERSUASIVE_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.PERSUADE))
        .withModifiers(listOf(RegisterModifier.WITTY, ModifierRegistry.LESSER_ATTUNED))
        ,"persuasive_scepter")
    val TRAVELERS_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.SUMMON_SEAHORSE))
        .withModifiers(listOf(RegisterModifier.TRAVELER))
        ,"travelers_scepter")
    val SCEPTER_OF_RECALL = register(ParticleScepterItem(ParticleTypes.PORTAL,10,AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.RECALL))
        .withModifiers(listOf(ModifierRegistry.GREATER_REACH))
        ,"scepter_of_recall")
    val BUILDERS_SCEPTER = register(BuilderScepterItem(AiConfig.materials.scepters.builder,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.HARD_LIGHT_BRIDGE))
        .withModifiers(listOf(RegisterModifier.BUILDERS_ASPECT, ModifierRegistry.LESSER_REACH))
        ,"builders_scepter")
    val SCEPTER_OF_THE_VANGUARD = register(SpellbladeItem(AiConfig.materials.scepters.vanguard,3,-3f,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BARRIER))
        .withModifiers(listOf(RegisterModifier.SMITING, RegisterModifier.GRACEFUL))
        ,"scepter_of_the_vanguard")
    val SCEPTER_OF_THE_PALADIN = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.FORTIFY))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, ModifierRegistry.LESSER_ENDURING))
        ,"scepter_of_the_paladin")
    val SCEPTER_OF_HARVESTS = register(ScepterOfHarvestsItem(AiConfig.materials.scepters.harvests,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.ANIMAL_HUSBANDRY))
        .withModifiers(listOf(ModifierRegistry.REACH, ModifierRegistry.LESSER_THRIFTY))
        ,"scepter_of_harvests")
    val SCEPTER_OF_THE_PACIFIST = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.BEDAZZLE))
        .withModifiers(listOf(RegisterModifier.HEALERS_GRACE, RegisterModifier.HEALERS_PACT))
        ,"scepter_of_the_pacifist")
    val CLERICS_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.MINOR_HEAL))
        .withModifiers(listOf(RegisterModifier.HEALING))
        ,"clerics_scepter")
    val BARDIC_SCEPTER = register(ScepterItem(AiConfig.materials.scepters.tier2Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.RARE))
        .withAugments(listOf(RegisterEnchantment.INSPIRING_SONG))
        .withModifiers(listOf(ModifierRegistry.LESSER_ENDURING, ModifierRegistry.ATTUNED))
        ,"bardic_scepter")
    val EQUINOX = register(EquinoxScepterItem(AiConfig.materials.scepters.tier3Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.ELEMENTAL, RegisterModifier.FURIOUS))
        ,"equinox")
    val LETHALITY = register(LethalityScepterItem(AiConfig.materials.scepters.lethality,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.DANGEROUS,RegisterModifier.DANGEROUS_PACT))
        ,"lethality")
    val RESONANCE = register(ScepterItem(AiConfig.materials.scepters.tier3Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.RESONATE))
        .withModifiers(listOf(RegisterModifier.ECHOING,RegisterModifier.SKILLFUL))
        ,"resonance")
    val SOJOURN = register(SojournScepterItem(AiConfig.materials.scepters.tier3Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.SURVEY))
        .withModifiers(listOf(RegisterModifier.WITTY,RegisterModifier.TRAVELER))
        ,"sojourn")
    val AEGIS = register(SpellbladeItem(AiConfig.materials.scepters.tier3Scepter,5,-3f,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.PROTECTIVE, RegisterModifier.SMITING))
        ,"aegis")
    val REDEMPTION = register(ScepterItem(AiConfig.materials.scepters.tier3Scepter,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withModifiers(listOf(RegisterModifier.HEALERS_PACT,RegisterModifier.HEALERS_GRACE, ModifierRegistry.LESSER_ENDURING))
        ,"redemption")

    val A_SCEPTER_SO_FOWL = register(ScepterItem(AiConfig.materials.scepters.fowl,AiItemSettings().aiGroup(AiItemGroup.SCEPTER).rarity(Rarity.EPIC))
        .withAugments(listOf(RegisterEnchantment.TORRENT_OF_BEAKS,RegisterEnchantment.CHICKENFORM,RegisterEnchantment.POULTRYMORPH))
        .withModifiers(listOf(RegisterModifier.FOWL))
        ,"a_scepter_so_fowl")


    // Spell scrolls
    val EMPTY_SPELL_SCROLL = register(CustomFlavorItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"empty_spell_scroll")
    val SPELL_SCROLL = register(SpellScrollItem(AiItemSettings().aiGroup(AiItemGroup.EQUIPMENT)),"spell_scroll")

    ///////////////////////////

    fun registerAll() {
    }
}
