package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.QuickChargeEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuickChargeEnchantment.class)
public abstract class EnchantQuickChargeMixin {

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxLevelToFour(int original){
        return AiConfig.INSTANCE.getEnchants().getVanillaMaxLevel((Enchantment)(Object)this,4);
    }

    @Inject(method = "getMaxPower", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(80);
    }
}
