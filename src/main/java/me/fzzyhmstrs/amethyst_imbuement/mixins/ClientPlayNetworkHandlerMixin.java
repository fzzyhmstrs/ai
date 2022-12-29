package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.screen.ImbuingRecipeBookScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    /*@Inject(method = "onSynchronizeRecipes", at = @At("TAIL"))
    private void amethyst_imbuement_syncImbuingRecipesForBook(SynchronizeRecipesS2CPacket packet, CallbackInfo ci){
        ImbuingRecipeBookScreen.RecipeContainer.registerClient();
    }*/

    @Inject(method = "getActiveTotemOfUndying", at = @At(value = "TAIL"), cancellable = true)
    private static void amethyst_imbuement_checkForTotemOfAmethyst(PlayerEntity player, CallbackInfoReturnable<ItemStack> cir){
        player.getInventory().main.forEach(stack->{
            if (stack.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST())){
                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(), stack) > 0){
                    cir.setReturnValue(stack);
                }
            }
        });
        player.getInventory().offHand.forEach(stack-> {
            if (stack.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST())) {
                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(), stack) > 0) {
                    cir.setReturnValue(stack);
                }
            }
        });
    }
}
