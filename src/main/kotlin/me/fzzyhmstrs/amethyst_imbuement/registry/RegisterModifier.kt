package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.modifier.AugmentModifier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierConsumers
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry

object RegisterModifier {

    private val regMod: MutableMap<AugmentModifier,Int> = mutableMapOf()

    //Modifiers unique to the spellcasters focus and other non-scepter things that might use rolling
    val SAVANT_ASPECT = AugmentModifier(AI.identity("savant_aspect"), rollToll = 7).withXpMod(SpellType.FURY,1).withXpMod(SpellType.GRACE,1).withXpMod(SpellType.WIT,1) .also { regMod[it] = 1 }
    val FULGUROUS = AugmentModifier(AI.identity("fulgurous"), levelModifier = 3, cooldownModifier = -40.0, rollToll = 7).withRange(0.0,0.0,50.0).withAmplifier(2).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE) .also { regMod[it] = 1 }
    val DISARMING = AugmentModifier(AI.identity("disarming")).withConsumer(ModifierConsumers.DISARMING_CONSUMER).also { regMod[it] = 4 }
    val TESTAMENT_TO_POWER = AugmentModifier(AI.identity("testament_to_power"), levelModifier = 1, cooldownModifier = 35.0, rollToll = 8).withDuration(0,0,-10).withAmplifier(6).withConsumer(ModifierConsumers.HEALING_CONSUMER).also { regMod[it] = 1 }
    val SPELL_FRENZIED = AugmentModifier(AI.identity("spell_frenzied"), cooldownModifier = -50.0,manaCostModifier = 25.0).withDamage(0f,0f,-15f).also { regMod[it] = 5 }
    val BOLT_SPECIALIST = AugmentModifier(AI.identity("bolt_specialist"), cooldownModifier = -10.0, rollToll = 6).withDamage(1f).withConsumer(ModifierConsumers.BOLT_CONSUMER).withSpellToAffect(ModifierPredicates.BOLT_PREDICATE).also { regMod[it] = 1 }
    val BOLSTERING = AugmentModifier(AI.identity("bolstering"), manaCostModifier = 15.0).withDuration(0,0,35).withConsumer(ModifierConsumers.BOLSTERING_CONSUMER).also { regMod[it] = 3 }
    val BOUNDLESS = AugmentModifier(AI.identity("boundless"), cooldownModifier = 10.0, rollToll = 6).withRange(1.0,0.0,100.0).also { regMod[it] = 2 }
    val FOWL = AugmentModifier(AI.identity("fowl")).withConsumer(ModifierConsumers.FOWL_CONSUMER).also { regMod[it] = 1 }

    //custom modifiers for witches orb
    val INNER_FIRE = AugmentModifier(AI.identity("inner_fire")).withAmplifier(2,0,10).withDuration(0,0,25).also { regMod[it] = 0 }
    val BLESSED = AugmentModifier(AI.identity("blessed")).withRange(0.0,0.0,10.0).withConsumer(ModifierConsumers.BLESSED_CONSUMER).also { regMod[it] = 0 }
    val DYNAMO = AugmentModifier(AI.identity("dynamo"), cooldownModifier = -15.0, manaCostModifier = -15.0).also { regMod[it] = 0 }

    val ENRAGED = AugmentModifier(AI.identity("enraged"), levelModifier = 1).withDamage(0.4F).withXpMod(SpellType.FURY,2).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).also { regMod[it] = 2 }
    val FURIOUS = AugmentModifier(AI.identity("furious")).withDamage(0.2F).withXpMod(SpellType.FURY,1).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).also { regMod[it] = 4 }
    val GENIUS = AugmentModifier(AI.identity("genius"), cooldownModifier = -10.0).withXpMod(SpellType.WIT,2).withRange(0.6,0.0,10.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).also { regMod[it] = 2 }
    val WITTY = AugmentModifier(AI.identity("witty")).withXpMod(SpellType.WIT,1).withRange(0.25,0.0,5.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).withDescendant(GENIUS).also { regMod[it] = 4 }
    val ELEGANT = AugmentModifier(AI.identity("elegant"), cooldownModifier = -10.0).withXpMod(SpellType.GRACE,2).withDuration(0,0,20).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod[it] = 2 }
    val GRACEFUL = AugmentModifier(AI.identity("graceful")).withXpMod(SpellType.GRACE,1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod[it] = 4 }
    val NECROTIC = AugmentModifier(AI.identity("necrotic")).withConsumer(ModifierConsumers.NECROTIC_CONSUMER).also { regMod[it] = 3 }
    val SAINTS_PACT = AugmentModifier(AI.identity("saints_pact"), cooldownModifier = 90.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE) .also { regMod[it] = 0 }
    val HEALERS_PACT = AugmentModifier(AI.identity("healers_pact"), cooldownModifier = 35.0, manaCostModifier = 35.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE).withDescendant(SAINTS_PACT) .also { regMod[it] = 0 }
    val SAINTS_GRACE = AugmentModifier(AI.identity("saints_grace")).withAmplifier(3).withDuration(durationPercent = 100).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE) .also { regMod[it] = 1 }
    val HEALERS_GRACE = AugmentModifier(AI.identity("healers_grace")).withAmplifier(1).withDuration(durationPercent = 50).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE).withDescendant(SAINTS_GRACE) .also { regMod[it] = 3}
    val LETHAL_PACT = AugmentModifier(AI.identity("pact_of_lethality"), cooldownModifier = 65.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE) .also { regMod[it] = 0 }
    val DANGEROUS_PACT= AugmentModifier(AI.identity("pact_of_danger"), cooldownModifier = 25.0, manaCostModifier = 25.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE).withDescendant(LETHAL_PACT) .also { regMod[it] = 0 }
    val HEALING = AugmentModifier(AI.identity("healing")).withConsumer(ModifierConsumers.HEALING_CONSUMER).also { regMod[it] = 5 }
    val WARRIOR_OF_LIGHT = AugmentModifier(AI.identity("warrior_of_light"), cooldownModifier = -10.0).withConsumer(ModifierConsumers.WARRIORS_CONSUMER).also { regMod[it] = 2 }
    val SMITING = AugmentModifier(AI.identity("smiting")).withConsumer(ModifierConsumers.SMITING_CONSUMER).withDescendant(WARRIOR_OF_LIGHT).also { regMod[it] = 5 }
    val INSIGHTFUL = AugmentModifier(AI.identity("insightful")).withConsumer(ModifierConsumers.INSIGHTFUL_CONSUMER).also { regMod[it] = 6 }
    val LETHAL = AugmentModifier(AI.identity("lethal")).withDamage(0.5F,0.0F,35.0F).also { regMod[it] = 1 }
    val DANGEROUS = AugmentModifier(AI.identity("dangerous")).withDamage(0.0F,0.0F,15.0F).withDescendant(LETHAL).also { regMod[it] = 2 }
    val ECHOING = AugmentModifier(AI.identity("echoing"), cooldownModifier = 10.0).withConsumer(ModifierConsumers.ECHOING_CONSUMER).also { regMod[it] = 1 }
    val MASTERFUL = AugmentModifier(AI.identity("masterful"), levelModifier = 2).also { regMod[it] = 2 }
    val SKILLFUL = AugmentModifier(AI.identity("skillful"), levelModifier = 1).withDescendant(MASTERFUL).also { regMod[it] = 4 }
    val CHAMPION_OF_THE_MEEK = AugmentModifier(AI.identity("champion_of_the_meek")).withDuration(durationPercent = 15).withConsumer(ModifierConsumers.CHAMPIONS_CONSUMER).also { regMod[it] = 2 }
    val PROTECTIVE = AugmentModifier(AI.identity("protective")).withConsumer(ModifierConsumers.PROTECTIVE_CONSUMER).withDescendant(CHAMPION_OF_THE_MEEK).also { regMod[it] = 5 }
    val ELEMENTAL = AugmentModifier(AI.identity("elemental"), levelModifier = 1, cooldownModifier = -10.0, manaCostModifier = -10.0).withAmplifier(1).withRange(0.0,0.0,10.0).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.ELEMENTAL_PREDICATE).also { regMod[it] = 6 }
    val BLADE_ASPECT = AugmentModifier(AI.identity("blade_aspect"), cooldownModifier = -10.0).withDamage(0.5F).withRange(0.25,0.25).withSpellToAffect(ModifierPredicates.BLADE_PREDICATE) .also { regMod[it] = 6 }
    val FIRE_ASPECT = AugmentModifier(AI.identity("fire_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.FIRE_PREDICATE).also { regMod[it] = 6 }
    val ICE_ASPECT = AugmentModifier(AI.identity("ice_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.ICE_PREDICATE).also { regMod[it] = 6 }
    val LIGHTNING_ASPECT = AugmentModifier(AI.identity("lightning_aspect"), levelModifier = 1, cooldownModifier = -10.0).withDescendant(FULGUROUS).withRange(0.0,0.0,25.0).withAmplifier(1).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE).also { regMod[it] = 6 }
    val SUMMONERS_ASPECT = AugmentModifier(AI.identity("summoners_aspect"), cooldownModifier = -10.0, levelModifier = 1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.SUMMONERS_PREDICATE).also { regMod[it] = 6 }
    val BUILDERS_ASPECT = AugmentModifier(AI.identity("builders_aspect"), cooldownModifier = -20.0).withRange(0.0,1.0) .withSpellToAffect(ModifierPredicates.BUILDERS_PREDICATE).also { regMod[it] = 4 }
    val SOJOURNER = AugmentModifier(AI.identity("sojourner"), cooldownModifier = -90.0).withRange(rangePercent = 25.0).withConsumer(ModifierConsumers.SOJOURNER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE) .also { regMod[it] = 2 }
    val TRAVELER = AugmentModifier(AI.identity("traveler"), cooldownModifier = -65.0).withRange(rangePercent = 15.0).withConsumer(ModifierConsumers.TRAVELER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE).withDescendant(SOJOURNER) .also { regMod[it] = 5 }



    fun registerAll(){
        regMod.forEach {
            if (it.value == 0){
                ModifierRegistry.register(it.key)
            } else {
                me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry.registerWithRolling(it.key,it.value)
            }
        }
    }
}
