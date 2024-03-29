package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Shadow
    public static boolean hasProjectile(ItemStack crossbow, Item projectile){return true;}

    @Inject(method = "getSpeed", at = @At(value = "HEAD"), cancellable = true)
    private static void amethyst_imbuement_getSpeed(ItemStack stack, CallbackInfoReturnable<Float> cir){
        int i;
        i = RegisterEnchantment.INSTANCE.getDEADLY_SHOT().getLevel(stack);
        if (i > 0) {
            if (hasProjectile(stack, Items.FIREWORK_ROCKET)) {
                cir.setReturnValue(RegisterEnchantment.INSTANCE.getDEADLY_SHOT().getSpeed(i, true, false));
            } else {
                cir.setReturnValue(RegisterEnchantment.INSTANCE.getDEADLY_SHOT().getSpeed(i, false, false));
            }
        }
    }
}
