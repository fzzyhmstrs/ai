package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Redirect(method = "canWalkOnPowderSnow", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z"))
    private static boolean amethyst_imbuement_checkForLightfooted(ItemStack instance, Item item){
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getLIGHTFOOTED(),instance) > 0){
            return true;
        } else {
            return instance.isOf(Items.LEATHER_BOOTS);
        }
    }

}
