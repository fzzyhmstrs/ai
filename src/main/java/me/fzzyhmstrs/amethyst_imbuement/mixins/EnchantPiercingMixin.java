package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.PiercingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiercingEnchantment.class)
public abstract class EnchantPiercingMixin {

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxLevelToSix(int original){
        return AiConfig.INSTANCE.getEnchants().getVanillaMaxLevel((Enchantment)(Object)this,6);
    }

    @ModifyReturnValue(method = "getMaxPower", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxPower(int original, int level){
        int i = 0;
        if (level == 5) i = 10;
        if (level == 6) i = 20;
        return 50+i;
    }
}
