package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Shadow @Final ScreenHandlerType<?> type;

    @Inject(method="getType",at=@At(value = "HEAD"), cancellable = true)
    private void getTypeMixin(CallbackInfoReturnable<ScreenHandlerType<?>> cir){
        if (this.type == null) {
            cir.setReturnValue(null);
        }
    }
}
