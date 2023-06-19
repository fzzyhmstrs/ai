package me.fzzyhmstrs.amethyst_imbuement.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterParticle
import net.minecraft.particle.ParticleType
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.Vec3f

class ColoredEndParticleType: ParticleType<ColoredEndParticleEffect>(false, RegisterParticle.COLORED_END_ROD_EFFECT_FACTORY) {

    private val CODEC = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ColoredEndParticleEffect> ->
        instance.group(
            Vec3f.CODEC.fieldOf("color").forGetter { effect -> effect.color },
            Codec.FLOAT.fieldOf("scale").forGetter { effect -> effect.scale }
        ).apply(instance){color, scale -> ColoredEndParticleEffect(color, scale)} }

    override fun getCodec(): Codec<ColoredEndParticleEffect> {
        return CODEC
    }
}