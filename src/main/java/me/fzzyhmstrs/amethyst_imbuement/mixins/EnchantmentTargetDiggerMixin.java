package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterScepter;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$12")
public abstract class EnchantmentTargetDiggerMixin {

    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_buildersScepterEnchant(Item item, CallbackInfoReturnable<Boolean> cir){
        if (item == RegisterScepter.INSTANCE.getBUILDERS_SCEPTER()){
            cir.setReturnValue(true);
        }
    }
}
