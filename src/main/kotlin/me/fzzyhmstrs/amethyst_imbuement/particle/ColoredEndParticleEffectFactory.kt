package me.fzzyhmstrs.amethyst_imbuement.particle

import com.mojang.brigadier.StringReader
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType

class ColoredEndParticleEffectFactory: ParticleEffect.Factory<ColoredEndParticleEffect> {
    override fun read(type: ParticleType<ColoredEndParticleEffect>, reader: StringReader): ColoredEndParticleEffect {
        reader.expect(' ')
        val f = reader.readFloat()
        reader.expect(' ')
        val g = reader.readFloat()
        reader.expect(' ')
        val h = reader.readFloat()
        reader.expect(' ')
        val scale: Float = reader.readFloat()
        return ColoredEndParticleEffect(f, g, h, scale)
    }

    override fun read(type: ParticleType<ColoredEndParticleEffect>, buf: PacketByteBuf): ColoredEndParticleEffect {
        val f = buf.readFloat()
        val g = buf.readFloat()
        val h = buf.readFloat()
        val scale = buf.readFloat()
        return ColoredEndParticleEffect(f, g, h, scale)
    }
}