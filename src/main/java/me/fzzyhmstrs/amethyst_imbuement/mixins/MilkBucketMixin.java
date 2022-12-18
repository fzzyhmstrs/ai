package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MilkBucketItem.class)
public class MilkBucketMixin {

    /*@Redirect(method = "finishUsing", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void amethyst_imbuement_infinityNonDecrement(ItemStack instance, int amount){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, instance);
        if (!(level > 0)) {
            instance.decrement(amount);
        }
    }*/

    @WrapOperation(method = "finishUsing", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void amethyst_imbuement_infinityNonDecrement(ItemStack instance, int amount, Operation<Void> operation){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, instance);
        if (!(level > 0)) {
            operation.call(instance, amount);
        }
    }
}
