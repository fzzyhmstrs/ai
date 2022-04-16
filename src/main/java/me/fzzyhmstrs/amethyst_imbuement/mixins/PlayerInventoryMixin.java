package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.AI;
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem;
import me.fzzyhmstrs.amethyst_imbuement.item.SniperBowItem;
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final
    public PlayerEntity player;


    //@Shadow public abstract void scrollInHotbar(double scrollAmount);

    @Inject(at = @At("HEAD"), method = "scrollInHotbar", cancellable = true)
    private void scrollInHotbar(double scrollAmount, CallbackInfo ci) {
        //System.out.println(player.getStackInHand(Hand.MAIN_HAND).getItem().toString());
        if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScepterItem){
            if (player.getPose().equals(EntityPose.CROUCHING)){
                ScepterObject.INSTANCE.updateScepterActiveEnchant(player.getStackInHand(Hand.MAIN_HAND),player,scrollAmount < 0.0D);
                ci.cancel();
            }
        }

    }


}
