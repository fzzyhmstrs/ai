package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleEffectFactory
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleFactory
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleType
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry

object RegisterParticle {

    val COLORED_END_ROD_EFFECT_FACTORY = ColoredEndParticleEffectFactory()
    val COLORED_END_ROD_TYPE = ColoredEndParticleType()

    fun registerParticleTypes(){
        FzzyPort.PARTICLE_TYPE.register(AI.identity("colored_end_rod"), COLORED_END_ROD_TYPE)
    }

    @Environment(value = EnvType.CLIENT)
    fun registerParticleFactories(){
        ParticleFactoryRegistry.getInstance().register(COLORED_END_ROD_TYPE){spriteProvider -> ColoredEndParticleFactory(spriteProvider)}
    }

}