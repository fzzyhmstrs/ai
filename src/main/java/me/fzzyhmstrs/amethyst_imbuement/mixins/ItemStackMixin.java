package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 3000)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract NbtList getEnchantments();

    //credit for this mixin (C) Timefall Development, Chronos Sacaria, Kluzzio
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void amethyst_imbuement_onUsingItemWhilstStunned(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        if (RegisterStatus.INSTANCE.isStunned(user)){
            cir.setReturnValue(TypedActionResult.fail((ItemStack) (Object) this));
        }
    }
}
