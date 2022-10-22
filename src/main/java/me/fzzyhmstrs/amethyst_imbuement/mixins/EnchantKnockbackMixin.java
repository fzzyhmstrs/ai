package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.KnockbackEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KnockbackEnchantment.class)
public abstract class EnchantKnockbackMixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
    }
}
