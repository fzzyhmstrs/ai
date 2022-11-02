package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.LuckEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LuckEnchantment.class)
public abstract class EnchantLuckMixin {

    @ModifyReturnValue(method = "getMaxPower", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxPowerToSixty(int original){
        return 60;
    }
}
