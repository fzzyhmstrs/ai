package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.item.ElytraItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net/minecraft/enchantment/EnchantmentTarget$1","net/minecraft/enchantment/EnchantmentTarget$4"})
public abstract class EnchantmentTargetArmorMixin {

    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_elytraArmorEnchant(Item item, CallbackInfoReturnable<Boolean> cir){
        if (item instanceof ElytraItem){
            cir.setReturnValue(true);
        }
        if (item instanceof HorseArmorItem){
            cir.setReturnValue(true);
        }
    }
}
