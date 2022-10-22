package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.LureEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LureEnchantment.class)
public abstract class EnchantLureMixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(4);
    }
}
