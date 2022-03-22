package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.fzzyhmstrs.amethyst_imbuement.AI;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
   //@Inject(method="<init>",at = @At(value = "TAIL"))
    //private void HandledScreen(ScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
       // hndlr = (T) handler;
    //}

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

    /*@Inject(method="isClickOutsideBounds", at=@At(value="HEAD"), cancellable = true)
    private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir){
        if (handler != null) {
            if (handler.getType() == RegisterHandler.INSTANCE.getIMBUING_SCREEN_HANDLER()){
                cir.setReturnValue(mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight));
            }
        }
    }*/

}
