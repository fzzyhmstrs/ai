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
        if(player.isUsingSpyglass()) {
            if (player.getPose().equals(EntityPose.CROUCHING) && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
                int si = SniperBowItem.Companion.get_sniper_scope_index();
                int sl = SniperBowItem.Companion.getSNIPER_BOW_SCOPE_LIST_LENGTH();
                if (scrollAmount > 0.0D) {
                    if (si < sl) {
                        SniperBowItem.Companion.set_sniper_scope_index(si + 1);

                    } else {
                        SniperBowItem.Companion.set_sniper_scope_index(0);
                    }
                    SniperBowItem.Companion.setSNIPER_BOW_SCOPE(SniperBowItem.Companion.getSNIPER_BOW_SCOPE_LIST()[SniperBowItem.Companion.get_sniper_scope_index()]);
                }
                if (scrollAmount < 0.0D) {
                    if (si > 0) {
                        SniperBowItem.Companion.set_sniper_scope_index(si - 1);
                    } else {
                        SniperBowItem.Companion.set_sniper_scope_index(sl);
                    }
                    SniperBowItem.Companion.setSNIPER_BOW_SCOPE(SniperBowItem.Companion.getSNIPER_BOW_SCOPE_LIST()[SniperBowItem.Companion.get_sniper_scope_index()]);
                }
                ci.cancel();

            }
        } else if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ScepterItem){
            if (player.getPose().equals(EntityPose.CROUCHING)){
                if (scrollAmount > 0.0D) {
                    ScepterObject.INSTANCE.updateScepterActiveEnchant(player.getStackInHand(Hand.MAIN_HAND),player,false);
                } else if(scrollAmount < 0.0D){
                    ScepterObject.INSTANCE.updateScepterActiveEnchant(player.getStackInHand(Hand.MAIN_HAND),player,true);
                }
                ci.cancel();
            }
        }

    }


}
