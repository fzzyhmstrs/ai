package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE",target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z", ordinal = 1))
    private boolean checkForSniperBow(ItemStack instance, Item item){
        return instance.isOf(Items.CROSSBOW) || instance.isOf(RegisterItem.INSTANCE.getSNIPER_BOW());
    }
}
