package me.fzzyhmstrs.amethyst_imbuement.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterParticle
import net.minecraft.particle.ParticleType

class ColoredEndParticleType: ParticleType<ColoredEndParticleEffect>(false, RegisterParticle.COLORED_END_ROD_EFFECT_FACTORY) {

    private val CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ColoredEndParticleEffect> ->
        instance.group(
            Codec.FLOAT.fieldOf("r").forGetter { effect -> effect.r },
            Codec.FLOAT.fieldOf("g").forGetter { effect -> effect.r },
            Codec.FLOAT.fieldOf("b").forGetter { effect -> effect.r },
            Codec.FLOAT.fieldOf("scale").forGetter { effect -> effect.scale }
        ).apply(instance){r, g, b, scale -> ColoredEndParticleEffect(r, g, b, scale)} }

    override fun getCodec(): Codec<ColoredEndParticleEffect> {
        return CODEC
    }
}