package me.fzzyhmstrs.amethyst_imbuement.particle

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterParticle
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor
import org.joml.Vector3f
import java.util.*

class ColoredEndParticleEffect(val color: Vector3f, val scale: Float): ParticleEffect {

    constructor(r: Int, g: Int, b: Int, scale: Float = 1f): this(Vector3f(r/255f,g/255f,b/255f),scale)
    constructor(dyeColor: DyeColor, scale: Float = 1f):this(Vector3f(dyeColor.colorComponents),scale)

    override fun getType(): ParticleType<*> {
        return RegisterParticle.COLORED_END_ROD_TYPE
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeFloat(color.x())
        buf.writeFloat(color.y())
        buf.writeFloat(color.z())
        buf.writeFloat(scale)
    }

    override fun asString(): String {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.type), color.x(), color.y(), color.z(), scale)
    }
}