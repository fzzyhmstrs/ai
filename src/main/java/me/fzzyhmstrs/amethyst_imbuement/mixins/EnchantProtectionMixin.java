package me.fzzyhmstrs.amethyst_imbuement.mixins;


import net.minecraft.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public abstract class EnchantProtectionMixin {

    @Shadow @Final public ProtectionEnchantment.Type protectionType;

    //making the level 5 protections slightly harder to get
    @Inject(method = "getMinPower", at = @At(value = "HEAD"), cancellable = true)
    public void amethyst_imbuement_getMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        if (level > 4) {
            cir.setReturnValue(this.protectionType.getBasePower() + (level - 1) * this.protectionType.getPowerPerLevel() + 12);
        }
    }
}
