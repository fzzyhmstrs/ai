package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundHeight;
    @Shadow protected int backgroundWidth;
    @Final
    @Shadow protected T handler;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method="init",at = @At(value="TAIL"))
    private void initMixin(CallbackInfo ci){
        if (handler != null) {
            if (handler.getType() == RegisterHandler.INSTANCE.getIMBUING_SCREEN_HANDLER()) {
                this.x = (this.width - 234) / 2;
                this.y = (this.height - 174) / 2;
                this.backgroundWidth = 234;
                this.backgroundHeight = 174;
            }
        }
    }
}
