package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(Bucketable.class)
public interface BucketableMixin {

    @Redirect(method = "tryBucket", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemUsage.exchangeStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack fishBucketInfinity(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, boolean creativeOverride){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY,inputStack);
        ItemStack tempStack = ItemUsage.exchangeStack(inputStack,player,outputStack, creativeOverride);
        if (level > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            EnchantmentHelper.set(map,tempStack);
        }
        return tempStack;

    }


}
