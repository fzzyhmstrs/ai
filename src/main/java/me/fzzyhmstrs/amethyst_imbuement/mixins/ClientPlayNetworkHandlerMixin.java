package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "getActiveTotemOfUndying", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z"))
    private static boolean checkForTotemOfAmethyst(ItemStack instance, Item item){
        if (instance.isOf(Items.TOTEM_OF_UNDYING)) return true;
        boolean bl1 = instance.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST());
        bl1 = bl1 && EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(), instance) > 0;
        return bl1;
    }

}
