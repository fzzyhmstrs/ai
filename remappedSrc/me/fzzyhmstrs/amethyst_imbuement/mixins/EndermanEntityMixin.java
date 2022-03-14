package me.fzzyhmstrs.amethyst_imbuement.mixins;


import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {

    @Inject(method = "isPlayerStaring", at = @At(value = "HEAD"), cancellable = true)
    private void isPlayerWearingFriendly(PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if (RegisterEnchantment.INSTANCE.getFRIENDLY().specialEffect(player,1,ItemStack.EMPTY)){
            cir.setReturnValue(false);
        }
    }
}
