package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    private boolean isSniper = false;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), require = 0)
    private void amethyst_imbuement_checkStackForSniper(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        isSniper = item.isOf(RegisterItem.INSTANCE.getSNIPER_BOW());
    }

    @ModifyArg(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z",ordinal = 1), require = 0)
    private Item amethyst_imbuement_updateIfCheckSniper(Item chkItem){
        if (isSniper){
            return RegisterItem.INSTANCE.getSNIPER_BOW();
        } else {
            return chkItem;
        }
    }
}
