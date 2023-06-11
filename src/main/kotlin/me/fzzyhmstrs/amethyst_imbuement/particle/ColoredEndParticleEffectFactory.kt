package me.fzzyhmstrs.amethyst_imbuement.particle

import com.mojang.brigadier.StringReader
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.AbstractDustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import org.joml.Vector3f

class ColoredEndParticleEffectFactory: ParticleEffect.Factory<ColoredEndParticleEffect> {
    override fun read(type: ParticleType<ColoredEndParticleEffect>, reader: StringReader): ColoredEndParticleEffect {
        val color: Vector3f = AbstractDustParticleEffect.readColor(reader)
        reader.expect(' ')
        val scale: Float = reader.readFloat()
        return ColoredEndParticleEffect(color, scale)
    }

    override fun read(type: ParticleType<ColoredEndParticleEffect>, buf: PacketByteBuf): ColoredEndParticleEffect {
        val color = AbstractDustParticleEffect.readColor(buf)
        val scale = buf.readFloat()
        return ColoredEndParticleEffect(color, scale)
    }
}