package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.advancement.ClientAdvancementContainer
import me.fzzyhmstrs.amethyst_core.advancement.FeatureCriteria
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.function.Supplier

object SpellAdvancementChecks {

    //the base advancement of damage,amplifier,duration,range,cooldown,mana cost
    private val STAT_ID = AI.identity("spell_features/stat_feature")
    val STAT_TRIGGER = Trigger(AI.identity("stat"))
    val STAT = makeConfigSupplier { ClientAdvancementContainer.isDone(STAT_ID) }

    private val DAMAGE_ID = AI.identity("spell_features/stat/damage_feature")
    val DAMAGE_TRIGGER = Trigger(AI.identity("damage"), STAT_TRIGGER)
    val DAMAGE = makeConfigSupplier { ClientAdvancementContainer.isDone(DAMAGE_ID) }

    private val AMPLIFIER_ID = AI.identity("spell_features/stat/amplifier_feature")
    val AMPLIFIER_TRIGGER = Trigger(AI.identity("amplifier"), STAT_TRIGGER)
    val AMPLIFIER = makeConfigSupplier { ClientAdvancementContainer.isDone(AMPLIFIER_ID) }

    private val DURATION_ID = AI.identity("spell_features/stat/duration_feature")
    val DURATION_TRIGGER = Trigger(AI.identity("duration"), STAT_TRIGGER)
    val DURATION = makeConfigSupplier { ClientAdvancementContainer.isDone(DURATION_ID) }

    private val RANGE_ID = AI.identity("spell_features/stat/range_feature")
    val RANGE_TRIGGER = Trigger(AI.identity("range"), STAT_TRIGGER)
    val RANGE = makeConfigSupplier { ClientAdvancementContainer.isDone(RANGE_ID) }

    private val COOLDOWN_ID = AI.identity("spell_features/stat/cooldown_feature")
    val COOLDOWN_TRIGGER = Trigger(AI.identity("cooldown"), STAT_TRIGGER)
    val COOLDOWN = makeConfigSupplier { ClientAdvancementContainer.isDone(COOLDOWN_ID) }

    private val MANA_COST_ID = AI.identity("spell_features/stat/mana_cost_feature")
    val MANA_COST_TRIGGER = Trigger(AI.identity("mana_cost"), STAT_TRIGGER)
    val MANA_COST = makeConfigSupplier { ClientAdvancementContainer.isDone(MANA_COST_ID) }

    private val ON_KILL_ID = AI.identity("spell_features/stat/damage/on_kill_feature")
    val ON_KILL_TRIGGER = Trigger(AI.identity("on_kill"), DAMAGE_TRIGGER)
    val ON_KILL = makeConfigSupplier { ClientAdvancementContainer.isDone(ON_KILL_ID) }

    private val DAMAGE_SOURCE_ID = AI.identity("spell_features/stat/damage/damage_source_feature")
    val DAMAGE_SOURCE_TRIGGER = Trigger(AI.identity("damage_source"), DAMAGE_TRIGGER)
    val DAMAGE_SOURCE = makeConfigSupplier { ClientAdvancementContainer.isDone(DAMAGE_SOURCE_ID) }

    private val DOUBLE_ID = AI.identity("spell_features/double_feature")
    val DOUBLE_TRIGGER = Trigger(AI.identity("double"))
    val DOUBLE = makeConfigSupplier { ClientAdvancementContainer.isDone(DOUBLE_ID) }

    private val UNIQUE_ID = AI.identity("spell_features/unique_feature")
    val UNIQUE_TRIGGER = Trigger(AI.identity("unique"))
    val UNIQUE = makeConfigSupplier { ClientAdvancementContainer.isDone(UNIQUE_ID) }

    private val ELEMENTAL_ID = AI.identity("spell_features/elemental_feature")
    val ELEMENTAL_TRIGGER = Trigger(AI.identity("elemental"))
    val ELEMENTAL = makeConfigSupplier { ClientAdvancementContainer.isDone(ELEMENTAL_ID) }

    private val LIGHTNING_ID = AI.identity("spell_features/elemental/lightning_feature")
    val LIGHTNING_TRIGGER = Trigger(AI.identity("lightning"))
    val LIGHTNING = makeConfigSupplier { ClientAdvancementContainer.isDone(LIGHTNING_ID) }

    private val FLAME_ID = AI.identity("spell_features/elemental/flame_feature")
    val FLAME_TRIGGER = Trigger(AI.identity("flame"))
    val FLAME = makeConfigSupplier { ClientAdvancementContainer.isDone(FLAME_ID) }

    private val ICE_ID = AI.identity("spell_features/elemental/ice_feature")
    val ICE_TRIGGER = Trigger(AI.identity("ice"))
    val ICE = makeConfigSupplier { ClientAdvancementContainer.isDone(ICE_ID) }

    private val SOUL_ID = AI.identity("spell_features/elemental/soul_feature")
    val SOUL_TRIGGER = Trigger(AI.identity("soul"))
    val SOUL = makeConfigSupplier { ClientAdvancementContainer.isDone(SOUL_ID) }

