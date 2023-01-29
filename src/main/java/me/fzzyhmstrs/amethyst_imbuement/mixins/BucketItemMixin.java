package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Shadow @Final private Fluid fluid;

    /*@Redirect(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemUsage.exchangeStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack amethyst_imbuement_infinityBucketFill(ItemStack inputStack, PlayerEntity player, ItemStack outputStack){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY,inputStack);
        ItemStack tempStack = ItemUsage.exchangeStack(inputStack,player,outputStack);
        if (level > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            EnchantmentHelper.set(map,tempStack);
        }
        return tempStack;
    }*/

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemUsage.exchangeStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack amethyst_imbuement_infinityBucketFill(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, Operation<ItemStack> operation){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY,inputStack);
        ItemStack tempStack = operation.call(inputStack, player, outputStack);
        if (level > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            EnchantmentHelper.set(map,tempStack);
        }
        return tempStack;
    }

    /*@Redirect(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/item/BucketItem.getEmptiedStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack amethyst_imbuement_infinityBucket(ItemStack stack, PlayerEntity player){
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY,stack) > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            ItemStack tempStack;
            if (this.fluid == Fluids.WATER){
                if ((BucketItem)(Object)this instanceof EntityBucketItem){
                    tempStack = BucketItem.getEmptiedStack(stack, player);
                } else {
                    tempStack = stack;
                }
            } else {
                tempStack = BucketItem.getEmptiedStack(stack, player);
            }
            EnchantmentHelper.set(map,tempStack);
            return tempStack;
        }
        return BucketItem.getEmptiedStack(stack,player);
    }*/

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/item/BucketItem.getEmptiedStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack amethyst_imbuement_infinityBucket(ItemStack stack, PlayerEntity player, Operation<ItemStack> operation){
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY,stack) > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            ItemStack tempStack;
            if (this.fluid == Fluids.WATER){
                if ((BucketItem)(Object)this instanceof EntityBucketItem){
                    tempStack = operation.call(stack,player);
                } else {
                    tempStack = stack;
                }
            } else {
                tempStack = operation.call(stack,player);
            }
            EnchantmentHelper.set(map,tempStack);
            return tempStack;
        }
        return operation.call(stack,player);
    }

}
