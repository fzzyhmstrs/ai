package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.PiercingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiercingEnchantment.class)
public abstract class EnchantPiercingMixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(6);
    }

    @Inject(method = "getMaxPower", at = @At(value = "HEAD"), cancellable = true)
    private void getMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        int i = 0;
        if (level == 5) i = 10;
        if (level == 6) i = 20;
        cir.setReturnValue(50+i);
    }
}
