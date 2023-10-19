package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
    @Accessor
    boolean isInTeleportationState();

    @Accessor
    void setInTeleportationState(boolean inTeleportationState);
}
