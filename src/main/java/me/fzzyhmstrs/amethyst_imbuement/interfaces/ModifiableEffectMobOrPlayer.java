package me.fzzyhmstrs.amethyst_imbuement.interfaces;

import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext;
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface ModifiableEffectMobOrPlayer {

    void amethyst_imbuement_run(Identifier type, Entity entity, @Nullable Entity owner, ProcessContext processContext);
    void amethyst_imbuement_addTemporaryEffect(Identifier type, ModifiableEffect effect, int lifespan);
}
