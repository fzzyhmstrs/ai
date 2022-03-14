package me.fzzyhmstrs.amethyst_imbuement.depreciated;

import net.minecraft.item.BucketItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(targets = {"net/minecraft/enchantment/EnchantmentTarget$3"})
public abstract class EnchantmentTargetBowMixin {

    /*@Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void bucketInfinityEnchant(Item item, CallbackInfoReturnable<Boolean> cir){
        if (item instanceof BucketItem){
            cir.setReturnValue(true);
        }
    }*/
}
