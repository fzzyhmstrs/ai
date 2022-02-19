package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.PowerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowerEnchantment.class)
public abstract class EnchantPowerMixin {



    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(6);
    }

    @Inject(method = "getMinPower", at = @At(value = "HEAD"), cancellable = true)
    public void getMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        if (level == 6) {
            cir.setReturnValue(61);
        }
    }
}
