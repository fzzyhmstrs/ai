package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.item.scepter.ScepterItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "isAcceptableItem", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_checkScepterItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        Item item = stack.getItem();
        if (item instanceof ScepterItem) {
            if ((Object) this instanceof MendingEnchantment){
                cir.setReturnValue(false);
            }
            if ((Object) this instanceof UnbreakingEnchantment){
                cir.setReturnValue(false);
            }
        }
        if (item instanceof BucketItem || item instanceof MilkBucketItem || item instanceof PowderSnowBucketItem){
            if ((Object) this instanceof InfinityEnchantment){
                cir.setReturnValue(true);
            }
        }
        if (item instanceof PowderSnowBucketItem || item instanceof EntityBucketItem){
            if ((Object) this instanceof InfinityEnchantment){
                cir.setReturnValue(true);
            }
        }
    }

}
