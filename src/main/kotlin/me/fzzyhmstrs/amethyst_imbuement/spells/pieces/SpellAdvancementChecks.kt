package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.advancement.ClientAdvancementContainer
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.function.Supplier

object SpellAdvancementChecks {

    val DOUBLE_ID = Identifier(AI.MOD_ID,"todo")
    val DOUBLE_TRIGGER = Identifier(AI.MOD_ID,"stunned")
    val DOUBLE = Supplier<Boolean> {ClientAdvancementContainer.isDone(DOUBLE_ID)}

    val LIGHTNING_ID = Identifier(AI.MOD_ID,"todo")
    val LIGHTNING_TRIGGER = Identifier(AI.MOD_ID,"lightning")
    val LIGHTNING = Supplier<Boolean> {ClientAdvancementContainer.isDone(LIGHTNING_ID)}

    val STUNNED_ID = Identifier(AI.MOD_ID,"todo")
    val STUNNED_TRIGGER = Identifier(AI.MOD_ID,"stunned")
    val STUNNED = Supplier<Boolean> {ClientAdvancementContainer.isDone(STUNNED_ID)}



    fun grant(player: ServerPlayerEntity, id: Identifier){
        ClientAdvancementContainer.PAIRED_FEATURE.trigger(player,id)
    }


}