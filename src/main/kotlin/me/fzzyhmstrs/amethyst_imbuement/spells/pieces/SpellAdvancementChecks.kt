package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.advancement.ClientAdvancementContainer
import me.fzzyhmstrs.amethyst_core.advancement.FeatureCriteria
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.function.Supplier

object SpellAdvancementChecks {

    //the base advancement of damage,amplifier,duration,range,cooldown,mana cost
    private val STAT_ID = AI.identity("todo")
    val STAT_TRIGGER = Trigger(AI.identity("stat"))
    val STAT = Supplier<Boolean> {ClientAdvancementContainer.isDone(STAT_ID)}

    private val DAMAGE_ID = AI.identity("todo")
    val DAMAGE_TRIGGER = Trigger(AI.identity("damage"), STAT_TRIGGER)
    val DAMAGE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DAMAGE_ID)}

    private val AMPLIFIER_ID = AI.identity("todo")
    val AMPLIFIER_TRIGGER = Trigger(AI.identity("amplifier"), STAT_TRIGGER)
    val AMPLIFIER = Supplier<Boolean> {ClientAdvancementContainer.isDone(AMPLIFIER_ID)}

    private val DURATION_ID = AI.identity("todo")
    val DURATION_TRIGGER = Trigger(AI.identity("duration"), STAT_TRIGGER)
    val DURATION = Supplier<Boolean> {ClientAdvancementContainer.isDone(DURATION_ID)}

    private val RANGE_ID = AI.identity("todo")
    val RANGE_TRIGGER = Trigger(AI.identity("range"), STAT_TRIGGER)
    val RANGE = Supplier<Boolean> {ClientAdvancementContainer.isDone(RANGE_ID)}

    private val COOLDOWN_ID = AI.identity("todo")
    val COOLDOWN_TRIGGER = Trigger(AI.identity("cooldown"), STAT_TRIGGER)
    val COOLDOWN = Supplier<Boolean> {ClientAdvancementContainer.isDone(COOLDOWN_ID)}

    private val MANA_COST_ID = AI.identity("todo")
    val MANA_COST_TRIGGER = Trigger(AI.identity("mana_cost"), STAT_TRIGGER)
    val MANA_COST = Supplier<Boolean> {ClientAdvancementContainer.isDone(MANA_COST_ID)}

    private val ON_KILL_ID = AI.identity("todo")
    val ON_KILL_TRIGGER = Trigger(AI.identity("on_kill"))
    val ON_KILL = Supplier<Boolean> {ClientAdvancementContainer.isDone(ON_KILL_ID)}

    private val DAMAGE_SOURCE_ID = AI.identity("todo")
    val DAMAGE_SOURCE_TRIGGER = Trigger(AI.identity("damage"))
    val DAMAGE_SOURCE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DAMAGE_SOURCE_ID)}

    private val DOUBLE_ID = AI.identity("todo")
    val DOUBLE_TRIGGER = Trigger(AI.identity("stunned"))
    val DOUBLE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DOUBLE_ID)}

    private val UNIQUE_ID = AI.identity("todo")
    val UNIQUE_TRIGGER = Trigger(AI.identity("unique"))
    val UNIQUE = Supplier<Boolean> {ClientAdvancementContainer.isDone(UNIQUE_ID)}

    private val LIGHTNING_ID = AI.identity("todo")
    val LIGHTNING_TRIGGER = Trigger(AI.identity("lightning"))
    val LIGHTNING = Supplier<Boolean> {ClientAdvancementContainer.isDone(LIGHTNING_ID)}

    private val FLAME_ID = AI.identity("todo")
    val FLAME_TRIGGER = Trigger(AI.identity("flame"))
    val FLAME = Supplier<Boolean> {ClientAdvancementContainer.isDone(FLAME_ID)}

    private val ICE_ID = AI.identity("todo")
    val ICE_TRIGGER = Trigger(AI.identity("ice"))
    val ICE = Supplier<Boolean> {ClientAdvancementContainer.isDone(ICE_ID)}

    private val SOUL_ID = AI.identity("todo")
    val SOUL_TRIGGER = Trigger(AI.identity("soul"))
    val SOUL = Supplier<Boolean> {ClientAdvancementContainer.isDone(SOUL_ID)}

    private val STUNNED_ID = AI.identity("todo")
    val STUNNED_TRIGGER = Trigger(AI.identity("stunned"))
    val STUNS = Supplier<Boolean> {ClientAdvancementContainer.isDone(STUNNED_ID)}

    private val SUMMONS_ID = AI.identity("todo")
    val SUMMONS_TRIGGER = Trigger(AI.identity("summons"))
    val SUMMONS = Supplier<Boolean> {ClientAdvancementContainer.isDone(SUMMONS_ID)}

    private val ENTITY_EFFECT_ID = AI.identity("todo")
    val ENTITY_EFFECT_TRIGGER = Trigger(AI.identity("entity_effect"))
    val ENTITY_EFFECT = Supplier<Boolean> {ClientAdvancementContainer.isDone(ENTITY_EFFECT_ID)}

    private val PROTECTED_ID = AI.identity("todo")
    val PROTECTED_TRIGGER = Trigger(AI.identity("protected"), ENTITY_EFFECT_TRIGGER)
    val PROTECTED_EFFECT = Supplier<Boolean> {ClientAdvancementContainer.isDone(PROTECTED_ID)}

    private val BOOSTED_ID = AI.identity("todo")
    val BOOSTED_TRIGGER = Trigger(AI.identity("boosted"), ENTITY_EFFECT_TRIGGER)
    val BOOSTED_EFFECT = Supplier<Boolean> {ClientAdvancementContainer.isDone(BOOSTED_ID)}

    private val HARMED_ID = AI.identity("todo")
    val HARMED_TRIGGER = Trigger(AI.identity("harmed"), ENTITY_EFFECT_TRIGGER)
    val HARMED_EFFECT = Supplier<Boolean> {ClientAdvancementContainer.isDone(HARMED_ID)}

    private val HEALTH_ID = AI.identity("todo")
    val HEALTH_TRIGGER = Trigger(AI.identity("health"), ENTITY_EFFECT_TRIGGER)
    val HEALTH = Supplier<Boolean> {ClientAdvancementContainer.isDone(HEALTH_ID)}

    private val SPEED_ID = AI.identity("todo")
    val SPEED_TRIGGER = Trigger(AI.identity("speed"), ENTITY_EFFECT_TRIGGER)
    val SPEED = Supplier<Boolean> {ClientAdvancementContainer.isDone(SPEED_ID)}
    
    private val EXPLODES_ID = AI.identity("todo")
    val EXPLODES_TRIGGER = Trigger(AI.identity("explodes"))
    val EXPLODES = Supplier<Boolean> {ClientAdvancementContainer.isDone(EXPLODES_ID)}

    private val SHOOT_ITEM_ID = AI.identity("todo")
    val SHOOT_ITEM_TRIGGER = Trigger(AI.identity("shoot_item"))
    val SHOOT_ITEM = Supplier<Boolean> {ClientAdvancementContainer.isDone(SHOOT_ITEM_ID)}

    private val BLOCK_ID = AI.identity("todo")
    val BLOCK_TRIGGER = Trigger(AI.identity("block"))
    val BLOCK = Supplier<Boolean> {ClientAdvancementContainer.isDone(BLOCK_ID)}

    private val PROJECTILE_ID = AI.identity("todo")
    val PROJECTILE_TRIGGER = Trigger(AI.identity("projectile"))
    val PROJECTILE = Supplier<Boolean> {ClientAdvancementContainer.isDone(PROJECTILE_ID)}

    private val CHICKEN_ID = AI.identity("todo")
    val CHICKEN_TRIGGER = Trigger(AI.identity("chicken"))
    val CHICKEN = Supplier<Boolean> {ClientAdvancementContainer.isDone(CHICKEN_ID)}

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

    fun Supplier<Boolean>.or(other: Supplier<Boolean>): Supplier<Boolean>{
        return Supplier<Boolean>{ this.get() || other.get() }
    }
    fun Supplier<Boolean>.and(other: Supplier<Boolean>): Supplier<Boolean>{
        return Supplier<Boolean>{ this.get() && other.get() }
    }

    class Trigger(val id: Identifier, vararg val children: Trigger)

}
