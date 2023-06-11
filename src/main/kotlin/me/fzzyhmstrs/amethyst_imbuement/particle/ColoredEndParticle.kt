package me.fzzyhmstrs.amethyst_imbuement.particle

import net.minecraft.client.particle.AnimatedParticle
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.ColorHelper
import org.joml.Vector3f

class ColoredEndParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    color:
    Vector3f,
    particleScale: Float,
    spriteProvider: SpriteProvider)
    :
    AnimatedParticle(world,x, y, z, spriteProvider,0.0125f)
{

    init{
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        this.red = color.x
        this.green = color.y
        this.blue = color.z
        val f = random.nextFloat() * 0.4f + 0.6f
        this.setTargetColor(ColorHelper.Argb.getArgb(255,(255 * darken(color.x,f)).toInt(),(255 * darken(color.y,f)).toInt(),(255 * darken(color.z,f)).toInt()))
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