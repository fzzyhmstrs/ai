package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
    public static boolean hasProjectile(ItemStack crossbow, Item projectile){return true;};

    @Inject(method = "getSpeed", at = @At(value = "HEAD"), cancellable = true)
    private static void getSpeed(ItemStack stack, CallbackInfoReturnable<Float> cir){
        int i;
        i = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getDEADLY_SHOT(), stack);
        if (hasProjectile(stack, Items.FIREWORK_ROCKET)) {
            cir.setReturnValue(1.6f + (0.25f * i));
        } else {
            cir.setReturnValue(3.15f + (0.75f * i));
        }
    }
}
