package me.fzzyhmstrs.amethyst_imbuement.interfaces;

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect;
import net.minecraft.util.Identifier;

public interface ModifiableEffectMobOrPlayer {
    void amethyst_imbuement_addTemporaryEffect(Identifier type, ModifiableEffect effect, int lifespan);
}
