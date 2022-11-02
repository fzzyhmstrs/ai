package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.PowerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowerEnchantment.class)
public abstract class EnchantPowerMixin {

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxLevelToSix(int original){
        return 6;
    }

    @Inject(method = "getMinPower", at = @At(value = "HEAD"), cancellable = true)
    public void amethyst_imbuement_getMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        if (level == 6) {
            cir.setReturnValue(61);
        }
    }
}
