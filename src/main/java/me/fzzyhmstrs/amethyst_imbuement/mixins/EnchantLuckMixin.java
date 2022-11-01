package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.LuckEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LuckEnchantment.class)
public abstract class EnchantLuckMixin {

    @Inject(method = "getMaxPower", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(60);
    }
}
