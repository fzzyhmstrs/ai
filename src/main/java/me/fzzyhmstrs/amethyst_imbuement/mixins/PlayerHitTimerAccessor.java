package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface PlayerHitTimerAccessor {
    @Accessor(value = "playerHitTimer")
    void setPlayerHitTimer(int value);
}
