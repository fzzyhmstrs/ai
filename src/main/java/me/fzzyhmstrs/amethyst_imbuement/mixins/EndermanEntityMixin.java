package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {

    @Inject(method = "isPlayerStaring", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_isPlayerWearingFriendly(PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if (RegisterEnchantment.INSTANCE.getFRIENDLY().specialEffect(player,1,ItemStack.EMPTY)){
            cir.setReturnValue(false);
        }
    }
}
