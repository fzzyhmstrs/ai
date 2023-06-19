package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleEffectFactory
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleFactory
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleType
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterParticle {

    val COLORED_END_ROD_EFFECT_FACTORY = ColoredEndParticleEffectFactory()
    val COLORED_END_ROD_TYPE = ColoredEndParticleType()

    fun registerParticleTypes(){
        Registry.register(Registry.PARTICLE_TYPE, Identifier(AI.MOD_ID,"colored_end_rod"), COLORED_END_ROD_TYPE)
    }

    fun registerParticleFactories(){
        ParticleFactoryRegistry.getInstance().register(COLORED_END_ROD_TYPE){spriteProvider -> ColoredEndParticleFactory(spriteProvider)}
    }

}