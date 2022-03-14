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

    //private ItemStack bucketStack;


    @Redirect(method = "finishUsing", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void infinityNonDecrement(ItemStack instance, int amount){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, instance);
        if (!(level > 0)) {
            instance.decrement(amount);
        }
    }



    /*@Inject(method = "finishUsing", at = @At(value = "HEAD"))
    private void getStackForInfinity(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        bucketStack = stack;
    }

    @Inject(method = "finishUsing", at = @At(value = "RETURN"), cancellable = true)
    private void finishUsingInfinity(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        if (stack.isEmpty()){
            int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, bucketStack);
            if (level > 0){
                ItemStack tempStack = new ItemStack(Items.BUCKET);
                Map<Enchantment, Integer> map = new HashMap<>();
                map.put(Enchantments.INFINITY,1);
                EnchantmentHelper.set(map,tempStack);
                cir.setReturnValue(tempStack);
            }
        }
    }*/

}
