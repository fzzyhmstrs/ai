package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.PowderSnowBucketItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(PowderSnowBucketItem.class)
public class PowderSnowBucketItemMixin {

    private int level = 0;

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"))
    private void getStackForInfinity(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity != null) {
            ItemStack bucketStack = playerEntity.getStackInHand(context.getHand());
            level = EnchantmentHelper.getLevel(Enchantments.INFINITY, bucketStack);
        }
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.setStackInHand (Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    private void setInfinityStackInHand(PlayerEntity instance, Hand hand, ItemStack itemStack){
        /*ItemStack inputStack = instance.getStackInHand(hand);
        System.out.println(inputStack);
        System.out.println(inputStack.getEnchantments());*/
        ItemStack outputStack = Items.BUCKET.getDefaultStack();
        if (level > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            EnchantmentHelper.set(map,outputStack);
        }
        instance.setStackInHand(hand,outputStack);
    }

}
