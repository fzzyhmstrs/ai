package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt;
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {


    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "net/minecraft/enchantment/EnchantmentHelper.set (Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
    private void transferDisenchantNbt(Map<Enchantment, Integer> enchantments, ItemStack stack){
        ItemStack stack2 = this.input.getStack(1);
        NbtCompound nbt2 = stack2.getNbt();
        if (nbt2 != null && nbt2.contains(NbtKeys.DISENCHANT_COUNT.str())){
            NbtCompound nbt = stack.getOrCreateNbt();
            int lvl = Nbt.INSTANCE.readIntNbt(NbtKeys.DISENCHANT_COUNT.str(),nbt2);
            Nbt.INSTANCE.writeIntNbt(NbtKeys.DISENCHANT_COUNT.str(), lvl, nbt);
        }
        EnchantmentHelper.set(enchantments,stack);
    }

}
