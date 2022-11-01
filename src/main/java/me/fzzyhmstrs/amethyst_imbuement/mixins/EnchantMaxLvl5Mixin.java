package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FireAspectEnchantment.class,
        KnockbackEnchantment.class,
        LoyaltyEnchantment.class,
        LuckEnchantment.class,
        ProtectionEnchantment.class,
        RespirationEnchantment.class,
        SweepingEnchantment.class,
        ThornsEnchantment.class,
        UnbreakingEnchantment.class})
public abstract class EnchantMaxLvl5Mixin {

    @Inject(method = "getMaxLevel", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
    }

}
