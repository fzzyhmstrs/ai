package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ComplexAugmentModifier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierConsumers
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import net.minecraft.util.Identifier

object RegisterModifier {

    private fun <T: AugmentModifier> register(modifier: T, weight: Int = 0): T{
        if (modifier is ComplexAugmentModifier && weight > 0)
            me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry.registerWithRolling(modifier, weight)
        else
            ModifierRegistry.register(modifier)
        return modifier
    }
    
    //Modifiers unique to the spellcasters focus and other non-scepter things that might use rolling
    val SAVANT_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"savant_aspect"), rollToll = 7).withXpMod(SpellType.FURY,1).withXpMod(SpellType.GRACE,1).withXpMod(SpellType.WIT,1) as ComplexAugmentModifier, 1 )
    val FULGUROUS = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"fulgurous"), levelModifier = 3, cooldownModifier = -40.0, rollToll = 7).withRange(0.0,0.0,50.0).withAmplifier(2).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE), 1)
    val DISARMING = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"disarming")).withConsumer(ModifierConsumers.DISARMING_CONSUMER), 4)
    val TESTAMENT_TO_POWER = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"testament_to_power"), levelModifier = 1, cooldownModifier = 35.0, rollToll = 8).withDuration(0,0,-10).withAmplifier(6).withConsumer(ModifierConsumers.HEALING_CONSUMER), 1)
    val SPELL_FRENZIED = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"spell_frenzied"), cooldownModifier = -50.0,manaCostModifier = 25.0).withDamage(0f,0f,-15f), 5)
    val BOLT_SPECIALIST = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"bolt_specialist"), cooldownModifier = -10.0, rollToll = 6).withDamage(1f).withConsumer(ModifierConsumers.BOLT_CONSUMER).withSpellToAffect(ModifierPredicates.BOLT_PREDICATE), 1)
    val BOLSTERING = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"bolstering"), manaCostModifier = 15.0).withDuration(0,0,35).withConsumer(ModifierConsumers.BOLSTERING_CONSUMER), 3)
    val BOUNDLESS = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"boundless"), cooldownModifier = 10.0, rollToll = 6).withRange(1.0,0.0,100.0), 2)
    val FOWL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"fowl")).withConsumer(ModifierConsumers.FOWL_CONSUMER), 1)

    //custom modifiers for witches orb
    val INNER_FIRE = register(AugmentModifier(Identifier(AI.MOD_ID,"inner_fire")).withAmplifier(2,0,10).withDuration(0,0,25), 0)
    val BLESSED = register(AugmentModifier(Identifier(AI.MOD_ID,"blessed")).withRange(0.0,0.0,10.0).withConsumer(ModifierConsumers.BLESSED_CONSUMER), 0)
    val DYNAMO = register(AugmentModifier(Identifier(AI.MOD_ID,"dynamo"), cooldownModifier = -15.0, manaCostModifier = -15.0), 0)

    //Soulwoven set
    val ENSOULED_XP = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_xp")).withConsumer(ModifierConsumers.SOUL_XP_CONSUMER).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_REGEN = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_regen")).withConsumer(ModifierConsumers.SOUL_REGEN_CONSUMER).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_SPEED = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_speed")).withConsumer(ModifierConsumers.SOUL_SPEED_CONSUMER).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_ABSORPTION = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_absorption")).withConsumer(ModifierConsumers.SOUL_ABSORPTION_CONSUMER).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_ARMOR = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_armor")).withConsumer(ModifierConsumers.SOUL_ARMOR_CONSUMER).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_COOLDOWN = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_cooldown"), cooldownModifier = -20.0).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_LEVEL = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_level"), levelModifier = 1).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_AMPLIFIER = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_amplifier")).withAmplifier(2).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    val ENSOULED_SPELL_XP = register(AugmentModifier(Identifier(AI.MOD_ID,"ensouled_spell_xp")).withXpMod(SpellType.FURY,1).withXpMod(SpellType.GRACE,1).withXpMod(SpellType.WIT,1).withSpellToAffect(ModifierPredicates.SOUL_PREDICATE), 0)
    internal val ensouledModifiers: List<AugmentModifier> = listOf(ENSOULED_XP, ENSOULED_REGEN, ENSOULED_SPEED, ENSOULED_ABSORPTION, ENSOULED_ARMOR, ENSOULED_COOLDOWN, ENSOULED_LEVEL, ENSOULED_AMPLIFIER, ENSOULED_SPELL_XP)

    val ENRAGED = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"enraged"), levelModifier = 1).withDamage(0.4F).withXpMod(SpellType.FURY,2).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE), 2)
    val FURIOUS = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"furious")).withDamage(0.2F).withXpMod(SpellType.FURY,1).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).withDescendant(ENRAGED), 4)
    val GENIUS = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"genius"), cooldownModifier = -10.0).withXpMod(SpellType.WIT,2).withRange(0.6,0.0,10.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE), 2)
    val WITTY = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"witty")).withXpMod(SpellType.WIT,1).withRange(0.25,0.0,5.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).withDescendant(GENIUS), 4)
    val ELEGANT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"elegant"), cooldownModifier = -10.0).withXpMod(SpellType.GRACE,2).withDuration(0,0,20).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE), 2)
    val GRACEFUL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"graceful")).withXpMod(SpellType.GRACE,1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).withDescendant(ELEGANT), 4)
    val NECROTIC = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"necrotic")).withConsumer(ModifierConsumers.NECROTIC_CONSUMER), 3)
    val SAINTS_PACT = register(AugmentModifier(Identifier(AI.MOD_ID,"saints_pact"), cooldownModifier = 90.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE), 0)
    val HEALERS_PACT = register(AugmentModifier(Identifier(AI.MOD_ID,"healers_pact"), cooldownModifier = 35.0, manaCostModifier = 35.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE).withDescendant(SAINTS_PACT), 0)
    val SAINTS_GRACE = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"saints_grace")).withAmplifier(3).withDuration(durationPercent = 100).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE), 1)
    val HEALERS_GRACE = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"healers_grace")).withAmplifier(1).withDuration(durationPercent = 50).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE).withDescendant(SAINTS_GRACE), 3)
    val LETHAL_PACT = register(AugmentModifier(Identifier(AI.MOD_ID,"pact_of_lethality"), cooldownModifier = 65.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE), 0)
    val DANGEROUS_PACT= register(AugmentModifier(Identifier(AI.MOD_ID,"pact_of_danger"), cooldownModifier = 25.0, manaCostModifier = 25.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE).withDescendant(LETHAL_PACT), 0)
    val HEALING = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"healing")).withConsumer(ModifierConsumers.HEALING_CONSUMER), 5)
    val WARRIOR_OF_LIGHT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"warrior_of_light"), cooldownModifier = -10.0).withConsumer(ModifierConsumers.WARRIORS_CONSUMER), 2)
    val SMITING = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"smiting")).withConsumer(ModifierConsumers.SMITING_CONSUMER).withDescendant(WARRIOR_OF_LIGHT), 5)
    val INSIGHTFUL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"insightful")).withConsumer(ModifierConsumers.INSIGHTFUL_CONSUMER), 6)
    val LETHAL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"lethal")).withDamage(0.5F,0.0F,35.0F), 1)
    val DANGEROUS = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"dangerous")).withDamage(0.0F,0.0F,15.0F).withDescendant(LETHAL), 2)
    val ECHOING = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"echoing"), cooldownModifier = 10.0).withConsumer(ModifierConsumers.ECHOING_CONSUMER), 1)
    val MASTERFUL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"masterful"), levelModifier = 2), 2)
    val SKILLFUL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"skillful"), levelModifier = 1).withDescendant(MASTERFUL), 4)
    val CHAMPION_OF_THE_MEEK = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"champion_of_the_meek")).withDuration(durationPercent = 15).withConsumer(ModifierConsumers.CHAMPIONS_CONSUMER), 2)
    val PROTECTIVE = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"protective")).withConsumer(ModifierConsumers.PROTECTIVE_CONSUMER).withDescendant(CHAMPION_OF_THE_MEEK), 5)
    val ELEMENTAL = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"elemental"), levelModifier = 1, cooldownModifier = -10.0, manaCostModifier = -10.0).withAmplifier(1).withRange(0.0,0.0,10.0).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.ELEMENTAL_PREDICATE), 6)
    val BLADE_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"blade_aspect"), cooldownModifier = -10.0).withDamage(0.5F).withRange(0.25,0.25).withSpellToAffect(ModifierPredicates.BLADE_PREDICATE), 6)
    val FIRE_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"fire_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.FIRE_PREDICATE), 6)
    val ICE_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"ice_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.ICE_PREDICATE), 6)
    val LIGHTNING_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"lightning_aspect"), levelModifier = 1, cooldownModifier = -10.0).withDescendant(FULGUROUS).withRange(0.0,0.0,25.0).withAmplifier(1).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE), 6)
    val SUMMONERS_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"summoners_aspect"), cooldownModifier = -10.0, levelModifier = 1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.SUMMONERS_PREDICATE), 6)
    val BUILDERS_ASPECT = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"builders_aspect"), cooldownModifier = -20.0).withRange(0.0,1.0) .withSpellToAffect(ModifierPredicates.BUILDERS_PREDICATE), 4)
    val SOJOURNER = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"sojourner"), cooldownModifier = -90.0).withRange(rangePercent = 25.0).withConsumer(ModifierConsumers.SOJOURNER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE), 2)
    val TRAVELER = register(ComplexAugmentModifier(Identifier(AI.MOD_ID,"traveler"), cooldownModifier = -65.0).withRange(rangePercent = 15.0).withConsumer(ModifierConsumers.TRAVELER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE).withDescendant(SOJOURNER), 5)



    fun registerAll() {
    }
}