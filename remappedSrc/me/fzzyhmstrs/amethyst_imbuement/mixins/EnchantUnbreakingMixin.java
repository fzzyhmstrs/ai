package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.UnbreakingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnbreakingEnchantment.class)
public abstract class EnchantUnbreakingMixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
    }


    @Inject(method = "getMinPower", at = @At(value = "HEAD"), cancellable = true)
    public void getMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        if (level > 3) {
            cir.setReturnValue(21 + (level - 3) * 16);
        }
    }
}
