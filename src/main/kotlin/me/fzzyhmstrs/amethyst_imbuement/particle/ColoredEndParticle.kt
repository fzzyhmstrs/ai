package me.fzzyhmstrs.amethyst_imbuement.particle

import net.minecraft.client.particle.AnimatedParticle
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.ColorHelper

class ColoredEndParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    colorR: Float,
    colorG: Float,
    colorB: Float,
    particleScale: Float,
    spriteProvider: SpriteProvider)
    :
    AnimatedParticle(world,x, y, z, spriteProvider,0.0125f)
{

    init{
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        this.red = colorR
        this.green = colorG
        this.blue = colorB
        val f = random.nextFloat() * 0.4f + 0.6f
        this.setTargetColor(ColorHelper.Argb.getArgb(255,(255 * darken(colorR,f)).toInt(),(255 * darken(colorG,f)).toInt(),(255 * darken(colorB,f)).toInt()))
        this.scale *= 0.75f * particleScale
        maxAge = 60 + random.nextInt(12)
        this.setSpriteForAge(spriteProvider)
    }

    override fun move(dx: Double, dy: Double, dz: Double) {
        boundingBox = boundingBox.offset(dx, dy, dz)
        repositionFromBoundingBox()
    }

    private fun darken(colorComponent: Float, multiplier: Float): Float {
        return (random.nextFloat() * 0.2f + 0.8f) * colorComponent * multiplier
    }

}