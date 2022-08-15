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
    val WITTY = AugmentModifier(Identifier(AI.MOD_ID,"witty")).withXpMod(SpellType.WIT,1).withRange(0.25,0.0,15.0).withSpellToAffect(ModifierPredicates.WITTY_PREDICATE).also { regMod.add(it) }
    val GRACEFUL = AugmentModifier(Identifier(AI.MOD_ID,"graceful")).withXpMod(SpellType.GRACE,1).withDuration(0,0,15).withSpellToAffect(ModifierPredicates.GRACEFUL_PREDICATE).also { regMod.add(it) }
    val DEMANDING = AugmentModifier(Identifier(AI.MOD_ID,"demanding"), manaCostModifier = 15.0) .also { regMod.add(it) }
    val NECROTIC = AugmentModifier(Identifier(AI.MOD_ID,"necrotic")).withConsumer(ModifierConsumers.NECROTIC_CONSUMER).also { regMod.add(it) }
    val HEALING = AugmentModifier(Identifier(AI.MOD_ID,"healing")).withConsumer(ModifierConsumers.HEALING_CONSUMER).also { regMod.add(it) }
    val SMITING = AugmentModifier(Identifier(AI.MOD_ID,"smiting")).withConsumer(ModifierConsumers.SMITING_CONSUMER).also { regMod.add(it) }
    val INSIGHTFUL = AugmentModifier(Identifier(AI.MOD_ID,"insightful")).withConsumer(ModifierConsumers.INSIGHTFUL_CONSUMER).also { regMod.add(it) }
    val LETHAL = AugmentModifier(Identifier(AI.MOD_ID,"lethal")).withDamage(0.0F,0.0F,30.0F).also { regMod.add(it) }
    val MURDEROUS = AugmentModifier(Identifier(AI.MOD_ID,"murderous")).withDamage(0.0F,0.0F,20.0F).withDescendant(LETHAL).also { regMod.add(it) }
    val DANGEROUS = AugmentModifier(Identifier(AI.MOD_ID,"dangerous")).withDamage(0.0F,0.0F,10F).withDescendant(MURDEROUS).also { regMod.add(it) }
    val ECHOING = AugmentModifier(Identifier(AI.MOD_ID,"echoing"), cooldownModifier = 10.0).withConsumer(ModifierConsumers.ECHOING_CONSUMER).also { regMod.add(it) }
    val GREATER_REACH = AugmentModifier(Identifier(AI.MOD_ID,"greater_reach")).withRange(rangePercent = 18.0).also { regMod.add(it) }
    val REACH = AugmentModifier(Identifier(AI.MOD_ID,"reach")).withDescendant(GREATER_REACH).withRange(rangePercent = 12.0).also { regMod.add(it) }
    val LESSER_REACH = AugmentModifier(Identifier(AI.MOD_ID,"lesser_reach")).withDescendant(REACH).withRange(rangePercent = 6.0).also { regMod.add(it) }
    val GREATER_ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"greater_enduring")).withDuration(durationPercent = 20).also { regMod.add(it) }
    val ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"enduring")).withDescendant(GREATER_ENDURING).withDuration(durationPercent = 15).also { regMod.add(it) }
    val LESSER_ENDURING = AugmentModifier(Identifier(AI.MOD_ID,"lesser_enduring")).withDescendant(ENDURING).withDuration(durationPercent = 10).also { regMod.add(it) }
    val MASTERFUL = AugmentModifier(Identifier(AI.MOD_ID,"masterful"), levelModifier = 2).also { regMod.add(it) }
    val SKILLFUL = AugmentModifier(Identifier(AI.MOD_ID,"skillful"), levelModifier = 1).withDescendant(MASTERFUL).also { regMod.add(it) }
    val BLADE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"blade_aspect"), cooldownModifier = -5.0).withDamage(0.5F).withRange(0.25,0.25).withSpellToAffect(ModifierPredicates.BLADE_PREDICATE) .also { regMod.add(it) }
    val FIRE_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"fire_aspect"), cooldownModifier = -5.0).withAmplifier(1).withDuration(32).withSpellToAffect(ModifierPredicates.FIRE_PREDICATE).also { regMod.add(it) }
    val LIGHTNING_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"lightning_aspect"), cooldownModifier = -5.0).withRange(0.0,0.0,25.0).withAmplifier(1).withSpellToAffect(ModifierPredicates.LIGHTNING_PREDICATE).also { regMod.add(it) }
    val SUMMONERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"summoners_aspect"), cooldownModifier = -10.0, levelModifier = 1).withDuration(0,0,10).withSpellToAffect(ModifierPredicates.SUMMONERS_PREDICATE).also { regMod.add(it) }
    val BUILDERS_ASPECT = AugmentModifier(Identifier(AI.MOD_ID,"builders_aspect"), cooldownModifier = -15.0).withRange(0.0,1.0) .withSpellToAffect(ModifierPredicates.BUILDERS_PREDICATE).also { regMod.add(it) }
    val HEALERS_PACT = AugmentModifier(Identifier(AIO.MOD_ID,"healers_pact"), cooldownModifier = 50.0, manaCostModifier = 20.0).withSpellToAffect(ModifierPredicates.HEALERS_PACT_PREDICATE) .also { regMod.add(it) }
    val HEALERS_GRACE = AugmentModifier(Identifier(AIO.MOD_ID,"healers_grace")).withAmplifier(1).withDuration(durationPercent = 50).withSpellToAffect(ModifierPredicates.HEALERS_PREDICATE) .also { regMod.add(it) }
    val TRAVELER = AugmentModifier(Identifier(AIO.MOD_ID,"traveler"), cooldownModifier = -90.0).withRange(rangePercent = 15.0).withConsumer(ModifierConsumers.TRAVELER_CONSUMER).withSpellToAffect(ModifierPredicates.TRAVELER_PREDICATE) .also { regMod.add(it) }


    fun registerAll(){
        regMod.forEach {
            ModifierRegistry.register(it)
        }
        regMod.clear()
    }

}