    private val STUNNED_ID = AI.identity("spell_features/elemental/stunned_feature")
    val STUNNED_TRIGGER = Trigger(AI.identity("stunned"))
    val STUNS = makeConfigSupplier { ClientAdvancementContainer.isDone(STUNNED_ID) }

    private val SUMMONS_ID = AI.identity("spell_features/summons_feature")
    val SUMMONS_TRIGGER = Trigger(AI.identity("summons"))
    val SUMMONS = makeConfigSupplier { ClientAdvancementContainer.isDone(SUMMONS_ID) }

    private val ENTITY_EFFECT_ID = AI.identity("spell_features/entity_effect_feature")
    val ENTITY_EFFECT_TRIGGER = Trigger(AI.identity("entity_effect"))
    val ENTITY_EFFECT = makeConfigSupplier { ClientAdvancementContainer.isDone(ENTITY_EFFECT_ID) }

    private val PROTECTED_ID = AI.identity("spell_features/entity_effect/protected_feature")
    val PROTECTED_TRIGGER = Trigger(AI.identity("protected"), ENTITY_EFFECT_TRIGGER)
    val PROTECTED_EFFECT = makeConfigSupplier { ClientAdvancementContainer.isDone(PROTECTED_ID) }

    private val BOOSTED_ID = AI.identity("spell_features/entity_effect/boosted_feature")
    val BOOSTED_TRIGGER = Trigger(AI.identity("boosted"), ENTITY_EFFECT_TRIGGER)
    val BOOSTED_EFFECT = makeConfigSupplier { ClientAdvancementContainer.isDone(BOOSTED_ID) }

    private val HARMED_ID = AI.identity("spell_features/entity_effect/harmed_feature")
    val HARMED_TRIGGER = Trigger(AI.identity("harmed"), ENTITY_EFFECT_TRIGGER)
    val HARMED_EFFECT = makeConfigSupplier { ClientAdvancementContainer.isDone(HARMED_ID) }

    private val HEALTH_ID = AI.identity("spell_features/entity_effect/health_feature")
    val HEALTH_TRIGGER = Trigger(AI.identity("health"), ENTITY_EFFECT_TRIGGER)
    val HEALTH = makeConfigSupplier { ClientAdvancementContainer.isDone(HEALTH_ID) }

    private val SPEED_ID = AI.identity("spell_features/entity_effect/speed_feature")
    val SPEED_TRIGGER = Trigger(AI.identity("speed"), ENTITY_EFFECT_TRIGGER)
    val SPEED = makeConfigSupplier { ClientAdvancementContainer.isDone(SPEED_ID) }

    private val EXPLODES_ID = AI.identity("spell_features/explodes_feature")
    val EXPLODES_TRIGGER = Trigger(AI.identity("explodes"))
    val EXPLODES = makeConfigSupplier { ClientAdvancementContainer.isDone(EXPLODES_ID) }

    private val BLOCK_ID = AI.identity("spell_features/block_feature")
    val BLOCK_TRIGGER = Trigger(AI.identity("block"))
    val BLOCK = makeConfigSupplier { ClientAdvancementContainer.isDone(BLOCK_ID) }

    private val PROJECTILE_ID = AI.identity("spell_features/projectile_feature")
    val PROJECTILE_TRIGGER = Trigger(AI.identity("projectile"))
    val PROJECTILE = makeConfigSupplier { ClientAdvancementContainer.isDone(PROJECTILE_ID) }

    private val CHICKEN_ID = AI.identity("spell_features/chicken_feature")
    val CHICKEN_TRIGGER = Trigger(AI.identity("chicken"))
    val CHICKEN = makeConfigSupplier { ClientAdvancementContainer.isDone(CHICKEN_ID) }

    fun uniqueOrDouble(player: ServerPlayerEntity, pair: PairedAugments){
        if (pair.spellsAreEqual()){
            grant(player,DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            grant(player,UNIQUE_TRIGGER)
        }
    }

    fun grant(player: ServerPlayerEntity, trigger: Trigger){
        FeatureCriteria.PAIRED_FEATURE.trigger(player,trigger.id)
        for (child in trigger.children){
            FeatureCriteria.PAIRED_FEATURE.trigger(player,child.id)
        }
    }

    private fun makeConfigSupplier(supplier: Supplier<Boolean>): Supplier<Boolean>{
        return supplier.or { AiConfig.enchants.disableSpellProgression.get() }
    }

    fun Supplier<Boolean>.or(other: Supplier<Boolean>): Supplier<Boolean>{
        return Supplier<Boolean>{ this.get() || other.get() }
    }
    fun Supplier<Boolean>.and(other: Supplier<Boolean>): Supplier<Boolean>{
        return Supplier<Boolean>{ this.get() && other.get() }
    }

    class Trigger(val id: Identifier, vararg val children: Trigger)

}
