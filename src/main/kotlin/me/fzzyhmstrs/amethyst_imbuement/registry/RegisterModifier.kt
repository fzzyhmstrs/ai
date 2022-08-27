package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierConsumers
import me.fzzyhmstrs.amethyst_imbuement.modifier.ModifierPredicates
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import net.minecraft.util.Identifier

object RegisterModifier {

    private val regMod: MutableList<AugmentModifier> = mutableListOf()

    val FURIOUS = AugmentModifier(Identifier(AI.MOD_ID,"furious"), levelModifier = 1).withDamage(0.0F,0.2F).withXpMod(SpellType.FURY,1).withSpellToAffect(ModifierPredicates.FURIOUS_PREDICATE).also { regMod.add(it) }
    val GENIUS = AugmentModifier(Identifier(AI.MOD_ID,"genius"), cooldownModifier = -10.0).withXpMod(SpellType.WIT,2).withRange(0.6,0.0,25.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).also { regMod.add(it) }
    val WITTY = AugmentModifier(Identifier(AI.MOD_ID,"witty")).withXpMod(SpellType.WIT,1).withRange(0.25,0.0,15.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).withDescendant(GENIUS).also { regMod.add(it) }
    val GRACEFUL = AugmentModifier(Identifier(AI.MOD_ID,"graceful")).withXpMod(SpellType.GRACE,1).withDuration(0,0,15).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod.add(it) }
    val NECROTIC = AugmentModifier(Identifier(AI.MOD_ID,"necrotic")).withConsumer(ModifierConsumers.NECROTIC_CONSUMER).also { regMod.add(it) }
    val SAINTS_PACT = AugmentModifier(Identifier(AI.MOD_ID,"saints_pact"), cooldownModifier = 75.0, manaCostModifier = 50.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE) .also { regMod.add(it) }
    val HEALERS_PACT = AugmentModifier(Identifier(AI.MOD_ID,"healers_pact"), cooldownModifier = 50.0, manaCostModifier = 20.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE).withDescendant(SAINTS_PACT) .also { regMod.add(it) }
    val SAINTS_GRACE = AugmentModifier(Identifier(AI.MOD_ID,"saints_grace")).withAmplifier(3).withDuration(durationPercent = 100).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE) .also { regMod.add(it) }
    val HEALERS_GRACE = AugmentModifier(Identifier(AI.MOD_ID,"healers_grace")).withAmplifier(2).withDuration(durationPercent = 50).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE).withDescendant(SAINTS_GRACE) .also { regMod.add(it) }
    val HEALING = AugmentModifier(Identifier(AI.MOD_ID,"healing")).withConsumer(ModifierConsumers.HEALING_CONSUMER).withDescendant(HEALERS_GRACE).also { regMod.add(it) }
    val SMITING = AugmentModifier(Identifier(AI.MOD_ID,"smiting")).withConsumer(ModifierConsumers.SMITING_CONSUMER).also { regMod.add(it) }
    val INSIGHTFUL = AugmentModifier(Identifier(AI.MOD_ID,"insightful")).withConsumer(ModifierConsumers.INSIGHTFUL_CONSUMER).also { regMod.add(it) }
    val LETHAL = AugmentModifier(Identifier(AI.MOD_ID,"lethal")).withDamage(0.5F,0.0F,25.0F).also { regMod.add(it) }
    val DANGEROUS = AugmentModifier(Identifier(AI.MOD_ID,"dangerous")).withDamage(0.0F,0.0F,12.5F).withDescendant(LETHAL).also { regMod.add(it) }
    val ECHOING = AugmentModifier(Identifier(AI.MOD_ID,"echoing"), cooldownModifier = 10.0).withConsumer(ModifierConsumers.ECHOING_CONSUMER).also { regMod.add(it) }
    val GREATER_REACH = AugmentModifier(Identifier(AI.MOD_ID,"greater_reach")).withRange(rangePercent = 18.0).also { regMod.add(it) }
    val REACH = AugmentModifier(Identifier(AI.MOD_ID,"reach")).withDescendant(GREATER_REACH).withRange(rangePercent = 12.0).also { regMod.add(it) }
    val LESSER_REACH = AugmentModifier(Identifier(AI.MOD_ID,"lesser_reach")).withDescendant(REACH).withRange(rangePercent = 6.0).also { regMod.add(it) }
    val GREATER_ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"greater_enduring")).withDuration(durationPercent = 25).also { regMod.add(it) }
    val ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"enduring")).withDescendant(GREATER_ENDURING).withDuration(durationPercent = 15).also { regMod.add(it) }
    val LESSER_ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"lesser_enduring")).withDescendant(ENDURING).withDuration(durationPercent = 10).also { regMod.add(it) }
    val MASTERFUL = AugmentModifier(Identifier(AI.MOD_ID,"masterful"), levelModifier = 2).also { regMod.add(it) }
    val SKILLFUL = AugmentModifier(Identifier(AI.MOD_ID,"skillful"), levelModifier = 1).withDescendant(MASTERFUL).also { regMod.add(it) }
    val AEGIS = AugmentModifier(Identifier(AI.MOD_ID,"aegis")).withConsumer(ModifierConsumers.AEGIS_CONSUMER).also { regMod.add(it) }
    val PROTECTIVE = AugmentModifier(Identifier(AI.MOD_ID,"protective")).withConsumer(ModifierConsumers.PROTECTIVE_CONSUMER).withDescendant(AEGIS).also { regMod.add(it) }
    val ELEMENTAL = AugmentModifier(Identifier(AI.MOD_ID,"elemental"), cooldownModifier = -10.0, manaCostModifier = -5.0).withAmplifier(1).withRange(0.0,0.0,10.0).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.ELEMENTAL_PREDICATE).also { regMod.add(it) }
    val BLADE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"blade_aspect"), cooldownModifier = -5.0).withDamage(0.5F).withRange(0.25,0.25).withSpellToAffect(ModifierPredicates.BLADE_PREDICATE) .also { regMod.add(it) }
    val FIRE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"fire_aspect"), cooldownModifier = -5.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.FIRE_PREDICATE).also { regMod.add(it) }
    val ICE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"ice_aspect"), cooldownModifier = -5.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.ICE_PREDICATE).also { regMod.add(it) }
    val LIGHTNING_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"lightning_aspect"), cooldownModifier = -5.0).withRange(0.0,0.0,25.0).withAmplifier(1).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE).also { regMod.add(it) }
    val SUMMONERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"summoners_aspect"), cooldownModifier = -10.0, levelModifier = 1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.SUMMONERS_PREDICATE).also { regMod.add(it) }
    val BUILDERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"builders_aspect"), cooldownModifier = -15.0).withRange(0.0,1.0) .withSpellToAffect(ModifierPredicates.BUILDERS_PREDICATE).also { regMod.add(it) }
    val SOJOURNER = AugmentModifier(Identifier(AI.MOD_ID,"sojourner"), cooldownModifier = -90.0).withRange(rangePercent = 25.0).withConsumer(ModifierConsumers.SOJOURNER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE) .also { regMod.add(it) }
    val TRAVELER = AugmentModifier(Identifier(AI.MOD_ID,"traveler"), cooldownModifier = -65.0).withRange(rangePercent = 15.0).withConsumer(ModifierConsumers.TRAVELER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE).withDescendant(SOJOURNER) .also { regMod.add(it) }


    fun registerAll(){
        regMod.forEach {
            ModifierRegistry.register(it)
        }
    }
}
