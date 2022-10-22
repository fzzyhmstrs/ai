package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(CowEntity.class)
public class CowEntityMixin {

    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemUsage.exchangeStack (Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack amethyst_imbuement_milkBucketInfinity(ItemStack inputStack, PlayerEntity player, ItemStack outputStack){
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY,inputStack);
        ItemStack tempStack = ItemUsage.exchangeStack(inputStack,player,outputStack);
        if (level > 0){
            Map<Enchantment, Integer> map = new HashMap<>();
            map.put(Enchantments.INFINITY,1);
            EnchantmentHelper.set(map,tempStack);
        }
        return tempStack;
    }

}
