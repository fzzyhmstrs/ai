package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.advancement.ClientAdvancementContainer
import me.fzzyhmstrs.amethyst_core.advancement.FeatureCriteria
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.function.Supplier

object SpellAdvancementChecks {

    //the base advancement of damage,amplifier,duration,range,cooldown,mana cost
    private val STAT_ID = Identifier(AI.MOD_ID,"todo")
    val STAT_TRIGGER = Identifier(AI.MOD_ID,"stat")
    val STAT = Supplier<Boolean> {ClientAdvancementContainer.isDone(STAT_ID)}

    private val DAMAGE_ID = Identifier(AI.MOD_ID,"todo")
    val DAMAGE_TRIGGER = Identifier(AI.MOD_ID,"damage")
    val DAMAGE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DAMAGE_ID)}

    private val AMPLIFIER_ID = Identifier(AI.MOD_ID,"todo")
    val AMPLIFIER_TRIGGER = Identifier(AI.MOD_ID,"amplifier")
    val AMPLIFIER = Supplier<Boolean> {ClientAdvancementContainer.isDone(AMPLIFIER_ID)}

    private val DURATION_ID = Identifier(AI.MOD_ID,"todo")
    val DURATION_TRIGGER = Identifier(AI.MOD_ID,"duration")
    val DURATION = Supplier<Boolean> {ClientAdvancementContainer.isDone(DURATION_ID)}

    private val RANGE_ID = Identifier(AI.MOD_ID,"todo")
    val RANGE_TRIGGER = Identifier(AI.MOD_ID,"range")
    val RANGE = Supplier<Boolean> {ClientAdvancementContainer.isDone(RANGE_ID)}

    private val COOLDOWN_ID = Identifier(AI.MOD_ID,"todo")
    val COOLDOWN_TRIGGER = Identifier(AI.MOD_ID,"cooldown")
    val COOLDOWN = Supplier<Boolean> {ClientAdvancementContainer.isDone(COOLDOWN_ID)}

    private val MANA_COST_ID = Identifier(AI.MOD_ID,"todo")
    val MANA_COST_TRIGGER = Identifier(AI.MOD_ID,"mana_cost")
    val MANA_COST = Supplier<Boolean> {ClientAdvancementContainer.isDone(MANA_COST_ID)}

    private val DAMAGE_SOURCE_ID = Identifier(AI.MOD_ID,"todo")
    val DAMAGE_SOURCE_TRIGGER = Identifier(AI.MOD_ID,"damage")
    val DAMAGE_SOURCE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DAMAGE_SOURCE_ID)}

    private val DOUBLE_ID = Identifier(AI.MOD_ID,"todo")
    val DOUBLE_TRIGGER = Identifier(AI.MOD_ID,"stunned")
    val DOUBLE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DOUBLE_ID)}

    private val UNIQUE_ID = Identifier(AI.MOD_ID,"todo")
    val UNIQUE_TRIGGER = Identifier(AI.MOD_ID,"unique")
    val UNIQUE = Supplier<Boolean> {ClientAdvancementContainer.isDone(UNIQUE_ID)}

    private val LIGHTNING_ID = Identifier(AI.MOD_ID,"todo")
    val LIGHTNING_TRIGGER = Identifier(AI.MOD_ID,"lightning")
    val LIGHTNING = Supplier<Boolean> {ClientAdvancementContainer.isDone(LIGHTNING_ID)}

    private val FLAME_ID = Identifier(AI.MOD_ID,"todo")
    val FLAME_TRIGGER = Identifier(AI.MOD_ID,"flame")
    val FLAME = Supplier<Boolean> {ClientAdvancementContainer.isDone(FLAME_ID)}

    private val ICE_ID = Identifier(AI.MOD_ID,"todo")
    val ICE_TRIGGER = Identifier(AI.MOD_ID,"ice")
    val ICE = Supplier<Boolean> {ClientAdvancementContainer.isDone(ICE_ID)}

    private val STUNNED_ID = Identifier(AI.MOD_ID,"todo")
    val STUNNED_TRIGGER = Identifier(AI.MOD_ID,"stunned")
    val STUNS = Supplier<Boolean> {ClientAdvancementContainer.isDone(STUNNED_ID)}

    private val SUMMONS_ID = Identifier(AI.MOD_ID,"todo")
    val SUMMONS_TRIGGER = Identifier(AI.MOD_ID,"summons")
    val SUMMONS = Supplier<Boolean> {ClientAdvancementContainer.isDone(SUMMONS_ID)}

    private val EXPLODES_ID = Identifier(AI.MOD_ID,"todo")
    val EXPLODES_TRIGGER = Identifier(AI.MOD_ID,"explodes")
    val EXPLODES = Supplier<Boolean> {ClientAdvancementContainer.isDone(EXPLODES_ID)}


    fun or(a: Supplier<Boolean>, b: Supplier<Boolean>): Supplier<Boolean>{
        return Supplier<Boolean> {a.get() || b.get()}
    }

    fun grant(player: ServerPlayerEntity, id: Identifier){
        FeatureCriteria.PAIRED_FEATURE.trigger(player,id)
    }


}