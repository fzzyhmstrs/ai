package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem;
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedWardItem;
import me.fzzyhmstrs.amethyst_imbuement.item.scepter.ScepterItem;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnbreakingEnchantment.class)
public abstract class EnchantUnbreakingMixin {

    @Inject(method = "getMinPower", at = @At(value = "HEAD"), cancellable = true)
    public void amethyst_imbuement_getMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        if (level > 3) {
            cir.setReturnValue(21 + (level - 3) * 16);
        }
    }

    @Inject(method = "isAcceptableItem", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_checkScepterItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();
        if (item instanceof ScepterItem || item instanceof ImbuedJewelryItem || item instanceof ImbuedWardItem) {
            cir.setReturnValue(false);
        }
    }
}
