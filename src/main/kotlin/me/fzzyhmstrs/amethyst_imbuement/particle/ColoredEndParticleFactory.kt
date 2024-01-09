package me.fzzyhmstrs.amethyst_imbuement.particle

import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld

class ColoredEndParticleFactory(private val spriteProvider: SpriteProvider): ParticleFactory<ColoredEndParticleEffect> {
    override fun createParticle(
        parameters: ColoredEndParticleEffect,
        world: ClientWorld,
        x: Double,
        y: Double,
        z: Double,
        velocityX: Double,
        velocityY: Double,
        velocityZ: Double
    ): Particle {
        return ColoredEndParticle(world,x,y,z,velocityX,velocityY,velocityZ,parameters.r,parameters.g,parameters.b,parameters.scale, spriteProvider)
    }
}