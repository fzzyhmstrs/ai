package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object RegisterSound {

    val HAMSTER_HIT = soundEvent(Identifier(AI.MOD_ID,"hamster_hit"))

    val HAMSTER_DIE = soundEvent(Identifier(AI.MOD_ID,"hamster_die"))

    val HAMSTER_AMBIENT = soundEvent(Identifier(AI.MOD_ID,"hamster_ambient"))

    val UNLOCK = soundEvent(Identifier(AI.MOD_ID,"unlock"))

    val LOCKED_BOOK = soundEvent(Identifier(AI.MOD_ID,"locked_book"))

    val EXECUTE = soundEvent(Identifier(AI.MOD_ID,"execute"))

    val ICE_SPIKES = soundEvent(Identifier(AI.MOD_ID,"ice_spikes"))

    val SOLAR_FLARE_CHARGE = soundEvent(Identifier(AI.MOD_ID,"solar_flare_charge"))

    val SOLAR_FLARE_FIRE = soundEvent(Identifier(AI.MOD_ID,"solar_flare_fire"))

    val ENERGY_BLADE = soundEvent(Identifier(AI.MOD_ID,"energy_blade"))

    val STOMP = soundEvent(Identifier(AI.MOD_ID,"stomp"))

    val ELEMENTAL_RUMBLE = soundEvent(Identifier(AI.MOD_ID,"elemental_rumble"))

    val ELEMENTAL_DEATH = soundEvent(Identifier(AI.MOD_ID,"elemental_death"))

    val ELEMENTAL_HURT = soundEvent(Identifier(AI.MOD_ID,"elemental_hurt"))

    val FRAGMENT_CRUMBLES = soundEvent(Identifier(AI.MOD_ID,"fragment_crumbles"))

    private fun soundEvent(identifier: Identifier): SoundEvent{
        return Registry.register(Registries.SOUND_EVENT,identifier, SoundEvent.of(identifier))
    }

    fun registerAll(){

    }


}