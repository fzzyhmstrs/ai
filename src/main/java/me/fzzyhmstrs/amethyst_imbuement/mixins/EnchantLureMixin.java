package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.LureEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LureEnchantment.class)
public abstract class EnchantLureMixin {

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int amethyst_imbuement_updateMaxLevelToFour(int original){
        return AiConfig.INSTANCE.getEnchants().getVanillaMaxLevel((Enchantment)(Object)this,4);
    }
}
