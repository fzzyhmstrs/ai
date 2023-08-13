package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_imbuement.AI

object ModifiableEffects {

    fun init(){}

    //used for the ticking of the entity
    val SHOCKING_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("shocking_effect")) {entity,attackerOrOwner,context-> ShockingEffect.shock(entity,attackerOrOwner,context)}
    val STATIC_SHOCK_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("static_shock_effect")) {entity,attackerOrOwner,context-> ShockingEffect.staticShock(entity,attackerOrOwner,context)}
    val CHAIN_LIGHTNING_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("chain_lightning_effect")) {entity,attackerOrOwner,context-> ShockingEffect.chainLightning(entity,attackerOrOwner,context)}
    val GROW_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("grow_effect")) {entity,attackerOrOwner,context-> AbundanceEffect.grow(entity,attackerOrOwner,context)}
    val GUST_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("gust_effect")) {entity,attackerOrOwner,context-> SummonChickenEffect.gust(entity,attackerOrOwner,context)}
    val CALL_HOSTILES_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("call_hostiles_effect")) {entity,attackerOrOwner,context-> BodySwapEffect.callHostiles(entity,attackerOrOwner,context)}
    val CALL_SUMMONS_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("call_summons_effect")) {entity,attackerOrOwner,context-> BodySwapEffect.callSummons(entity,attackerOrOwner,context)}
    val SHINE_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("shine_effect")) {entity,attackerOrOwner,context-> ShineEffect.shine(entity,attackerOrOwner,context)}
    val ECHO_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("echo_effect")) {entity,attackerOrOwner,context-> EchoingEffect.resonate(entity,attackerOrOwner,context)}
    val SHIELD_OF_FAITH_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("shield_of_faith_effect")) {entity,attackerOrOwner,context-> ShieldOfFaithEffect.shield(entity,attackerOrOwner,context)}
    val DRAW_AGGRO_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("draw_aggro_effect")) {entity,attackerOrOwner,context-> BedazzlingEffect.drawAggro(entity,attackerOrOwner,context)}
    val FROST_NOVA_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("frost_nova_effect")) {entity,attackerOrOwner,context-> IcyEffects.nova(entity,attackerOrOwner,context)}
    val ICE_SPIKES_SUMMON_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("ice_spikes_summon_effect")) {entity,attackerOrOwner,context-> IcyEffects.jab(entity,attackerOrOwner,context)}
    val ICE_SPIKES_BOOST_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("ice_spikes_boost_effect")) {entity,attackerOrOwner,context-> IcyEffects.stab(entity,attackerOrOwner,context)}

}
