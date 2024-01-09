package me.fzzyhmstrs.amethyst_imbuement.particle

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterParticle
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.util.DyeColor
import java.util.*

class ColoredEndParticleEffect(val r: Float, val g: Float, val b: Float, val scale: Float): ParticleEffect {

    constructor(r: Int, g: Int, b: Int, scale: Float = 1f): this(r/255f,g/255f,b/255f,scale)
    constructor(dyeColor: DyeColor, scale: Float = 1f): this(dyeColor.colorComponents[0],dyeColor.colorComponents[1],dyeColor.colorComponents[2],scale)

    override fun getType(): ParticleType<*> {
        return RegisterParticle.COLORED_END_ROD_TYPE
    }

    override fun write(buf: PacketByteBuf) {
        buf.writeFloat(r)
        buf.writeFloat(g)
        buf.writeFloat(b)
        buf.writeFloat(scale)
    }

    override fun asString(): String {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", FzzyPort.PARTICLE_TYPE.getId(this.type), r, g, b, scale)
    }
}