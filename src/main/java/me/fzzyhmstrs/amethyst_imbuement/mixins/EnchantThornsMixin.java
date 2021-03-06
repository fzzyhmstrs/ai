package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.random.Random;

@Mixin(ThornsEnchantment.class)
public abstract class EnchantThornsMixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
    }

    @Inject(method = "getDamageAmount", at = @At(value = "HEAD"), cancellable = true)
    private static void getDamageAmount(int level, Random random, CallbackInfoReturnable<Integer> cir){
        if (level == 4) {
            cir.setReturnValue(2 + random.nextInt(4));
        } else if(level == 5) {
            cir.setReturnValue(3 + random.nextInt(4));
        }
    }
}
