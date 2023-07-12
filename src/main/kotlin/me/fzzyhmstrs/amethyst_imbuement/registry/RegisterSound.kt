package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object RegisterSound {

    private val HAMSTER_HIT_ID = AI.identity("hamster_hit")
    val HAMSTER_HIT = soundEvent(HAMSTER_HIT_ID)

    private val HAMSTER_DIE_ID = AI.identity("hamster_die")
    val HAMSTER_DIE = soundEvent(HAMSTER_DIE_ID)

    private val HAMSTER_AMBIENT_ID = AI.identity("hamster_ambient")
    val HAMSTER_AMBIENT = soundEvent(HAMSTER_AMBIENT_ID)

    private fun soundEvent(identifier: Identifier): SoundEvent{
        return Registry.register(Registries.SOUND_EVENT,identifier, SoundEvent.of(identifier))
    }

    fun registerAll(){

    }


}