package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin  {

    private static AnvilScreenHandler handler2;
    private static int playerLvl;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void constructor(AnvilScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci){
        handler2 = handler;
        playerLvl = inventory.player.experienceLevel;
    }

    @Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "net/minecraft/client/font/TextRenderer.drawWithShadow (Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int ignoreTooExpensiveText(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color){
        Text text2 = new TranslatableText("container.repair.cost", (handler2).getLevelCost());
        int t;
        if (playerLvl >= handler2.getLevelCost()){
            t = 8453920;
        } else {
            t = 0xFF6060;
        }
        int k = 176 - 8 - instance.getWidth(text2) - 2;
        AnvilScreen.fill(matrices, k - 2, 67, (int)x-2, 79, 0x4F000000);
        return instance.drawWithShadow(matrices, text2, k, 69.0f, t);
    }
}
