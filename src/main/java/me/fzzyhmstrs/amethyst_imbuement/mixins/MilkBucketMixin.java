package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MilkBucketItem.class)
public class MilkBucketMixin {

    @Redirect(method = "finishUsing", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void amethyst_imbuement_infinityNonDecrement(ItemStack instance, int amount){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, instance);
        if (!(level > 0)) {
            instance.decrement(amount);
        }
    }
}
