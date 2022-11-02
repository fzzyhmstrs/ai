package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxLevelToFive(int original){
        return 5;
    }

}
