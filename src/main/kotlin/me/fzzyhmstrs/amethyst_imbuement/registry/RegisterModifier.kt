package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierConsumers
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import net.minecraft.util.Identifier

object RegisterModifier {

    private val regMod: MutableMap<AugmentModifier,Int> = mutableMapOf()

    val ENRAGED = AugmentModifier(Identifier(AI.MOD_ID,"enraged"), levelModifier = 1).withDamage(0.4F).withXpMod(SpellType.FURY,2).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).also { regMod.[it] = 2 }
    val FURIOUS = AugmentModifier(Identifier(AI.MOD_ID,"furious")).withDamage(0.2F).withXpMod(SpellType.FURY,1).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).also { regMod.[it] = 4 }
    val GENIUS = AugmentModifier(Identifier(AI.MOD_ID,"genius"), cooldownModifier = -10.0).withXpMod(SpellType.WIT,2).withRange(0.6,0.0,10.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).also { regMod.[it] = 2 }
    val WITTY = AugmentModifier(Identifier(AI.MOD_ID,"witty")).withXpMod(SpellType.WIT,1).withRange(0.25,0.0,5.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).withDescendant(GENIUS).also { regMod.[it] = 4 }
    val ELEGANT = AugmentModifier(Identifier(AI.MOD_ID,"elegant"), cooldownModifier = -10.0).withXpMod(SpellType.GRACE,2).withDuration(0,0,20).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod.[it] = 2 }
    val GRACEFUL = AugmentModifier(Identifier(AI.MOD_ID,"graceful")).withXpMod(SpellType.GRACE,1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod.[it] = 4 }
    val NECROTIC = AugmentModifier(Identifier(AI.MOD_ID,"necrotic")).withConsumer(ModifierConsumers.NECROTIC_CONSUMER).also { regMod.[it] = 3 }
    val SAINTS_PACT = AugmentModifier(Identifier(AI.MOD_ID,"saints_pact"), cooldownModifier = 90.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE) .also { regMod.[it] = 0 }
    val HEALERS_PACT = AugmentModifier(Identifier(AI.MOD_ID,"healers_pact"), cooldownModifier = 35.0, manaCostModifier = 35.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE).withDescendant(SAINTS_PACT) .also { regMod.[it] = 0 }
    val SAINTS_GRACE = AugmentModifier(Identifier(AI.MOD_ID,"saints_grace")).withAmplifier(3).withDuration(durationPercent = 100).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE) .also { regMod.[it] = 1 }
    val HEALERS_GRACE = AugmentModifier(Identifier(AI.MOD_ID,"healers_grace")).withAmplifier(1).withDuration(durationPercent = 50).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE).withDescendant(SAINTS_GRACE) .also { regMod.[it] = 3}
    val LETHAL_PACT = AugmentModifier(Identifier(AI.MOD_ID,"pact_of_lethality"), cooldownModifier = 65.0, manaCostModifier = 65.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE) .also { regMod.[it] = 0 }
    val DANGEROUS_PACT= AugmentModifier(Identifier(AI.MOD_ID,"pact_of_danger"), cooldownModifier = 25.0, manaCostModifier = 25.0).withSpellToAffect(ModifierPredicates.DANGER_PACT_PREDICATE).withDescendant(LETHAL_PACT) .also { regMod.[it] = 0 }
    val HEALING = AugmentModifier(Identifier(AI.MOD_ID,"healing")).withConsumer(ModifierConsumers.HEALING_CONSUMER).also { regMod.[it] = 5 }
    val WARRIOR_OF_LIGHT = AugmentModifier(Identifier(AI.MOD_ID,"warrior_of_light"), cooldownModifier = -10.0).withConsumer(ModifierConsumers.WARRIORS_CONSUMER).also { regMod.[it] = 2 }
    val SMITING = AugmentModifier(Identifier(AI.MOD_ID,"smiting")).withConsumer(ModifierConsumers.SMITING_CONSUMER).withDescendant(WARRIOR_OF_LIGHT).also { regMod.[it] = 5 }
    val INSIGHTFUL = AugmentModifier(Identifier(AI.MOD_ID,"insightful")).withConsumer(ModifierConsumers.INSIGHTFUL_CONSUMER).also { regMod.[it] = 6 }
    val LETHAL = AugmentModifier(Identifier(AI.MOD_ID,"lethal")).withDamage(0.5F,0.0F,35.0F).also { regMod.[it] = 1 }
    val DANGEROUS = AugmentModifier(Identifier(AI.MOD_ID,"dangerous")).withDamage(0.0F,0.0F,15.0F).withDescendant(LETHAL).also { regMod.[it] = 2 }
    val ECHOING = AugmentModifier(Identifier(AI.MOD_ID,"echoing"), cooldownModifier = 10.0).withConsumer(ModifierConsumers.ECHOING_CONSUMER).also { regMod.[it] = 1 }
    val MASTERFUL = AugmentModifier(Identifier(AI.MOD_ID,"masterful"), levelModifier = 2).also { regMod.[it] = 2 }
    val SKILLFUL = AugmentModifier(Identifier(AI.MOD_ID,"skillful"), levelModifier = 1).withDescendant(MASTERFUL).also { regMod.[it] = 4 }
    val CHAMPION_OF_THE_MEEK = AugmentModifier(Identifier(AI.MOD_ID,"champion_of_the_meek")).withDuration(durationPercent = 15).withConsumer(ModifierConsumers.CHAMPIONS_CONSUMER).also { regMod.[it] = 2 }
    val PROTECTIVE = AugmentModifier(Identifier(AI.MOD_ID,"protective")).withConsumer(ModifierConsumers.PROTECTIVE_CONSUMER).withDescendant(CHAMPION_OF_THE_MEEK).also { regMod.[it] = 5 }
    val ELEMENTAL = AugmentModifier(Identifier(AI.MOD_ID,"elemental"), levelModifier = 1, cooldownModifier = -10.0, manaCostModifier = -10.0).withAmplifier(1).withRange(0.0,0.0,10.0).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.ELEMENTAL_PREDICATE).also { regMod.[it] = 6 }
    val BLADE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"blade_aspect"), cooldownModifier = -10.0).withDamage(0.5F).withRange(0.25,0.25).withSpellToAffect(ModifierPredicates.BLADE_PREDICATE) .also { regMod.[it] = 6 }
    val FIRE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"fire_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.FIRE_PREDICATE).also { regMod.[it] = 6 }
    val ICE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"ice_aspect"), levelModifier = 1, cooldownModifier = -10.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.ICE_PREDICATE).also { regMod.[it] = 6 }
    val LIGHTNING_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"lightning_aspect"), levelModifier = 1, cooldownModifier = -10.0).withRange(0.0,0.0,25.0).withAmplifier(1).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE).also { regMod.[it] = 6 }
    val SUMMONERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"summoners_aspect"), cooldownModifier = -10.0, levelModifier = 1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.SUMMONERS_PREDICATE).also { regMod.[it] = 6 }
    val BUILDERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"builders_aspect"), cooldownModifier = -20.0).withRange(0.0,1.0) .withSpellToAffect(ModifierPredicates.BUILDERS_PREDICATE).also { regMod.[it] = 4 }
    val SOJOURNER = AugmentModifier(Identifier(AI.MOD_ID,"sojourner"), cooldownModifier = -90.0).withRange(rangePercent = 25.0).withConsumer(ModifierConsumers.SOJOURNER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE) .also { regMod.[it] = 2 }
    val TRAVELER = AugmentModifier(Identifier(AI.MOD_ID,"traveler"), cooldownModifier = -65.0).withRange(rangePercent = 15.0).withConsumer(ModifierConsumers.TRAVELER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE).withDescendant(SOJOURNER) .also { regMod.[it] = 5 }

    //Modifiers unique to the spellcasters focus and other non-scepter things that might use rolling
    val SAVANT_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"savant_aspect").withXpMod(SpellType.FURY,1).withXpMod(SpellType.GRACE,1).withXpMod(SpellType.WIT,1) .also { regMod.[it] = 1 }

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
