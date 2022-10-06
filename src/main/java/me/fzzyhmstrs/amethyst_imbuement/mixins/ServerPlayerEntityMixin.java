package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at= @At(value = "HEAD"))
    private void copyInventoryIfSoulBound(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci){
        if (oldPlayer.hasStatusEffect(RegisterStatus.INSTANCE.getSOULBINDING())){
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            player.getInventory().clone(oldPlayer.getInventory());
        }
    }

}
